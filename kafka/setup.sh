#!/bin/bash

# This script runs when starting the Kafka container and can be used to create topics or otherwise configure the Kafka server

# Make sure that Kafka is on the PATH
export PATH=$PATH:/opt/kafka/bin

# Start the Kafka server in the background
/etc/confluent/docker/run &

# Wait for Kafka to start
while ! nc -z kafka-master 19092; do
  sleep 1
done

# Create topics
kafka-topics --create --bootstrap-server kafka-master:19092 --replication-factor 1 --partitions 8 --topic taxi-events --config "cleanup.policy=delete"

# Bring the Kafka server process back to the foreground, this ensures that the container does not exit early
wait
