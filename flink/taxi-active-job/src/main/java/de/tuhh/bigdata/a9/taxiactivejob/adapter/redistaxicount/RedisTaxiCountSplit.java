package de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount;

import org.apache.flink.api.connector.source.SourceSplit;

/**
 * Represents a split for the Redis Taxi Count.
 */
public class RedisTaxiCountSplit implements SourceSplit {

	public static final String DEFAULT_SPLIT_ID = "redis-taxi-count-split";

	/**
	 * Returns the ID of this split.
	 */
	@Override
	public String splitId() {
		return DEFAULT_SPLIT_ID;
	}
}
