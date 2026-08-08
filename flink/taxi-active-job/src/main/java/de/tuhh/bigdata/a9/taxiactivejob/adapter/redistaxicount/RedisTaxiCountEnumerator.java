package de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;

/**
 * Enumerator for managing RedisTaxiCountSplits. This enumerator assigns a
 * single split to the first subtask that requests it.
 */
public class RedisTaxiCountEnumerator implements SplitEnumerator<RedisTaxiCountSplit, RedisTaxiCountSplit> {

	private final SplitEnumeratorContext<RedisTaxiCountSplit> context;
	private boolean assigned = false;

	/**
	 * Constructor for RedisTaxiCountEnumerator.
	 * 
	 * @param context
	 *            the SplitEnumeratorContext for managing splits
	 */
	public RedisTaxiCountEnumerator(SplitEnumeratorContext<RedisTaxiCountSplit> context) {
		this.context = context;
	}

	/**
	 * Starts the enumerator.
	 */
	@Override
	public void start() {
		// Empty implementation for starting the enumerator
	}

	/**
	 * Handles split requests from subtasks.
	 * 
	 * @param subtaskId
	 *            the ID of the subtask requesting the split
	 * @param requesterHostname
	 *            ignored hostname of the requester
	 */
	@Override
	public void handleSplitRequest(int subtaskId, @Nullable String requesterHostname) {
		// Assign the single split to the first requesting subtask
		if (!assigned) {
			context.assignSplit(new RedisTaxiCountSplit(), subtaskId);
			assigned = true;
		}
	}

	/**
	 * Adds splits back to the enumerator.
	 * 
	 * @param splits
	 *            the list of splits to add back
	 * @param subtaskId
	 *            the ID of the subtask to which the splits should be assigned
	 */
	@Override
	public void addSplitsBack(List<RedisTaxiCountSplit> splits, int subtaskId) {
		// Single split, just reassign if needed
		if (!assigned && splits != null && !splits.isEmpty()) {
			context.assignSplit(splits.get(0), subtaskId);
			assigned = true;
		}
	}

	/**
	 * Adds a reader to the enumerator.
	 * 
	 * @param subtaskId
	 *            the ID of the subtask to add as a reader
	 */
	@Override
	public void addReader(int subtaskId) {
		// Assign the split if not already assigned
		handleSplitRequest(subtaskId, null);
	}

	/**
	 * Handles the snapshot of the enumerator state.
	 * 
	 * @param checkpointId
	 *            ignored checkpoint ID
	 * @return null, as there is no state to snapshot
	 */
	@Override
	public RedisTaxiCountSplit snapshotState(long checkpointId) {
		// No state to snapshot
		return null;
	}

	/**
	 * Closes the enumerator.
	 */
	@Override
	public void close() {
		// Empty implementation for closing the enumerator
	}
}
