package de.tuhh.bigdata.a9.taxiactivejob.adapter.redistaxicount;

import org.apache.flink.core.io.SimpleVersionedSerializer;

/**
 * Serializer for RedisTaxiCountSplit.
 */
public class RedisTaxiCountSplitSerializer implements SimpleVersionedSerializer<RedisTaxiCountSplit> {

	private static final int VERSION = 1;

	/**
	 * Returns the version of this serializer.
	 *
	 * @return the version number
	 */
	@Override
	public int getVersion() {
		return VERSION;
	}

	/**
	 * Serializes the RedisTaxiCountSplit object to a byte array.
	 *
	 * @param obj
	 *            the RedisTaxiCountSplit object to serialize
	 * @return a byte array representing the serialized object
	 */
	@Override
	public byte[] serialize(RedisTaxiCountSplit obj) {
		return new byte[0];
	}

	/**
	 * Deserializes a byte array to a RedisTaxiCountSplit object.
	 *
	 * @param version
	 *            ignored version parameter
	 * @param serialized
	 *            the byte array to deserialize
	 * @return a RedisTaxiCountSplit object
	 */
	@Override
	public RedisTaxiCountSplit deserialize(int version, byte[] serialized) {
		return new RedisTaxiCountSplit();
	}
}
