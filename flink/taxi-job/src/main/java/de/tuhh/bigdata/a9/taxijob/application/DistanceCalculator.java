package de.tuhh.bigdata.a9.taxijob.application;

/**
 * Utility class for calculating distances using the Haversine formula.
 */
public class DistanceCalculator {

	public static final double EARTH_RADIUS = 6371.0;
	public static final double FORBIDDEN_CITY_LATITUDE = 39.91835007000467;
	public static final double FORBIDDEN_CITY_LONGITUDE = 116.39716995667276;

	/**
	 * Calculates the Haversine distance between two geographical points specified
	 * by latitude and longitude.
	 * 
	 * @param lat1
	 *            the latitude of the first point in degrees
	 * @param lon1
	 *            the longitude of the first point in degrees
	 * @param lat2
	 *            the latitude of the second point in degrees
	 * @param lon2
	 *            the longitude of the second point in degrees
	 * @return the distance between the two points in kilometers
	 */
	public static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		double dLat = lat2 - lat1;
		double dLon = Math.toRadians(lon2 - lon1);

		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return EARTH_RADIUS * c;
	}
}
