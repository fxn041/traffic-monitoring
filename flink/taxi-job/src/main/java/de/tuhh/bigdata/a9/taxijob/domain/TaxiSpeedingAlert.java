package de.tuhh.bigdata.a9.taxijob.domain;

import java.util.HashMap;

/**
 * Represents a speeding alert for a taxi.
 */
public class TaxiSpeedingAlert {

	private final TaxiDataEvent taxiDataEvent;
	private final Double speedLimit;

	/**
	 * Constructor for TaxiSpeedingAlert.
	 * 
	 * @param taxiDataEvent
	 *            the taxi data event that triggered the alert
	 * @param speedLimit
	 *            the speed limit in km/h that was exceeded
	 */
	public TaxiSpeedingAlert(TaxiDataEvent taxiDataEvent, Double speedLimit) {
		this.taxiDataEvent = taxiDataEvent;
		this.speedLimit = speedLimit;
	}

	/**
	 * Returns a HashMap representation of the alert.
	 * 
	 * @return a HashMap containing the taxi ID, current speed, and date/time of the
	 *         event
	 */
	public HashMap<String, String> getHashMap() {
		HashMap<String, String> properties = new HashMap<>();
		properties.put("taxiId", taxiDataEvent.getId().toString());
		properties.put("currentSpeed", taxiDataEvent.getSpeed().toString());
		properties.put("dateTime", taxiDataEvent.getDateTime());

		return properties;
	}

	/**
	 * Returns the TaxiDataEvent associated with this alert.
	 * 
	 * @return the TaxiDataEvent that triggered the alert
	 */
	public TaxiDataEvent getTaxiDataEvent() {
		return taxiDataEvent;
	}

	/**
	 * Returns the speed limit that was exceeded.
	 * 
	 * @return the speed limit in km/h
	 */
	public Double getSpeedLimit() {
		return speedLimit;
	}
}
