package de.tuhh.bigdata.a9.domain;

/**
 * This TaxiInputEvent class represents a single taxi event.
 */
public record TaxiInputEvent(Long id, Integer taxiId, String datetime, Long timestamp, Double longitude,
		Double latitude) {
}
