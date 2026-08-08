package de.tuhh.bigdata.a9.taxiactivejob.adapter;

import org.apache.flink.api.connector.source.Boundedness;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;
import org.apache.flink.core.io.SimpleVersionedSerializer;

import de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount.RedisTaxiCountEnumerator;
import de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount.RedisTaxiCountSourceReader;
import de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount.RedisTaxiCountSplit;
import de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount.RedisTaxiCountSplitSerializer;

/**
 * A source for reading active taxi counts from a Redis database. This source
 * continuously reads the count of active taxis from Redis.
 */
public class RedisTaxiCountSource implements Source<Long, RedisTaxiCountSplit, RedisTaxiCountSplit> {

	private String redisHost;
	private int redisPort;
	private int redisDb;
	private int redisPollingInterval;

	/**
	 * Constructor for RedisTaxiCountSource.
	 * 
	 * @param redisHost
	 *            the Redis host
	 * @param redisPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index for taxis to count
	 */
	public RedisTaxiCountSource(String redisHost, int redisPort, int redisDb, int redisPollingInterval) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.redisDb = redisDb;
		this.redisPollingInterval = redisPollingInterval;
	}

	/**
	 * Creates a new SourceReader for reading active taxi counts from Redis.
	 *
	 * @param readerContext
	 *            ignored context parameter
	 * @return a new RedisTaxiCountSourceReader, configured with the specified Redis
	 *         connection parameters
	 */
	@Override
	public SourceReader<Long, RedisTaxiCountSplit> createReader(SourceReaderContext readerContext) {
		return new RedisTaxiCountSourceReader(redisHost, redisPort, redisDb, redisPollingInterval);
	}

	/**
	 * Returns the boundedness of this source.
	 */
	@Override
	public Boundedness getBoundedness() {
		return Boundedness.CONTINUOUS_UNBOUNDED;
	}

	/**
	 * Creates a new SplitEnumerator for managing RedisTaxiCountSplits.
	 */
	@Override
	public SplitEnumerator<RedisTaxiCountSplit, RedisTaxiCountSplit> createEnumerator(
			SplitEnumeratorContext<RedisTaxiCountSplit> enumContext) {
		return new RedisTaxiCountEnumerator(enumContext);
	}

	/**
	 * Restores the enumerator from a checkpoint.
	 *
	 * @param enumContext
	 *            the context for the enumerator
	 * @param checkpoint
	 *            ignored checkpoint parameter
	 * @return a new RedisTaxiCountEnumerator
	 */
	@Override
	public SplitEnumerator<RedisTaxiCountSplit, RedisTaxiCountSplit> restoreEnumerator(
			SplitEnumeratorContext<RedisTaxiCountSplit> enumContext, RedisTaxiCountSplit checkpoint) {
		return new RedisTaxiCountEnumerator(enumContext);
	}

	/**
	 * Returns the serializer for RedisTaxiCountSplit objects.
	 */
	@Override
	public SimpleVersionedSerializer<RedisTaxiCountSplit> getSplitSerializer() {
		return new RedisTaxiCountSplitSerializer();
	}

	/**
	 * Returns the serializer for RedisTaxiCountSplit objects used for enumerator
	 * checkpoints.
	 */
	@Override
	public SimpleVersionedSerializer<RedisTaxiCountSplit> getEnumeratorCheckpointSerializer() {
		return new RedisTaxiCountSplitSerializer();
	}
}
