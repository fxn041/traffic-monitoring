package de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.flink.api.connector.source.ReaderOutput;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.core.io.InputStatus;

import redis.clients.jedis.Jedis;

/**
 * A SourceReader for reading active taxi counts from a Redis database. This
 * reader polls the Redis database at regular intervals to get the count of
 * active taxis.
 */
public class RedisTaxiCountSourceReader implements SourceReader<Long, RedisTaxiCountSplit> {

	private boolean hasSplit = false;
	private Jedis jedis;
	private String redisHost;
	private int redisPort;
	private int redisDb;

	private volatile CompletableFuture<Void> availableFuture = new CompletableFuture<>();
	private volatile long nextPollTime = 0;

	private long pollingIntervalMs = 5000;

	/**
	 * Constructor for RedisTaxiCountSourceReader.
	 *
	 * @param redisHost
	 *            the Redis host
	 * @param redisPort
	 *            the Redis port
	 * @param redisDb
	 *            the Redis database index for taxis to count
	 */
	public RedisTaxiCountSourceReader(String redisHost, int redisPort, int redisDb, int redisPollingInterval) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.redisDb = redisDb;
		this.pollingIntervalMs = redisPollingInterval;
	}

	/**
	 * Closes the Redis connection.
	 */
	@Override
	public void close() {
		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * Starts the reader by connecting to the Redis database and scheduling the
	 * first poll.
	 */
	@Override
	public void start() {
		jedis = new Jedis(redisHost, redisPort);
		jedis.select(redisDb);
		scheduleNextPoll();
	}

	/**
	 * Polls the next count of active taxis from Redis. If no split is available or
	 * the next poll time has not been reached, it returns NOTHING_AVAILABLE.
	 * Otherwise, it collects the count and schedules the next poll.
	 * 
	 * @param output
	 *            the output collector for the count of active taxis
	 * @return InputStatus.NOTHING_AVAILABLE to wait for the next scheduled poll
	 */
	@Override
	public InputStatus pollNext(ReaderOutput<Long> output) {
		long now = System.currentTimeMillis();
		if (!hasSplit || now < nextPollTime) {
			return InputStatus.NOTHING_AVAILABLE;
		}
		long taxiCount = jedis.dbSize();
		output.collect(taxiCount);
		scheduleNextPoll();
		return InputStatus.NOTHING_AVAILABLE;
	}

	/**
	 * Schedules the next poll by setting the next poll time to the current time
	 * plus the poll interval. It also creates a new CompletableFuture that will be
	 * completed after the poll interval.
	 */
	private void scheduleNextPoll() {
		nextPollTime = System.currentTimeMillis() + pollingIntervalMs;
		availableFuture = new CompletableFuture<>();
		new Thread(() -> {
			try {
				Thread.sleep(pollingIntervalMs);
			} catch (InterruptedException ignored) {
				// Ignore interrupt
			}
			availableFuture.complete(null);
		}).start();
	}

	/**
	 * Returns a snapshot of the current state of the reader.
	 */
	@Override
	public List<RedisTaxiCountSplit> snapshotState(long checkpointId) {
		return Collections.singletonList(new RedisTaxiCountSplit());
	}

	/**
	 * Checks whether the reader is available for polling.
	 * 
	 * @return a CompletableFuture that will be completed when the reader is
	 *         available
	 */
	@Override
	public CompletableFuture<Void> isAvailable() {
		return availableFuture;
	}

	/**
	 * Adds splits to the reader.
	 */
	@Override
	public void addSplits(List<RedisTaxiCountSplit> splits) {
		hasSplit = true;
	}

	/**
	 * Notifies the reader that there are no more splits available.
	 */
	@Override
	public void notifyNoMoreSplits() {
		hasSplit = false;
	}
}
