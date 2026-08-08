package de.tuhh.bigdata.a9.taxiactivejob.adapter;

import java.io.IOException;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.api.connector.sink2.WriterInitContext;

import de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount.RedisTaxiCountSinkWriter;

/**
 * A sink for writing taxi count data to a Redis database.
 */
public class RedisTaxiCountSink implements Sink<Long> {
	private final String redisHost;
	private final int rediPort;
	private final int redisDb;

	/**
	 * Constructor for RedisTaxiCountSink.
	 * 
	 * @param redisHost
	 *            the Redis host
	 * @param rediPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index for taxi counts
	 */
	public RedisTaxiCountSink(String redisHost, int rediPort, int redisDb) {
		this.redisHost = redisHost;
		this.rediPort = rediPort;
		this.redisDb = redisDb;
	}

	/**
	 * Creates a new SinkWriter for writing taxi count data to Redis.
	 *
	 * @param context
	 *            ignored context parameter
	 * @return a new RedisTaxiCountSinkWriter, configured with the specified Redis
	 *         connection parameters
	 */
	@Override
	public SinkWriter<Long> createWriter(WriterInitContext context) throws IOException {
		return new RedisTaxiCountSinkWriter(redisHost, rediPort, redisDb);
	}
}
