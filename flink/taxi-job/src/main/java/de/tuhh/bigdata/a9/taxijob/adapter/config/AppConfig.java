package de.tuhh.bigdata.a9.taxijob.adapter.config;

/**
 * Configuration class for the application, containing Kafka and Redis
 * configurations.
 */
public class AppConfig {

	private KafkaConfig kafka;
	private RedisConfig redis;
	private JobConfig job;

	public JobConfig getJob() {
		return job;
	}

	public void setJob(JobConfig job) {
		this.job = job;
	}

	/**
	 * Returns the Kafka configuration.
	 * 
	 * @return the Kafka configuration
	 */
	public KafkaConfig getKafka() {
		return kafka;
	}

	/**
	 * Sets the Kafka configuration.
	 * 
	 * @param kafka
	 *            the Kafka configuration to set
	 */
	public void setKafka(KafkaConfig kafka) {
		this.kafka = kafka;
	}

	/**
	 * Returns the Redis configuration.
	 * 
	 * @return the Redis configuration
	 */
	public RedisConfig getRedis() {
		return redis;
	}

	/**
	 * Sets the Redis configuration.
	 * 
	 * @param redis
	 *            the Redis configuration to set
	 */
	public void setRedis(RedisConfig redis) {
		this.redis = redis;
	}
}
