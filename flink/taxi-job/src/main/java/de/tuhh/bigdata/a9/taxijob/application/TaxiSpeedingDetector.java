package de.tuhh.bigdata.a9.taxijob.application;

import org.apache.flink.api.common.functions.FilterFunction;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;

/**
 * FilterFunction to detect if a taxi is speeding.
 */
public class TaxiSpeedingDetector implements FilterFunction<TaxiDataEvent> {

	private final double speedLimit;

	/**
	 * Constructor for TaxiSpeedingDetector.
	 * 
	 * @param speedLimit
	 *            the speed limit in km/h that is considered as speeding
	 */
	public TaxiSpeedingDetector(double speedLimit) {
		this.speedLimit = speedLimit;
	}

	/**
	 * Filters the TaxiDataEvent to check if the speed exceeds the speed limit.
	 * 
	 * @param event
	 *            the TaxiDataEvent to be checked
	 * 
	 * @return true if the speed exceeds the speed limit, false otherwise
	 */
	@Override
	public boolean filter(TaxiDataEvent event) {
		return event.getSpeed() > speedLimit;
	}
}
