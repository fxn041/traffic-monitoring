# Project Optimizations
The following optimizations have been implemented to enhance the performance of our real-time taxi monitoring solution:

## Data Input Optimization
- **SQLite Database**: Replaced the use of more than 10,000 CSV files with a single SQLite database to streamline data ingestion and improve input performance.

## Kafka Producer Optimization
- **Multithreading**: Implemented two-thread parallelization in the Python Kafka producer, optimizing reading from SQLite and writing to Kafka concurrently, thus reducing latency and improving throughput.

## Kafka Infrastructure Optimization
- **Kafka KRaft Mode**: Transitioned from Kafka with ZooKeeper to Kafka Raft mode (KRaft), removing ZooKeeper dependency to simplify infrastructure, enhance performance, and reduce deployment complexity.

## Stream Processing Optimization
- **Unified Java Flink Job**: Consolidated multiple Python-based Flink jobs into a single, optimized Java job, significantly enhancing processing efficiency and reducing complexity. This transition resulted in a substantial throughput increase from approximately 700 messages per second to approximately 600,000 messages per second.
- **Reduced Stream Operators**: Streamlined the Flink processing pipeline by reducing the number of stream processing operators, leading to improved performance and easier maintenance.

## Redis Integration Optimization
- **Flink-Redis Pipelining**: Introduced pipelining in the Flink-to-Redis integration, improving data transfer efficiency by batching multiple commands, thereby minimizing network overhead and enhancing overall throughput. This optimization further increased the processing speed from around 600,000 messages per second to approximately 2.4 million messages per second.
- **Reduced Redis Writes**: Optimized Redis interactions by decreasing unnecessary write operations, resulting in improved performance and reduced resource consumption.
