k:
	docker compose down kafka schema-registry postgresql conduktor-console conduktor-monitoring
	docker compose up -d kafka schema-registry postgresql conduktor-console conduktor-monitoring

f:
	docker compose down flink-jobmanager flink-taskmanager
	docker compose up -d flink-jobmanager flink-taskmanager

r:
	docker compose down redis redisinsight
	docker compose up -d redis redisinsight

bd:
	docker compose -f docker-compose-dev.yml up
