package de.tuhh.bigdata.a9.adapter;

import de.tuhh.bigdata.a9.application.TaxiEventRepository;
import de.tuhh.bigdata.a9.domain.TaxiInputEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the TaxiEventRepository interface for SQLite database.
 * It provides methods to fetch the next timestamp and events by timestamp from
 * the SQLite database.
 */
public class SqliteTaxiEventRepository implements TaxiEventRepository {
	private final Connection connection;

	/**
	 * Constructor for SqliteTaxiEventRepository.
	 *
	 * @param dbUrl
	 *            The URL of the SQLite database.
	 */
	public SqliteTaxiEventRepository(String dbUrl) {
		try {
			this.connection = DriverManager.getConnection(dbUrl);
		} catch (SQLException e) {
			throw new RuntimeException("Could not connect to the database: " + e.getMessage(), e);
		}
	}

	/**
	 * Fetches the next timestamp from the database that is greater than the given
	 * previous timestamp.
	 *
	 * @param previousTimestamp
	 *            The previous timestamp to compare against.
	 * @return The next timestamp greater than the previous timestamp, or -1 if no
	 *         such timestamp exists.
	 */
	@Override
	public Long getNextTimestamp(Long previousTimestamp) {
		String query = "SELECT DISTINCT timestamp FROM taxi_data WHERE timestamp > ? ORDER BY timestamp ASC LIMIT 1";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, previousTimestamp);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getLong("timestamp");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not fetch the next timestamp: " + e.getMessage(), e);
		}
		return -1L;
	}

	/**
	 * Fetches all events from the database that match the given timestamp.
	 *
	 * @param timestamp
	 *            The timestamp to filter events by.
	 * @return A list of TaxiEvent objects that match the given timestamp.
	 */
	@Override
	public List<TaxiInputEvent> getEventsByTimestamp(Long timestamp) {
		String query = "SELECT id, taxi_id, datetime, timestamp, longitude, latitude FROM taxi_data WHERE timestamp = ?";
		List<TaxiInputEvent> events = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, timestamp);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				events.add(new TaxiInputEvent(resultSet.getLong("id"), resultSet.getInt("taxi_id"),
						resultSet.getString("datetime"), timestamp, resultSet.getDouble("longitude"),
						resultSet.getDouble("latitude")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not fetch events for timestamp " + timestamp + ": " + e.getMessage(), e);
		}
		return events;
	}

	/**
	 * Closes the database connection. This method should be called when the
	 * repository is no longer needed to release resources.
	 *
	 * @throws SQLException
	 *             if an error occurs while closing the connection.
	 */
	@Override
	public void close() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not close the database connection: " + e.getMessage(), e);
		}
	}
}
