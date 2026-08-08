package de.tuhh.bigdata.a9.application;

import de.tuhh.bigdata.a9.domain.TaxiInputEvent;

/**
 * TaxiEventProducer is an interface that defines the contract for sending
 * TaxiEvent objects to a destination.
 */
public interface TaxiEventProducer {
	public void send(TaxiInputEvent event);

	public void close();
}
