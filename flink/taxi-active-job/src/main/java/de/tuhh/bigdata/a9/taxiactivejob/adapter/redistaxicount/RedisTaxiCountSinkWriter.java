package de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount;

import org.apache.flink.api.connector.sink2.SinkWriter;

import redis.clients.jedis.Jedis;

/**
 * A sink writer for writing taxi count data to a Redis database. This writer
 * updates the count of active taxis in Redis.
 */
public class RedisTaxiCountSinkWriter implements SinkWriter<Long> {

	private static final String TAXI_COUNT_KEY = "taxi_count";

	private final Jedis jedis;

	/**
	 * Constructor for RedisTaxiCountSinkWriter.
	 * 
	 * @param redisHost
	 *            the Redis host
	 * @param redisPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index for taxi counts
	 */
	public RedisTaxiCountSinkWriter(String redisHost, int redisPort, int redisDb) {
		this.jedis = new Jedis(redisHost, redisPort);
		this.jedis.select(redisDb);
		this.jedis.set(TAXI_COUNT_KEY, "0");
	}

	/**
	 * Writes the current count of active taxis to Redis.
	 * 
	 * @param element
	 *            the count of active taxis
	 * @param context
	 *            ignored context parameter
	 */
	@Override
	public void write(Long element, Context context) {
		jedis.set(TAXI_COUNT_KEY, element.toString());
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

	/**
	 * Closes the Redis connection.
	 */
	@Override
	public void close() throws Exception {
		if (jedis != null) {
			jedis.close();
		}
	}
}
