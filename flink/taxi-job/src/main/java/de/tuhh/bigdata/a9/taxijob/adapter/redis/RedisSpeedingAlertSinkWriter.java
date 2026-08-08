package de.tuhh.bigdata.a9.taxijob.adapter.redis;

import org.apache.flink.api.connector.sink2.SinkWriter;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiSpeedingAlert;
import redis.clients.jedis.AbstractPipeline;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.XAddParams;

/**
 * A sink for writing TaxiSpeedingAlert objects to Redis using streams.
 */
public class RedisSpeedingAlertSinkWriter implements SinkWriter<TaxiSpeedingAlert> {
	private final String streamKey;
	private final Jedis jedis;
	private final AbstractPipeline pipe;

	/**
	 * Constructor for RedisSpeedingAlertSinkWriter.
	 * 
	 * @param redisHost
	 *            the Redis host
	 * @param redisPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index to write to
	 */
	public RedisSpeedingAlertSinkWriter(String redisHost, int redisPort, int redisDb, String streamKey) {
		this.jedis = new Jedis(redisHost, redisPort);
		this.jedis.select(redisDb);
		this.streamKey = streamKey;
		this.pipe = jedis.pipelined();
	}

	/**
	 * Writes a TaxiSpeedingAlert to Redis Streams.
	 * 
	 * @param alert
	 *            the TaxiSpeedingAlert to write
	 * @param context
	 *            the context for writing
	 */
	@Override
	public void write(TaxiSpeedingAlert alert, Context context) {
		try {
			pipe.xadd(streamKey, alert.getHashMap(), XAddParams.xAddParams());
		} catch (Exception e) {
			System.err.println("Failed to publish TaxiSpeedingAlert: " + alert.getTaxiDataEvent().getId());
			e.printStackTrace();
		}
	}

	/**
	 * Flushes the writer and trims the stream to keep the last 1000 records.
	 * 
	 * @param endOfInput
	 *            indicates if this is the end of input
	 */
	@Override
	public void flush(boolean endOfInput) {
		pipe.xtrim(streamKey, 1000, true);
		pipe.sync();
	}

	/**
	 * Closes the writer and releases resources.
	 */
	@Override
	public void close() {
		if (pipe != null) {
			pipe.close();
		}

		if (jedis != null) {
			jedis.close();
		}
	}
}
