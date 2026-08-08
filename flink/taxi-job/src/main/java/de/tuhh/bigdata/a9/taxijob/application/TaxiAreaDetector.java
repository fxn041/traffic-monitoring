package de.tuhh.bigdata.a9.taxijob.application;

import org.apache.flink.api.common.functions.FilterFunction;

import de.tuhh.bigdata.a9.taxijob.domain.TaxiDataEvent;

/**
 * Filter function to alert if a taxi is outside ALERT_RADIUS km of the
 * Forbidden City.
 */
public class TaxiAreaDetector implements FilterFunction<TaxiDataEvent> {
	private final double taxiAreaAlertRadiusThreshold;

	public TaxiAreaDetector(double taxiAreaAlertRadiusThreshold) {
		this.taxiAreaAlertRadiusThreshold = taxiAreaAlertRadiusThreshold;
	}

	/**
	 * Checks whether the taxi is at least ALERT_RADIUS km away from the center of
	 * the Forbidden City.
	 * 
	 * @param event
	 *            the TaxiDataEvent to check
	 * 
	 * @return true if the taxi is outside the ALERT_RADIUS from the Forbidden City
	 *         and has not left the area before, false otherwise
	 */
	@Override
	public boolean filter(TaxiDataEvent event) {
		return !event.getHasLeftAreaBefore() && event.getDistanceFromForbiddenCity() > taxiAreaAlertRadiusThreshold;
	}
}
