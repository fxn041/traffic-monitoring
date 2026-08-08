package de.tuhh.bigdata.a9.taxijob.application;

import java.time.Duration;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import de.tuhh.bigdata.a9.taxi.domain.TaxiEvent;
import de.tuhh.bigdata.a9.taxijob.domain.TaxiAreaAlert;
import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;
import de.tuhh.bigdata.a9.taxijob.domain.TaxiSpeedingAlert;
import de.tuhh.bigdata.a9.taxijob.domain.ThresholdConfig;

/**
 * Service to set up the Taxi Job.
 */
public class TaxiJobService {
	/**
	 * Sets up the job to process taxi events.
	 *
	 * @param env
	 *            the execution environment
	 * @param kafkaSource
	 *            the Kafka source for TaxiEvent
	 * @param redisTaxiSink
	 *            the sink for TaxiDataEvent
	 * @param redisMetadataSink
	 *            the sink for metadata
	 * @param redisSpeedingAlertSink
	 *            the sink for TaxiSpeedingAlert
	 * @param redisAreaAlertSink
	 *            the sink for TaxiAreaAlert
	 */
	public static void setupJob(StreamExecutionEnvironment env, KafkaSource<TaxiEvent> kafkaSource,
			Sink<TaxiDataEvent> redisTaxiSink, Sink<TaxiDataEvent> redisMetadataSink,
			Sink<TaxiSpeedingAlert> redisSpeedingAlertSink, Sink<TaxiAreaAlert> redisAreaAlertSink,
			ThresholdConfig jobConfig) {
		// Create the Kafka source stream
		final DataStream<TaxiEvent> kafkaStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(),
				"Kafka Source");

		// Parse the stream to TaxiDataEvent
		final DataStream<TaxiDataEvent> taxiDataStream = kafkaStream.map(event -> new TaxiDataEvent(event.getId(),
				event.getTimestamp(), event.getDateTime(), event.getLatitude(), event.getLongitude()))
				.name("Taxi Data Mapper");

		// Filter the stream and drop events that are not in range
		final DataStream<TaxiDataEvent> filteredTaxiDataStream = taxiDataStream
				.filter(new TaxiRangeFilter(jobConfig.getTaxiAreaMaxRadiusThreshold())).name("Taxi Data Filter");

		// Key the stream by taxi ID
		final KeyedStream<TaxiDataEvent, Integer> keyedTaxiDataStream = filteredTaxiDataStream
				.keyBy(event -> event.getId());

		// Reduce the stream to calculate attributes
		final DataStream<TaxiDataEvent> infoTaxiDataStream = keyedTaxiDataStream.reduce(
				new TaxiCalculator(jobConfig.getTaxiMaxSpeedThreshold(), jobConfig.getTaxiAreaAlertRadiusThreshold()))
				.name("Taxi Data Calculator");

		// Filter the stream to generate speeding alerts
		final double speed_limit = jobConfig.getTaxiSpeedingAlertThreshold();
		final DataStream<TaxiSpeedingAlert> speedingAlerts = infoTaxiDataStream
				.filter(new TaxiSpeedingDetector(speed_limit)).map(event -> new TaxiSpeedingAlert(event, speed_limit))
				.name("Speeding Alert Generator");

		// Key the stream by taxi ID
		final KeyedStream<TaxiDataEvent, Integer> keyedinfoTaxiDataStream = infoTaxiDataStream
				.keyBy(event -> event.getId());

		// Window the stream to emit every 5 seconds
		WindowedStream<TaxiDataEvent, Integer, TimeWindow> windowedTaxiDataStream = keyedinfoTaxiDataStream
				.window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(5)));

		// Reduce the stream to calculate the segment distance
		final DataStream<TaxiDataEvent> reducedTaxiDataStream = windowedTaxiDataStream.reduce(new DistanceReduce())
				.name("Taxi Data Distance Reduce");

		// Filter the stream to generate area alerts
		final double taxiAreaAlertRadiusThreshold = jobConfig.getTaxiAreaAlertRadiusThreshold();
		final DataStream<TaxiAreaAlert> areaAlertStream = infoTaxiDataStream
				.filter(new TaxiAreaDetector(taxiAreaAlertRadiusThreshold))
				.map(event -> new TaxiAreaAlert(event, taxiAreaAlertRadiusThreshold)).name("Area Alert Generator");

		// Sink the taxi data to Redis
		reducedTaxiDataStream.sinkTo(redisTaxiSink).name("Redis Taxi Sink");

		// Sink the metadata (total distance) to Redis
		reducedTaxiDataStream.sinkTo(redisMetadataSink).name("Redis Metadata Sink (Total Distance)");

		// Sink the speeding events to pub/sub Redis
		speedingAlerts.sinkTo(redisSpeedingAlertSink).name("Redis Speeding Alert Sink");

		// Sink the area alerts to pub/sub Redis
		areaAlertStream.sinkTo(redisAreaAlertSink).name("Redis Area Alert Sink");
	}
}
