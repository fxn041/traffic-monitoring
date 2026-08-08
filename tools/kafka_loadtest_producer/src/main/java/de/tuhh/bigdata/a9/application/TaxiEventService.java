package de.tuhh.bigdata.a9.application;

import de.tuhh.bigdata.a9.domain.TaxiInputEvent;
import java.util.List;

/**
 * TaxiEventService is a class that processes taxi events by retrieving them
 * from a repository and sending them to a producer. It handles the timing of
 * event processing based on a time multiplier.
 */
public class TaxiEventService {
	private final TaxiEventRepository repository;
	private final TaxiEventProducer producer;
	private final Double timeMultiplier;

	/**
	 * Constructor for TaxiEventService.
	 *
	 * @param repository
	 *            The repository to fetch taxi events from.
	 * @param producer
	 *            The producer to send taxi events to.
	 * @param timeMultiplier
	 *            The multiplier to adjust the timing of event processing. A value
	 *            of 1.0 means real-time, less than 1.0 means faster, and greater
	 *            than 1.0 means slower. For example, a value of 0.5 means process
	 *            events twice as fast as real-time.
	 */
	public TaxiEventService(TaxiEventRepository repository, TaxiEventProducer producer, Double timeMultiplier) {
		this.repository = repository;
		this.producer = producer;
		this.timeMultiplier = timeMultiplier;
	}

	/**
	 * Processes taxi events by retrieving them from the repository and sending them
	 * to the producer. It handles the timing of event processing based on the time
	 * multiplier.
	 */
	public void processEvents() {
		Long previousTimestamp = -1L;
		Long processingTime = 0L;
		while (true) {
			Long nextTimestamp = repository.getNextTimestamp(previousTimestamp);
			if (nextTimestamp == -1L) {
				break;
			}

			if (previousTimestamp != -1L) {
				Double waitTime = (nextTimestamp - previousTimestamp) * timeMultiplier * 1000 - processingTime;
				if (waitTime > 0) {
					try {
						Thread.sleep(waitTime.longValue());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						System.err.println("Thread interrupted: " + e.getMessage());
						break;
					}
				}
			}

			Long startTime = System.currentTimeMillis();

			List<TaxiInputEvent> events = repository.getEventsByTimestamp(nextTimestamp);
			for (TaxiInputEvent event : events) {
				producer.send(event);
			}

			processingTime = System.currentTimeMillis() - startTime;

			previousTimestamp = nextTimestamp;
		}
	}
}
