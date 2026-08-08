package de.tuhh.bigdata.a9;

import java.util.Properties;

import org.apache.kafka.common.serialization.StringSerializer;

import de.tuhh.bigdata.a9.adapter.KafkaTaxiEventProducer;
import de.tuhh.bigdata.a9.adapter.SqliteTaxiEventRepository;
import de.tuhh.bigdata.a9.application.TaxiEventService;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import de.tuhh.bigdata.a9.application.TaxiEventRepository;
import de.tuhh.bigdata.a9.application.TaxiEventProducer;

/**
 * Main application class for sending taxi events to Kafka.
 */
public class App {
	public static void main(String[] args) {
		// Configuration
		String topic = "taxi-events";
		String bootstrapServers = "127.0.0.1:9092";
		String sqliteDbUrl = "jdbc:sqlite:tools/database_tool/output/database.db";
		Double timeMultiplier = 0.0;

		// Properties for Kafka Producer
		Properties properties = new Properties();
		properties.put("bootstrap.servers", bootstrapServers);
		properties.put("key.serializer", StringSerializer.class.getName());
		properties.put("value.serializer", KafkaAvroSerializer.class.getName());
		properties.put("schema.registry.url", "http://127.0.0.1:8082");

		// Initialize repository and producer
		TaxiEventRepository repository = new SqliteTaxiEventRepository(sqliteDbUrl);
		TaxiEventProducer producer = new KafkaTaxiEventProducer(properties, topic);

		// Initialize service
		TaxiEventService service = new TaxiEventService(repository, producer, timeMultiplier);

		// Process events
		System.out.println("Processing events...");
		service.processEvents();

		// Close resources
		try {
			producer.close();
			repository.close();
		} catch (Exception e) {
			System.err.println("Error closing resources: " + e.getMessage());
		}
		System.out.println("Finished processing events.");
	}
}
