package de.tuhh.bigdata.a9.taxiactivejob.adapter.config;

public class AppConfig {
	private RedisConfig redis;
	private JobConfig job;

	public JobConfig getJob() {
		return job;
	}

	public void setJob(JobConfig job) {
		this.job = job;
	}

	public AppConfig() {
		// Default constructor for snakeyaml
	}

	public AppConfig(RedisConfig redis) {
		this.redis = redis;
	}

	public RedisConfig getRedis() {
		return redis;
	}

	public void setRedis(RedisConfig redis) {
		this.redis = redis;
	}
}
