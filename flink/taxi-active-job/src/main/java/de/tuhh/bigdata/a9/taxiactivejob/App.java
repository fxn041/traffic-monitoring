package de.tuhh.bigdata.a9.taxiactivejob;

import java.io.FileInputStream;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import de.tuhh.bigdata.a9.taxiactivejob.adapter.RedisTaxiCountSink;
import de.tuhh.bigdata.a9.taxiactivejob.adapter.RedisTaxiCountSource;
import de.tuhh.bigdata.a9.taxiactivejob.adapter.config.AppConfig;
import de.tuhh.bigdata.a9.taxiactivejob.application.TaxiActiveJobService;

/**
 * Main application class for the Taxi Active Job.
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
			if (config == null || config.getRedis() == null || config.getJob() == null) {
				System.err.println("Configuration not found or invalid.");
				return;
			}

			// Create the source
			Source<Long, ? extends SourceSplit, ?> redisSource = new RedisTaxiCountSource(config.getRedis().getHost(),
					config.getRedis().getPort(), config.getRedis().getTaxiDb(),
					config.getJob().getTaxiActivePollingIntervalMs());

			// Create the sink
			Sink<Long> redisSink = new RedisTaxiCountSink(config.getRedis().getHost(), config.getRedis().getPort(),
					config.getRedis().getMetadataDb());

			// Setup the job
			final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			TaxiActiveJobService.setupJob(env, redisSource, redisSink);
			env.execute("Taxi Active Job");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
