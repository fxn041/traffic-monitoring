package de.tuhh.bigdata.a9.taxijob.application;

import org.apache.flink.api.common.functions.FilterFunction;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;

/**
 * Filter function to check if a taxi is within a certain range.
 */
public class TaxiRangeFilter implements FilterFunction<TaxiDataEvent> {
	private final double taxiAreaMaxRadiusThreshold;

	public TaxiRangeFilter(double taxiAreaMaxRadiusThreshold) {
		this.taxiAreaMaxRadiusThreshold = taxiAreaMaxRadiusThreshold;
	}

	/**
	 * Checks if the taxi's location is within FORBIDDEN_CITY_MAX_DISTANCE km of the
	 * forbidden city.
	 * 
	 * @param value
	 *            the TaxiDataEvent to check
	 * @return true if the taxi is within FORBIDDEN_CITY_MAX_DISTANCE km of the
	 *         forbidden city, false otherwise
	 */
	@Override
	public boolean filter(TaxiDataEvent value) {
		return DistanceCalculator.calculateHaversineDistance(value.getLatitude(), value.getLongitude(),
				DistanceCalculator.FORBIDDEN_CITY_LATITUDE,
				DistanceCalculator.FORBIDDEN_CITY_LONGITUDE) < taxiAreaMaxRadiusThreshold;
	}
}
