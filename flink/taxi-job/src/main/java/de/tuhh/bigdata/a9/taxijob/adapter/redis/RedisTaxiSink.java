package de.tuhh.bigdata.a9.taxijob.adapter.redis;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.api.connector.sink2.WriterInitContext;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;

/**
 * A sink for writing taxi data events to a Redis database.
 */
public class RedisTaxiSink implements Sink<TaxiDataEvent> {

	private final String redisHost;
	private final int redisPort;
	private final int redisDb;
	private final int ttlSeconds;

	/**
	 * Constructor for RedisTaxiSink.
	 * 
	 * @param redisHost
	 *            the Redis host
	 * @param redisPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index for taxi data
	 * @param ttlSeconds
	 *            the time-to-live in seconds for the Redis keys
	 */
	public RedisTaxiSink(String redisHost, int redisPort, int redisDb, int ttlSeconds) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.redisDb = redisDb;
		this.ttlSeconds = ttlSeconds;
	}

	/**
	 * Creates a new SinkWriter for writing TaxiDataEvent objects to Redis.
	 *
	 * @param context
	 *            ignored context parameter
	 * @return a new RedisTaxiSinkWriter, configured with the specified Redis
	 *         connection parameters
	 */
	@Override
	public SinkWriter<TaxiDataEvent> createWriter(WriterInitContext context) {
		return new RedisTaxiSinkWriter(redisHost, redisPort, redisDb, ttlSeconds);
	}
}
