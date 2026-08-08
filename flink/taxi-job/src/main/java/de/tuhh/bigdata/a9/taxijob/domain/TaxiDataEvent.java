package de.tuhh.bigdata.a9.taxijob.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a taxi data event.
 */
public class TaxiDataEvent {

	private Integer id;
	private Long timestamp;
	private String dateTime;
	private Double latitude;
	private Double longitude;
	private Double distance;
	private Double segmentDistance;
	private Double distanceFromForbiddenCity;
	private Double speed;
	private Double averageSpeed;
	private Long totalTime;
	private Boolean hasLeftAreaBefore;

	/**
	 * Constructor for TaxiDataEvent.
	 * 
	 * @param id
	 *            the unique identifier for the taxi
	 * @param timestamp
	 *            the timestamp of the event as Unix timestamp (seconds since epoch)
	 * @param latitude
	 *            the latitude of the taxi's location
	 * @param longitude
	 *            the longitude of the taxi's location
	 */
	public TaxiDataEvent(Integer id, Long timestamp, String dateTime, Double latitude, Double longitude) {
		this.id = id;
		this.timestamp = timestamp;
		this.dateTime = dateTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = 0.0;
		this.segmentDistance = 0.0;
		this.distanceFromForbiddenCity = 0.0;
		this.speed = 0.0;
		this.averageSpeed = 0.0;
		this.totalTime = 0L;
		this.hasLeftAreaBefore = false;
	}

	/**
	 * Returns a map of attributes for the taxi data event. Missing attributes will
	 * not be included in the map.
	 * 
	 * @return a map containing the attributes of the taxi data event
	 */
	public Map<String, String> getAttributeSet() {
		Map<String, String> attributes = new HashMap<>();
		if (timestamp != null) {
			attributes.put("timestamp", timestamp.toString());
		}
		if (dateTime != null) {
			attributes.put("dateTime", dateTime);
		}
		if (latitude != null) {
			attributes.put("latitude", latitude.toString());
		}
		if (longitude != null) {
			attributes.put("longitude", longitude.toString());
		}
		if (distance != null) {
			attributes.put("distance", distance.toString());
		}
		if (segmentDistance != null) {
			attributes.put("segmentDistance", segmentDistance.toString());
		}
		if (distanceFromForbiddenCity != null) {
			attributes.put("distanceFromForbiddenCity", distanceFromForbiddenCity.toString());
		}
		if (speed != null) {
			attributes.put("speed", speed.toString());
		}
		if (averageSpeed != null) {
			attributes.put("averageSpeed", averageSpeed.toString());
		}
		if (totalTime != null) {
			attributes.put("totalTime", totalTime.toString());
		}
		if (hasLeftAreaBefore != null) {
			attributes.put("hasLeftAreaBefore", hasLeftAreaBefore.toString());
		}
		return attributes;
	}

	/**
	 * Returns the unique identifier for the taxi.
	 * 
	 * @return the unique identifier for the taxi
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the unique identifier for the taxi.
	 * 
	 * @param id
	 *            the unique identifier for the taxi
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the timestamp of the event as Unix timestamp (seconds since epoch).
	 * 
	 * @return the timestamp of the event
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp of the event as Unix timestamp (seconds since epoch).
	 * 
	 * @param timestamp
	 *            the timestamp of the event
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Returns the latitude of the taxi's location.
	 * 
	 * @return the latitude of the taxi's location
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude of the taxi's location.
	 * 
	 * @param latitude
	 *            the latitude of the taxi's location
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Returns the longitude of the taxi's location.
	 * 
	 * @return the longitude of the taxi's location
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude of the taxi's location.
	 * 
	 * @param longitude
	 *            the longitude of the taxi's location
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Returns the total distance traveled by the taxi in kilometers.
	 * 
	 * @return the total distance traveled by the taxi
	 */
	public Double getDistance() {
		return distance;
	}

	/**
	 * Sets the total distance traveled by the taxi in kilometers.
	 * 
	 * @param distance
	 *            the total distance traveled by the taxi
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}

	/**
	 * Returns the distance of the current segment in kilometers. Current segment
	 * distance is the distance traveled since the last event.
	 * 
	 * @return the distance of the current segment
	 */
	public Double getSegmentDistance() {
		return segmentDistance;
	}

	/**
	 * Sets the distance of the current segment in kilometers. Current segment
	 * distance is the distance traveled since the last event.
	 * 
	 * @param segmentDistance
	 *            the distance of the current segment
	 */
	public void setSegmentDistance(Double segmentDistance) {
		this.segmentDistance = segmentDistance;
	}

	/**
	 * Returns the distance from the Forbidden City in kilometers.
	 * 
	 * @return the distance from the Forbidden City
	 */
	public Double getDistanceFromForbiddenCity() {
		return distanceFromForbiddenCity;
	}

	/**
	 * Sets the distance from the Forbidden City in kilometers.
	 * 
	 * @param distanceFromForbiddenCity
	 *            the distance from the Forbidden City
	 */
	public void setDistanceFromForbiddenCity(Double distanceFromForbiddenCity) {
		this.distanceFromForbiddenCity = distanceFromForbiddenCity;
	}

	/**
	 * Returns the current speed of the taxi in kilometers per hour.
	 * 
	 * @return the current speed of the taxi
	 */
	public Double getSpeed() {
		return speed;
	}

	/**
	 * Sets the current speed of the taxi in kilometers per hour.
	 * 
	 * @param speed
	 *            the current speed of the taxi
	 */
	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	/**
	 * Returns the average speed of the taxi in kilometers per hour.
	 * 
	 * @return the average speed of the taxi
	 */
	public Double getAverageSpeed() {
		return averageSpeed;
	}

	/**
	 * Sets the average speed of the taxi in kilometers per hour.
	 * 
	 * @param averageSpeed
	 *            the average speed of the taxi
	 */
	public void setAverageSpeed(Double averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	/**
	 * Returns the total time spent by the taxi in seconds. This is the total time
	 * from the first event to the current event.
	 * 
	 * @return the total time spent by the taxi
	 */
	public Long getTotalTime() {
		return totalTime;
	}

	/**
	 * Sets the total time spent by the taxi in seconds. This is the total time from
	 * the first event to the current event.
	 * 
	 * @param totalTime
	 *            the total time spent by the taxi
	 */
	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}

	/**
	 * Returns whether the taxi has left the area of interest before.
	 */
	public Boolean getHasLeftAreaBefore() {
		return hasLeftAreaBefore;
	}

	/**
	 * Sets whether the taxi has left the area of interest before.
	 * 
	 * @param hasLeftAreaBefore
	 *            true if the taxi has left the area before, false otherwise
	 */
	public void setHasLeftAreaBefore(Boolean hasLeftAreaBefore) {
		this.hasLeftAreaBefore = hasLeftAreaBefore;
	}
}
