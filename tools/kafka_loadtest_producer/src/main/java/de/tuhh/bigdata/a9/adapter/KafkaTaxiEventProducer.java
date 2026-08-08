package de.tuhh.bigdata.a9.adapter;

import de.tuhh.bigdata.a9.application.TaxiEventProducer;
import de.tuhh.bigdata.a9.domain.TaxiInputEvent;
import de.tuhh.bigdata.a9.taxi.domain.TaxiEvent;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * KafkaTaxiEventProducer is a class that implements the TaxiEventProducer
 * interface and is responsible for sending TaxiEvent objects to a Kafka topic.
 */
public class KafkaTaxiEventProducer implements TaxiEventProducer {
	private final KafkaProducer<String, TaxiEvent> producer;
	private final String topic;

	/**
	 * Constructor for KafkaTaxiEventProducer.
	 *
	 * @param properties
	 *            The properties for the Kafka producer, including bootstrap servers
	 *            and serializers.
	 * @param topic
	 *            The Kafka topic to which events will be sent.
	 */
	public KafkaTaxiEventProducer(Properties properties, String topic) {
		this.producer = new KafkaProducer<>(properties);
		this.topic = topic;
	}

	/**
	 * Sends a TaxiEvent to the Kafka topic.
	 *
	 * @param event
	 *            The TaxiEvent to be sent.
	 */
	@Override
	public void send(TaxiInputEvent event) {
		TaxiEvent taxiEvent = new TaxiEvent(event.taxiId(), event.timestamp(), event.datetime(), event.latitude(), event.longitude());
		producer.send(new ProducerRecord<>(topic, String.valueOf(taxiEvent.getId()), taxiEvent), this::callback);
	}

	/**
	 * Closes the Kafka producer. This method should be called when the producer is
	 * no longer needed to release resources.
	 */
	@Override
	public void close() {
		producer.close();
	}

	/**
	 * Callback method to handle the result of the send operation.
	 *
	 * @param metadata
	 *            The metadata of the sent record.
	 * @param exception
	 *            The exception if the send operation failed, null otherwise.
	 */
	private void callback(RecordMetadata metadata, Exception exception) {
		if (exception != null) {
			System.err.println("Error sending message: " + exception.getMessage());
		}
	}
}
