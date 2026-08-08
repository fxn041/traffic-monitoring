import sqlite3
from pathlib import Path
from confluent_kafka import SerializingProducer
from confluent_kafka.schema_registry import SchemaRegistryClient
from confluent_kafka.schema_registry.avro import AvroSerializer
from typing import Any, Dict,Tuple, Optional
import json
import yaml
import time
import queue
import threading
import os

def query_events_by_time_window(cursor: sqlite3.Cursor, start_time: Optional[int], time_multiplier: int, max_timestamp: int) -> Tuple[list[Any], Optional[int]]:
    # First call: initialize with the earliest timestamp
    if start_time is None:
        cursor.execute("SELECT MIN(timestamp) FROM taxi_data")
        start_time = cursor.fetchone()[0]
        if start_time is None:
            return [], None  # No data at all

    end_time = start_time + time_multiplier

    # Check if we've exceeded the data range
    if start_time > max_timestamp:
        return [], None

    # Get all events in the current time window
    # For optimization: CREATE INDEX IF NOT EXISTS idx_timestamp ON taxi_data(timestamp);
    events_query = """
    SELECT taxi_id, timestamp, datetime, longitude, latitude 
    FROM taxi_data 
    WHERE timestamp >= ? AND timestamp < ?
    ORDER BY timestamp ASC
    """
    events = cursor.execute(events_query, (start_time, end_time)).fetchall()

    # Always return the next window start (current end_time)
    
    return events, end_time

def get_max_timestamp(cursor: sqlite3.Cursor) -> int:
    return cursor.execute("SELECT MAX(timestamp) FROM taxi_data").fetchone()[0]

def acked(err, msg):
    t = time.strftime("%H:%M:%S")
    key = msg.key().decode() if msg.key() else None
    ts = json.loads(msg.value())['timestamp']
    if err:
        print(f"[{t}] → FAILED key={key} ts={ts} err={err}")

def producer_thread(event_queue: queue.Queue, kafka_config: Dict, stop_event: threading.Event):


    producer = SerializingProducer(kafka_config)
    
    while not stop_event.is_set() or not event_queue.empty():
        try:
            start_time = time.time()
            batch = event_queue.get(timeout=1)
            events = batch['events']
            
            for idx, event in enumerate(events):
                if idx % 100000 == 0:
                    producer.flush()

                taxi_id, timestamp, datetime, longitude, latitude = event
                
                payload = {
                    'id': taxi_id,
                    'timestamp': timestamp,
                    'dateTime': datetime,
                    'longitude': longitude,
                    'latitude': latitude
                }
                
                producer.produce(
                    topic='taxi-events',
                    key=str(taxi_id),
                    value=payload
                )
                                
            # Make sure all messages are delivered
            producer.flush()
            
        
            # Mark this time window as processed
            event_queue.task_done()
            
            elapsed = time.time() - start_time
            if elapsed <= 1:
                time.sleep(1 - elapsed)

                        
        except queue.Empty:
            continue

def key_serializer(key, ctx):
    return str(key).encode("utf-8") if key is not None else None

def main():
    """
    Main function that:
    1. Loads configuration
    2. Sets up the queue and threads
    3. Processes time windows and adds them to the queue
    4. Waits for all events to be processed
    """
    # Load configuration
    with open('config.yaml') as f:
        config = yaml.safe_load(f)
    
    kafka_config = config.get('kafka', {})
    producer_config = config.get('producer', {})
    schema_registry_config = config.get('schema_registry', {})
    
    # Get time multiplier from config (default is 1)
    time_multiplier = producer_config.get('time_multiplier', 1)

    # Load the avro schema
    with open('/avro/TaxiEvent.avsc') as f:
        schema_str = f.read()

    # Configure schema registry
    schema_registry_client = SchemaRegistryClient(schema_registry_config)

    # Create Avro serializer
    avro_serializer = AvroSerializer(
        schema_registry_client,
        schema_str
    )

    # Update Kafka config with the Avro serializer
    kafka_config.update({
        'key.serializer': key_serializer,
        'value.serializer': avro_serializer
    })
    
    # Connect to the database
    db_path = Path(os.getenv('DATABASE_PATH'))
    con = sqlite3.connect(db_path)
    cur = con.cursor()
    
    # Get the max timestamp to know the end of the data
    max_timestamp = get_max_timestamp(cur)
    
    # Create a queue to hold time windows of events
    event_queue = queue.Queue(maxsize=500) 
    
    # Create a stop event for signaling threads
    stop_event = threading.Event()
    
    # Start the producer thread
    producer_thread_instance = threading.Thread(
        target=producer_thread, 
        args=(event_queue, kafka_config, stop_event)
    )
    producer_thread_instance.daemon = True
    producer_thread_instance.start()
    
    current_start_time = None
    
    try:
        # Process the data in time windows based on the multiplier

        while True:
            # Get events for the current time window
            events, next_start_time = query_events_by_time_window(cur, current_start_time, time_multiplier, max_timestamp)
            
            # If no more events, we're done
            if next_start_time is None:
                break                
            
            # Add the time window to the queue
            event_queue.put({'events': events})
            
            current_start_time = next_start_time
       
        # Wait for all events to be processed
        event_queue.join()
        
    except KeyboardInterrupt:
        print("Shutting down...")
        
    finally:
        # Signal threads to stop and wait for them
        stop_event.set()
        producer_thread_instance.join(timeout=5)
        
        # Close the database connection
        con.close()

if __name__ == '__main__':
    main()