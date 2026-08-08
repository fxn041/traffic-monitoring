package de.tuhh.bigdata.a9.taxijob.application;

import org.apache.flink.api.common.functions.ReduceFunction;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;

/**
 * Reduce function to calculate the segment distance for taxi data events and
 * only return the last event.
 */
public class DistanceReduce implements ReduceFunction<TaxiDataEvent> {

	/**
	 * Reduces two TaxiDataEvent objects by summing their segment distances.
	 * 
	 * @param value1
	 *            the first TaxiDataEvent
	 * @param value2
	 *            the second TaxiDataEvent
	 * @return the second TaxiDataEvent with the updated segment distance
	 */
	@Override
	public TaxiDataEvent reduce(TaxiDataEvent value1, TaxiDataEvent value2) {
		double segmentDistance1 = value1.getSegmentDistance();
		double segmentDistance2 = value2.getSegmentDistance();
		value2.setSegmentDistance(segmentDistance1 + segmentDistance2);

		return value2;
	}
}
