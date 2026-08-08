package de.tuhh.bigdata.a9.taxijob.application;

import org.apache.flink.api.common.functions.ReduceFunction;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;

/**
 * Reduce function to calculate the total distance, speed, and average speed for
 * taxis.
 */
public class TaxiCalculator implements ReduceFunction<TaxiDataEvent> {
	// Threshold for ignoring taxi events with a speed above this value (in km/h)
	private final double taxiMaxSpeedThreshold;
	private final double taxiAreaAlertRadiusThreshold;

	public TaxiCalculator(double taxiMaxSpeedThreshold, double taxiAreaAlertRadiusThreshold) {
		this.taxiMaxSpeedThreshold = taxiMaxSpeedThreshold;
		this.taxiAreaAlertRadiusThreshold = taxiAreaAlertRadiusThreshold;
	}

	/**
	 * Reduces two TaxiDataEvent objects by calculating the total distance, speed,
	 * and average speed.
	 *
	 * @param value1
	 *            the first TaxiDataEvent
	 * @param value2
	 *            the second TaxiDataEvent
	 * @return the second TaxiDataEvent with updated distance, speed, and average
	 *         speed
	 */
	@Override
	public TaxiDataEvent reduce(TaxiDataEvent value1, TaxiDataEvent value2) {
		// Calculate the distance
		double lat1 = value1.getLatitude();
		double lon1 = value1.getLongitude();
		double lat2 = value2.getLatitude();
		double lon2 = value2.getLongitude();
		double prevTotalDistance = value1.getDistance();
		double segmentDistance = DistanceCalculator.calculateHaversineDistance(lat1, lon1, lat2, lon2);
		double newTotalDistance = prevTotalDistance + segmentDistance;
		value2.setSegmentDistance(segmentDistance);
		value2.setDistance(newTotalDistance);

		// Calculate the speed
		long timestamp1 = value1.getTimestamp();
		long timestamp2 = value2.getTimestamp();
		long timeDifference = timestamp2 - timestamp1;
		if (timeDifference <= 0) {
			value2.setSpeed(0.0);
		} else {
			double speed = 3600.0 * segmentDistance / timeDifference;
			value2.setSpeed(speed);

			// Ignore taxi events with a speed above a threshold
			if (speed > taxiMaxSpeedThreshold) {
				return value1;
			}
		}

		// Calculate the average speed
		long prevTotalTime = value1.getTotalTime();
		long newTotalTime = prevTotalTime + timeDifference;
		double averageSpeed = (newTotalTime > 0) ? (3600.0 * newTotalDistance / newTotalTime) : 0.0;
		value2.setTotalTime(newTotalTime);
		value2.setAverageSpeed(averageSpeed);

		// Calculate the distance from the Forbidden City
		double distanceFromForbiddenCity = DistanceCalculator.calculateHaversineDistance(lat2, lon2,
				DistanceCalculator.FORBIDDEN_CITY_LATITUDE, DistanceCalculator.FORBIDDEN_CITY_LONGITUDE);
		value2.setDistanceFromForbiddenCity(distanceFromForbiddenCity);

		// Set the flag for whether the taxi has left the area before
		value2.setHasLeftAreaBefore(value1.getDistanceFromForbiddenCity() > taxiAreaAlertRadiusThreshold);

		return value2;
	}
}
