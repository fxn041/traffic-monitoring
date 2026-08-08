package de.tuhh.bigdata.a9.taxijob.adapter.redis;

import org.apache.flink.api.connector.sink2.SinkWriter;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;
import redis.clients.jedis.Jedis;

/**
 * A sink writer for writing taxi metadata to a Redis database.
 */
public class RedisTaxiMetadataSinkWriter implements SinkWriter<TaxiDataEvent> {

	private final Jedis jedis;

	/**
	 * Constructor for RedisTaxiMetadataSinkWriter.
	 * 
	 * @param redisHost
	 *            the Redis host
	 * @param redisPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index for metadata
	 */
	public RedisTaxiMetadataSinkWriter(String redisHost, int redisPort, int redisDb) {
		this.jedis = new Jedis(redisHost, redisPort);
		this.jedis.select(redisDb);
	}

	/**
	 * Closes the Redis connection.
	 */
	@Override
	public void close() throws Exception {
		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * Increments the total distance in Redis by the segement distance given in the
	 * TaxiDataEvent.
	 * 
	 * @param element
	 *            the TaxiDataEvent containing the segment distance
	 * @param context
	 *            ignored context parameter
	 */
	@Override
	public void write(TaxiDataEvent element, Context context) {
		if (element.getSegmentDistance() > 0.0) {
			jedis.incrByFloat("total_distance", element.getSegmentDistance());
		}
	}

	/**
	 * This method is not used in this implementation.
	 * 
	 * @param endOfInput
	 *            ignored endOfInput parameter
	 */
	@Override
	public void flush(boolean endOfInput) {
		// No flush action needed for Redis
	}
}
