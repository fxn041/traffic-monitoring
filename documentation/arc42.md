# arc42

## Introduction and Goals
### Requirements Overview
The system is a real-time taxi monitoring solution that processes live location data from taxis using stream processing technologies and visualizes key metrics on a web-based dashboard. The system aims to:
- Calculate and display real-time speed, average speed, and distance per taxi
- Visualize currently active taxis on a geomap
- Trigger warnings when taxis exceed speed limits or leave predefined areas
- Monitor taxi fleet activity and provide operational insights
- Support both development and production deployment scenarios

### Quality Goals
| Priority | Quality Goal | Description |
|---|---|---|
| High | Real-time performance | Dashboard updates should reflect the latest data within seconds. |
| High | Scalability | The system should support increasing numbers of taxis with minimal delay. Support for both Docker Compose and Kubernetes deployment. |
| High | Data consistency | Schema Registry ensures consistent data format across components. |
| Medium | Fault tolerance | The system should gracefully handle temporary failures or restarts with health checks. |
| Medium | Modularity | Stream operators, database tools, dashboard components and monitoring are decoupled for maintainability. |
| Medium | Observability | Comprehensive monitoring through Prometheus, Grafana, Conduktor and RedisInsight. |

### Stakeholders
| Role | Interest |
|---|---|
| Students | Learn and implement a working Big Data pipeline using modern technologies. View dashboard insights and monitor system performance. |
| Professor/Tutor | Evaluate architecture design, code quality and system performance. Assess real-time processing capabilities. |

## Architecture Constraints
- Use Apache Kafka for message brokering.
- Use Apache Flink for stream processing.
- Store results in Redis for fast access.
- Deploy via Docker containers with support for cloud deployment.
- Dashboard must show live data with periodic updates.

## Context and Scope
### Business Context
The system simulates a real-time traffic monitoring dashboard for a taxi fleet. It focuses on live, short-term visualization of data for monitoring purposes.

### Technical Context
External systems:
- Input: T-drive dataset replayed into Kafka through a Python producer.
- Output: Real-time dashboards accessible via web browsers.
- Monitoring: External monitoring tools can access Prometheus metrics.

## Solution Strategy
- Apache Kafka will act as the message queue ingesting location data.
- Apache Flink will process the data using a DAG of stateful operators.
- Redis will store the results of the Flink processing.
- The Grafana dashboard will fetch and render processed data periodically.
- All components are containerized using Docker for consistency and reproducibility.

## Building Block View
### Level 1
**Main components:**
- **Kafka Producer**: Replays historical data into Kafka. Python script that reads from SQLite and writes to Kafka.
- **Schema Registry**: Confluent Schema Registry for Avro schema management and data consistency.
- **Flink Job(s)**: Two separate jobs for processing:
  - **Taxi Job**: Applies processing operators (speed, avg, distance, filtering, alerts).
  - **Taxi Active Job**: Counts active taxis for fleet monitoring.
- **Redis**: In-memory data store with multiple databases for different data types (taxi data, metadata, alerts).
- **Grafana Dashboard**: Geomap, warnings and analytics for the taxis with customized Redis datasource.
- **Monitoring Stack**: Prometheus for metrics collection, Conduktor for Kafka management, RedisInsight for Redis monitoring.

### Level 2
**Flink Operators (Taxi Job Pipeline):**
1. **Kafka Source**: Consumes TaxiEvent messages from Kafka topic
2. **Taxi Data Mapper**: Converts TaxiEvent to TaxiDataEvent format
3. **Taxi Range Filter**: Filters out taxis outside maximum radius from Forbidden City (configurable threshold)
4. **Key By Taxi ID**: Groups events by taxi ID for stateful processing
5. **Taxi Calculator**: Main processing operator that:
   - Calculates segment distance using Haversine formula
   - Computes instantaneous speed based on time/distance
   - Maintains total distance traveled
   - Calculates average speed over total journey time
   - Tracks distance from Forbidden City center
   - Filters out unrealistic speed readings above threshold
   - Maintains state flag for area violation detection
6. **Speeding Alert Detector**: Filters events exceeding speed limit and generates TaxiSpeedingAlert
7. **Windowing**: 5-second tumbling windows for aggregating segment distances
8. **Distance Reducer**: Aggregates segment distances within time windows
9. **Area Alert Detector**: Filters taxis that have left the alert radius and generates TaxiAreaAlert if they newly left the area
10. **Multiple Redis Sinks**: Routes data to different Redis databases (taxi data, metadata, alerts)

**Flink Operators (Taxi Active Job Pipeline):**
1. **Redis Taxi Count Source**: Periodically polls Redis to count active taxis
2. **Redis Count Sink**: Stores active taxi count in Redis metadata database

**Python Tools:**
- **Database Creator**: Reads raw T-drive dataset files and populates a SQLite database for the Kafka producer
- **Input Data Analyzer**: Computes statistics (e.g., average events per taxi) and generates visualizations for dataset understanding
- **Kafka Load Test Producer**: Generates synthetic load for testing system performance

**Infrastructure Components:**
- **Conduktor Console**: Web-based Kafka management interface with PostgreSQL backend
- **RedisInsight**: Web-based Redis monitoring and management tool
- **Prometheus**: Metrics aggregation and monitoring for Flink cluster performance
- **Grafana**: Visualization of the Redis data

### Level 3
**Domain-Driven Design Structure:**

**Taxi Job Architecture (Hexagonal/Clean Architecture):**
- **Domain Layer:**
  - `TaxiDataEvent`: Core entity representing taxi state and location data
  - `TaxiSpeedingAlert`: Domain event for speed violations
  - `TaxiAreaAlert`: Domain event for area violations
  - `ThresholdConfig`: Value object containing thresholds

- **Application Layer:**
  - `TaxiJobService`: Application service orchestrating the stream processing pipeline
  - `TaxiCalculator`: Domain service for taxi metrics calculations
  - `DistanceCalculator`: Domain service for geographical calculations
  - Filter operators (`TaxiSpeedingDetector`, `TaxiAreaDetector`, `TaxiRangeFilter`): Business rule implementations

- **Adapter Layer:**
  - `redis/`: Infrastructure adapters for Redis persistence
  - `config/`: Configuration adapters for YAML support

**Taxi Active Job Architecture:**
- **Application Layer**: `TaxiActiveJobService` for fleet monitoring
- **Adapter Layer**: Redis source/sink adapters for counting logic

## Runtime View
1. Kafka Producer reads and replays location data in timestamp order.
2. Flink consumes stream, applies transformations:
   - Calculates speed and average speed.
   - Tracks distance.
   - Flags violations (speed, area).
3. Flink writes results to Redis.
4. Dashboard fetches data in fixed intervals and visualizes:
   - Active taxis.
   - Alerts.
   - Distances and speeds.

## Deployment View
### Infrastructure Level 1
The system supports two main deployment scenarios:

**Development Environment (docker-compose-dev.yaml):**
- Uses local container builds
- Includes development tools and volume mounts
- Simplified configuration for rapid iteration

**Production Environment (docker-compose.yaml & Kubernetes):**
- Uses pre-built images from Docker Hub
- Optimized for performance and reliability
- Kubernetes deployment available for cloud environments

### Infrastructure Level 2
- Kafka, Flink, Redis, and Grafana run in separate containers.
- Flink jobs submitted to Flink cluster via API.
- Python tools can be executed on-demand for batch analysis.

## Crosscutting Concepts
- **State management**: Flink manages keyed state per taxi.
- **Time handling**: Uses event time for accurate stream processing.
- **Geo calculations**: Haversine formula used for distance and area checks.
- **Monitoring**: Optional metrics from Flink. Conduktor Console and RedisInsight are used for operational monitoring.
- **Security**: Basic precautions (credentials in `.gitignore`, no key exposure).
- **Configuration**: Stream rate, area radius, speed limit.
- **Data Analysis**: Python tools for historical data analysis.
- **Domain-Driven Design Elements**: The Flink jobs follow DDD principles with clear separation of concerns using hexagonal architecture patterns:
  - **Domain Layer**: Contains core business entities
  - **Application Layer**: Contains business logic and use cases
  - **Adapter Layer**: Contains infrastructure concerns

## Architecture Decisions
| Decision | Reason |
|---|---|
| KRaft mode for Kafka | Eliminates ZooKeeper dependency, simplifies deployment and improves performance |
| Two separate Flink jobs | Separation of concerns: taxi-job for main processing, taxi-active-job for fleet monitoring |
| Confluent Schema Registry | Ensures data consistency and schema evolution support for Avro messages |
| Customized Grafana Redis datasource | Enables direct integration between Grafana and Redis for real-time data visualization |
| Prometheus metrics integration | Provides comprehensive monitoring of Flink cluster performance |
| Kubernetes support | Enables cloud deployment and better scalability compared to Docker Compose only |
| Hexagonal Architecture | Provides clear separation between domain logic, application services, and infrastructure adapters, improving maintainability and testability |

## Quality Requirements
### Quality Requirements Overview
See earlier section on quality goals.

### Quality Scenarios
| Scenario | Description |
|---|---|
| Real-time updates | Dashboard reflects new data within seconds. |
| Scalable processing | System handles increased taxi data without significant delays. |
| Fault tolerance | System recovers gracefully from temporary failures. |
| Service health | Containers only start when dependencies are healthy, reducing startup errors. |

## Risks and Technical Debt
| Risk | Impact |
|---|---|
| Single Kafka broker | No fault tolerance for Kafka. This can quickly be changed if necessary. |
| Single Flink TaskManager | Limited scalability for stream processing. This can quickly be changed if necessary. |

## Glossary
| Term | Definition |
|---|---|
| Kafka | Distributed message broker. |
| Flink | Stream processing framework. |
| Redis | In-memory data structure store. |
| Operator | A processing unit in Flink. |
| Conduktor | Kafka management and monitoring UI |
| RedisInsight | Redis monitoring UI |
| Haversine Formula | Formula to calculate distance between two geo points. |
| Stream Processing | Processing data continuously in motion, not from static files. |
| T-Drive | Taxi GPS dataset used for simulation. |
| Dashboard | Grafana web interface displaying taxi metrics and incidents. |
| SQLite | Lightweight database for historical data storage. |
| Python Tools | Scripts for batch processing and data analysis. |
| Healthcheck | Docker Compose feature to ensure service readiness. |
| KRaft | Kafka Raft metadata mode, eliminates need for ZooKeeper. |
| Schema Registry | Component for managing Avro schemas and ensuring data compatibility. |
| Avro | Binary serialization format used for Kafka messages. |
| Kubernetes | Container orchestration platform for cloud deployment. |
| Prometheus | Time-series database and monitoring system for metrics collection. |
