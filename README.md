# Real-Time Taxi Monitoring System
This project is a real-time taxi monitoring solution that processes live location data from taxis using stream processing technologies and visualizes key metrics on a web-based dashboard.
The system is containerized using Docker and includes components for message brokering, stream processing, data storage, and monitoring.

For deeper insights into the project, please have a look at our [arc42](documentation/arc42.md) documentation.

## Features
- Real-time processing of taxi location data.
- Taxi events streaming using Kafka.
- Calculation of speed, average speed, and distance per taxi in a Flink job.
- Cashing results using Redis.
- Visualization of active taxis on a map in Grafana.
- Grafana dashboard for taxi fleet monitoring.
- Monitoring and management of data using Conduktor, Redisinsights and Prometheus.

## Prerequisites
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## How to Run the Project
If you want to quickly get started, use the PROD guide. If you want to actively develop the project, use the DEV guide.

### PROD Guide
1. Clone the repository:
   ```bash
   git clone git@collaborating.tuhh.de:e-19/teaching/bd25_project_a9_b.git
   cd bd25_project_a9_b
   ```
2. Set up the environment variables:
   - Create a `.env` file in the root project directory.
   - Refer to the specification of the `.env` file in the [documentation](documentation/env_setup.md).
3. Start the Docker containers:
   ```bash
   docker compose up -d
   ```
4. Access the Conduktor Web Interface:
   - Open your browser and navigate to [http://127.0.0.1:8080/](http://127.0.0.1:8080/).
   - Log in using the admin credentials from your `.env` file.
   - The Kafka container automatically creates the `taxi-events` topic.
   - Use this interface to monitor and manage Kafka.
5. Access the Flink Jobmanager Web Interface:
   - Open your browser and navigate to [http://127.0.0.1:8081/](http://127.0.0.1:8081/).
   - Use this interface to monitor and manage Flink jobs.
6. Access the RedisInsight Web Interface:
   - Open your browser and navigate to [http://127.0.0.1:5540/](http://127.0.0.1:5540/).
   - Use this interface to monitor and manage the Redis database.
7. Access the Prometheus Web Interface:
   - Open your Brower and navigate to [http://127.0.0.1:9090/](http://127.0.0.1:9090/).
   - Use this interface to monitor flink performance.
8. Access the Grafana Web Interface (this is where the map with the taxis can be viewed):
   - Log in using the admin credentials from your `.env` file.
   - Open your browser and navigate to [http://127.0.0.1:3000/](http://127.0.0.1:3000/).

### DEV Guide
Additional requirements for development are Java and Python.
1. Clone the repository:
   ```bash
   git clone git@collaborating.tuhh.de:e-19/teaching/bd25_project_a9_b.git
   cd bd25_project_a9_b
   ```
2. Set up the environment variables:
   - Create a `.env` file in the root project directory.
   - Refer to the specification of the `.env` file in the [documentation](documentation/env_setup.md).
3. Download and extract the [T-drive Trojectory Dataset](https://1drv.ms/u/s!AsWQUIUFkRXPgsN6OCa9OB-qziGfhA). Afterwards move the `taxi_log_2008_by_id` folder to `data/taxi_log_2008_by_id` (create the data folder inside the project's root directory).
4. Run the database tool to create a SQLite database from the dataset.
   ```bash
   python3 -m venv .venv
   source .venv/bin/activate
   cd tools/database_tool
   pip install -r requirements.txt
   python3 database_creator.py
   cd ../..
   ```
5. Compile the Fink Jobs:
   ```bash
   cd flink/
   mvn clean package
   cd ..
   ```
6. Start the Docker containers:
   ```bash
   docker compose -f docker-compose-dev.yaml up -d
   ```
7. Verify that all services are running:
   ```bash
   docker ps
   ```
8. Continue with step 4 of the [PROD Guide](#prod-guide).

## Project Components
- **Kafka**: Message broker for ingesting and distributing taxi location data with included controller.
- **PostgreSQL**: Database for storing Conduktor Console metadata.
- **Conduktor Console**: Web-based tool for managing and monitoring Kafka.
- **Conduktor Monitoring**: Provides additional monitoring capabilities for Kafka.
- **Flink Jobmanager**: Orchestrates the Flink cluster.
- **Flink Taskmanager**: One or more Flink workers that pick up jobs.
- **Redis**: In-memory key-value store for caching and fast data access.
- **Redisinsight**: Web-based managing and monitoring for Redis.
- **Prometheus**: Aggregator for streaming metadata, holds data for Grafana.
- **Grafana**: Main dashboard, visualizes taxi fleet and metrics.

## Development Notes
- The Kafka producer reads the data from a SQLite database which has to be created prior to the first pipeline run.
- The Kafka container runs a [script](kafka/setup.sh) to create the required topic.
- The [docker-compose.yaml](docker-compose.yaml) file is configured for production. For development, consider using [docker-compose-dev.yaml](docker-compose-dev.yaml).
- The command `docker compose build --no-cache` can be used to build the docker images again if changes to the Dockerfiles are required.

## Troubleshooting
- Container logs:
  ```bash
  docker logs <kafka|conduktor-console|postgresql|...>
  ```
