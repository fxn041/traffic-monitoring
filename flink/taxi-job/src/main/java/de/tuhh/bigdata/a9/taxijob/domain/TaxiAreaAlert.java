package de.tuhh.bigdata.a9.taxijob.domain;

import java.util.HashMap;

/**
 * Represents an area alert for a taxi.
 */
public class TaxiAreaAlert {

	private TaxiDataEvent taxiDataEvent;
	private Double maxDistance;

	/**
	 * Constructor for TaxiAreaAlert.
	 * 
	 * @param taxiDataEvent
	 *            the taxi data event that triggered the alert
	 * @param maxDistance
	 *            the maximum distance in kilometers allowed
	 */
	public TaxiAreaAlert(TaxiDataEvent taxiDataEvent, Double maxDistance) {
		this.taxiDataEvent = taxiDataEvent;
		this.maxDistance = maxDistance;
	}

	/**
	 * Returns a HashMap representation of the alert.
	 * 
	 * @return a HashMap containing the taxi ID, current distance from the forbidden
	 *         city, and date/time of the event
	 */
	public HashMap<String, String> getHashMap() {
		HashMap<String, String> properties = new HashMap<>();
		properties.put("taxiId", taxiDataEvent.getId().toString());
		properties.put("currentDistance", taxiDataEvent.getDistanceFromForbiddenCity().toString());
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
	 * Returns the maximum allowed distance.
	 * 
	 * @return the maximum distance in kilometers
	 */
	public Double getMaxDistance() {
		return maxDistance;
	}
}
