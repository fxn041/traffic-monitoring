package de.tuhh.bigdata.a9.taxijob.adapter.config;

/**
 * Configuration class for Kafka settings.
 */
public class KafkaConfig {

	private String bootstrapServers;
	private String topic;
	private String groupId;
	private String schemaRegistryUrl;

	/**
	 * Returns the Kafka bootstrap servers.
	 * 
	 * @return the bootstrap servers used to connect to Kafka
	 */
	public String getBootstrapServers() {
		return bootstrapServers;
	}

	/**
	 * Sets the Kafka bootstrap servers.
	 * 
	 * @param bootstrapServers
	 *            the bootstrap servers to set
	 */
	public void setBootstrapServers(String bootstrapServers) {
		this.bootstrapServers = bootstrapServers;
	}

	/**
	 * Returns the Kafka topic for messages.
	 * 
	 * @return the Kafka topic used for messages
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Sets the topic for Kafka messages.
	 * 
	 * @param topic
	 *            the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * Returns the group ID for Kafka consumers.
	 * 
	 * @return the group ID used by Kafka consumers
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Sets the group ID for Kafka consumers.
	 * 
	 * @param groupId
	 *            the group ID to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * Returns the URL for the schema registry.
	 * 
	 * @return the URL of the schema registry
	 */
	public String getSchemaRegistryUrl() {
		return schemaRegistryUrl;
	}

	/**
	 * Sets the URL for the schema registry.
	 * 
	 * @param schemaRegistryUrl
	 *            the URL of the schema registry
	 */
	public void setSchemaRegistryUrl(String schemaRegistryUrl) {
		this.schemaRegistryUrl = schemaRegistryUrl;
	}
}
