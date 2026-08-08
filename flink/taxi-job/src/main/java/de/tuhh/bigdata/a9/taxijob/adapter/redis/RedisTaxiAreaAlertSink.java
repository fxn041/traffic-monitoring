package de.tuhh.bigdata.a9.taxijob.adapter.redis;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.api.connector.sink2.WriterInitContext;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiAreaAlert;

/**
 * A sink for writing TaxiAreaAlert objects to Redis.
 */
public class RedisTaxiAreaAlertSink implements Sink<TaxiAreaAlert> {

	private final String redisHost;
	private final int redisPort;
	private final int redisDb;
	private final String streamKey;

	/**
	 * Constructor for RedisTaxiAreaAlertSink.
	 *
	 * @param redisHost
	 *            the Redis host
	 * @param redisPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index to write to
	 */
	public RedisTaxiAreaAlertSink(String redisHost, int redisPort, int redisDb, String streamKey) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.redisDb = redisDb;
		this.streamKey = streamKey;
	}

	/**
	 * Creates a SinkWriter for writing TaxiAreaAlert objects to Redis.
	 *
	 * @param context
	 *            the WriterInitContext containing initialization information
	 * 
	 * @return a SinkWriter that writes TaxiAreaAlert objects to Redis
	 */
	@Override
	public SinkWriter<TaxiAreaAlert> createWriter(WriterInitContext context) {
		return new RedisTaxiAreaAlertSinkWriter(redisHost, redisPort, redisDb, streamKey);
	}
}
