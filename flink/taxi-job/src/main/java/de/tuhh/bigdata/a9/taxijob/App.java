package de.tuhh.bigdata.a9.taxijob;

import java.io.FileInputStream;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.DumperOptions;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.LoaderOptions;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.constructor.Constructor;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.representer.Representer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import de.tuhh.bigdata.a9.taxi.domain.TaxiEvent;
import de.tuhh.bigdata.a9.taxijob.adapter.config.AppConfig;
import de.tuhh.bigdata.a9.taxijob.adapter.redis.RedisSpeedingAlertSink;
import de.tuhh.bigdata.a9.taxijob.adapter.redis.RedisTaxiAreaAlertSink;
import de.tuhh.bigdata.a9.taxijob.adapter.redis.RedisTaxiMetadataSink;
import de.tuhh.bigdata.a9.taxijob.adapter.redis.RedisTaxiSink;
import de.tuhh.bigdata.a9.taxijob.application.TaxiJobService;
import de.tuhh.bigdata.a9.taxijob.domain.TaxiAreaAlert;
import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;
import de.tuhh.bigdata.a9.taxijob.domain.TaxiSpeedingAlert;
import de.tuhh.bigdata.a9.taxijob.domain.ThresholdConfig;

/**
 * Main application class for the Taxi Job.
 */
public class App {
	public static void main(String[] args) {
		// Load configuration
		DumperOptions dumperOptions = new DumperOptions();
		Representer representer = new Representer(dumperOptions);
		representer.getPropertyUtils().setSkipMissingProperties(true);
		Yaml yaml = new Yaml(new Constructor(AppConfig.class, new LoaderOptions()), representer, dumperOptions);
		AppConfig config;
		try {
			config = yaml.load(new FileInputStream("/config.yaml"));
			if (config == null || config.getKafka() == null || config.getRedis() == null || config.getJob() == null) {
				System.err.println("Configuration not found or invalid.");
				return;
			}

			// Create the Kafka source
			KafkaSource<TaxiEvent> kafkaSource = KafkaSource.<TaxiEvent>builder()
					.setBootstrapServers(config.getKafka().getBootstrapServers())
					.setTopics(config.getKafka().getTopic()).setGroupId(config.getKafka().getGroupId())
					.setStartingOffsets(OffsetsInitializer.earliest())
					.setValueOnlyDeserializer(ConfluentRegistryAvroDeserializationSchema.forSpecific(TaxiEvent.class,
							config.getKafka().getSchemaRegistryUrl()))
					.build();

			// Create the Redis taxi sink
			Sink<TaxiDataEvent> redisTaxiSink = new RedisTaxiSink(config.getRedis().getHost(),
					config.getRedis().getPort(), config.getRedis().getTaxiDb(), config.getRedis().getTtl());

			// Create the Redis metadata sink
			Sink<TaxiDataEvent> redisMetadataSink = new RedisTaxiMetadataSink(config.getRedis().getHost(),
					config.getRedis().getPort(), config.getRedis().getMetadataDb());

			// Create the Redis speeding alert sink
			Sink<TaxiSpeedingAlert> redisSpeedingAlertSink = new RedisSpeedingAlertSink(config.getRedis().getHost(),
					config.getRedis().getPort(), config.getRedis().getMetadataDb(),
					config.getRedis().getSpeedingAlertsStreamKey());

			// Create the Redis area alert sink
			Sink<TaxiAreaAlert> redisAreaAlertSink = new RedisTaxiAreaAlertSink(config.getRedis().getHost(),
					config.getRedis().getPort(), config.getRedis().getMetadataDb(),
					config.getRedis().getAreaAlertsStreamKey());

			// Setup the job
			final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			env.setParallelism(8);
			env.enableCheckpointing(1000);

			ThresholdConfig thresholdConfig = new ThresholdConfig(config.getJob().getTaxiSpeedingAlertThreshold(),
					config.getJob().getTaxiMaxSpeedThreshold(), config.getJob().getTaxiAreaAlertRadiusThreshold(),
					config.getJob().getTaxiAreaMaxRadiusThreshold());
			TaxiJobService.setupJob(env, kafkaSource, redisTaxiSink, redisMetadataSink, redisSpeedingAlertSink,
					redisAreaAlertSink, thresholdConfig);

			env.execute("Taxi Job");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
