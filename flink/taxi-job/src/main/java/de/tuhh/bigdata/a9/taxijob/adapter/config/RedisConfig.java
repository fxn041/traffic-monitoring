package de.tuhh.bigdata.a9.taxijob.adapter.config;

/**
 * Configuration class for Redis settings.
 */
public class RedisConfig {

	private String host;
	private int port;
	private int taxiDb;
	private int metadataDb;
	private int ttl;
	private String areaAlertsStreamKey;
	private String speedingAlertsStreamKey;

	/**
	 * Returns the Redis host.
	 * 
	 * @return the host used to connect to Redis
	 */
	public String getHost() {
		return host;
	}

	public String getAreaAlertsStreamKey() {
		return areaAlertsStreamKey;
	}

	public void setAreaAlertsStreamKey(String areaAlertsStreamKey) {
		this.areaAlertsStreamKey = areaAlertsStreamKey;
	}

	public String getSpeedingAlertsStreamKey() {
		return speedingAlertsStreamKey;
	}

	public void setSpeedingAlertsStreamKey(String speedingAlertsStreamKey) {
		this.speedingAlertsStreamKey = speedingAlertsStreamKey;
	}

	/**
	 * Sets the Redis host.
	 * 
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns the Redis port.
	 * 
	 * @return the port used to connect to Redis
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the Redis port.
	 * 
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the Redis database index for taxi data.
	 * 
	 * @return the database index for taxi data
	 */
	public int getTaxiDb() {
		return taxiDb;
	}

	/**
	 * Sets the Redis database index for taxi data.
	 * 
	 * @param taxiDb
	 *            the database index to set for taxi data
	 */
	public void setTaxiDb(int taxiDb) {
		this.taxiDb = taxiDb;
	}

	/**
	 * Returns the Redis database index for metadata.
	 * 
	 * @return the database index for metadata
	 */
	public int getMetadataDb() {
		return metadataDb;
	}

	/**
	 * Sets the Redis database index for metadata.
	 * 
	 * @param metadataDb
	 *            the database index to set for metadata
	 */
	public void setMetadataDb(int metadataDb) {
		this.metadataDb = metadataDb;
	}

	/**
	 * Returns the time-to-live (TTL) for Redis keys in seconds.
	 * 
	 * @return the TTL in seconds for Redis keys
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * Sets the time-to-live (TTL) for Redis keys in seconds.
	 * 
	 * @param ttl
	 *            the TTL to set for Redis keys
	 */
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
}
