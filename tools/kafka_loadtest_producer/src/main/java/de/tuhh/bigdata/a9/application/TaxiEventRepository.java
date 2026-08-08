package de.tuhh.bigdata.a9.application;

import de.tuhh.bigdata.a9.domain.TaxiInputEvent;
import java.util.List;

/**
 * TaxiEventRepository is an interface that defines the contract for retrieving
 * TaxiEvent objects from a data source.
 */
public interface TaxiEventRepository {
	public Long getNextTimestamp(Long previousTimestamp);

	public List<TaxiInputEvent> getEventsByTimestamp(Long timestamp);

	public void close();
}
