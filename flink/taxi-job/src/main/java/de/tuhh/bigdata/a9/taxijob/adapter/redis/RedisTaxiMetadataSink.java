package de.tuhh.bigdata.a9.taxijob.adapter.redis;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.api.connector.sink2.WriterInitContext;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;

/**
 * A sink for writing taxi metadata to a Redis database.
 */
public class RedisTaxiMetadataSink implements Sink<TaxiDataEvent> {

	private final String redisHost;
	private final int redisPort;
	private final int redisDb;

	/**
	 * Constructor for RedisTaxiMetadataSink.
	 * 
	 * @param redisHost
	 *            the Redis host
	 * @param redisPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index for metadata
	 */
	public RedisTaxiMetadataSink(String redisHost, int redisPort, int redisDb) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.redisDb = redisDb;
	}

	/**
	 * Creates a new SinkWriter for writing TaxiDataEvent objects to Redis.
	 *
	 * @param context
	 *            ignored context parameter
	 * @return a new RedisTaxiMetadataSinkWriter, configured with the specified
	 *         Redis connection parameters
	 */
	@Override
	public SinkWriter<TaxiDataEvent> createWriter(WriterInitContext context) {
		return new RedisTaxiMetadataSinkWriter(redisHost, redisPort, redisDb);
	}
}
