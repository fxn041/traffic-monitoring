package de.tuhh.bigdata.a9.taxijob.adapter.redis;

import org.apache.flink.api.connector.sink2.SinkWriter;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;
import redis.clients.jedis.AbstractPipeline;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.HSetExParams;

/**
 * A sink writer for writing taxi data events to a Redis database.
 */
public class RedisTaxiSinkWriter implements SinkWriter<TaxiDataEvent> {

	private final HSetExParams hSetExParams;
	private final Jedis jedis;
	private final AbstractPipeline pipe;

	/**
	 * Constructor for RedisTaxiSinkWriter.
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
	public RedisTaxiSinkWriter(String redisHost, int redisPort, int redisDb, int ttlSeconds) {
		this.jedis = new Jedis(redisHost, redisPort);
		this.jedis.select(redisDb);
		this.hSetExParams = new HSetExParams();
		this.hSetExParams.ex(ttlSeconds);
		this.pipe = jedis.pipelined();
	}

	/**
	 * Closes the Redis connection.
	 */
	@Override
	public void close() throws Exception {
		if (pipe != null) {
			pipe.close();
		}

		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * Writes a TaxiDataEvent to Redis using a pipeline.
	 * 
	 * @param element
	 *            the TaxiDataEvent to write
	 * @param context
	 *            ignored context parameter
	 */
	@Override
	public void write(TaxiDataEvent element, Context context) {
		pipe.hsetex(element.getId().toString(), this.hSetExParams, element.getAttributeSet());
	}

	/**
	 * Flushes the pipeline to ensure all commands are sent to Redis.
	 * 
	 * @param endOfInput
	 *            ignored endOfInput parameter
	 */
	@Override
	public void flush(boolean endOfInput) {
		this.pipe.sync();
	}
}
