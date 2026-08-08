package de.tuhh.bigdata.a9.taxiactivejob.application;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Service to set up the Taxi Active Job. This job counts active taxis from a
 * source and writes the count to a sink.
 */
public class TaxiActiveJobService {
	/**
	 * Sets up the job to count active taxis.
	 * 
	 * @param env
	 *            the execution environment
	 * @param source
	 *            the source for counting active taxis
	 * @param sink
	 *            the sink to write the active taxi count
	 */
	public static void setupJob(StreamExecutionEnvironment env, Source<Long, ? extends SourceSplit, ?> source,
			Sink<Long> sink) {
		DataStream<Long> taxiCountStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "TaxiCountSource");
		taxiCountStream.sinkTo(sink);
	}
}
