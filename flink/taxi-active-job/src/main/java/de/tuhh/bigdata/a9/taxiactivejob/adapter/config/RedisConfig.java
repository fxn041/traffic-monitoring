package de.tuhh.bigdata.a9.taxiactivejob.adapter.config;

public class RedisConfig {
	private String host;
	private int port;
	private int taxiDb;
	private int metadataDb;

	public RedisConfig() {
		// Default constructor for snakeyaml
	}

	public RedisConfig(String host, int port, int taxiDb, int metadataDb) {
		this.host = host;
		this.port = port;
		this.taxiDb = taxiDb;
		this.metadataDb = metadataDb;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTaxiDb() {
		return taxiDb;
	}

	public void setTaxiDb(int taxiDb) {
		this.taxiDb = taxiDb;
	}

	public int getMetadataDb() {
		return metadataDb;
	}

	public void setMetadataDb(int metadataDb) {
		this.metadataDb = metadataDb;
	}
}
