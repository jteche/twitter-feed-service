# Local Development Setup Guide

This guide provides step-by-step instructions for setting up the Twitter Feed Kafka Service locally.

## Prerequisites

- Docker and Docker Compose
- Java 11+
- Maven 3.9.x (wrapper included)

## Setting up Kafka

1. Create a `docker-compose.yml` file in `src/main/resources` with the following content:

```yaml
version: '3'
services:
  broker:
    image: apache/kafka:latest
    hostname: broker
    container_name: broker
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT,CONTROLLER:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://broker:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_NODE_ID: 1
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@broker:29093
      KAFKA_LISTENERS: PLAINTEXT://broker:29092,CONTROLLER://broker:29093,PLAINTEXT_HOST://0.0.0.0:9092
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LOG_DIRS: /tmp/kraft-combined-logs
      CLUSTER_ID: "MkU3OEVBNTcwNTJENDM2Qk"
```

2. Start Kafka:
```bash
cd src/main/resources
docker-compose up -d
```

3. Verify Kafka is running:
```bash
docker ps
```

4. Create the required Kafka topic:
```bash
docker exec broker /opt/kafka/bin/kafka-topics.sh --bootstrap-server broker:9092 --create --topic twitter-feed-topic --partitions 1 --replication-factor 1
```

5. List topics to verify creation:
```bash
docker exec broker /opt/kafka/bin/kafka-topics.sh --bootstrap-server broker:9092 --list
```

## Redis Setup

1. Redis will be automatically started with Docker Compose
2. Verify Redis is running:
```bash
docker ps | grep redis
```

3. Test Redis connection:
```bash
docker exec -it redis redis-cli ping
```

### Redis Troubleshooting

1. Check Redis logs:
```bash
docker logs redis
```

2. Access Redis CLI:
```bash
docker exec -it redis redis-cli
```

3. Common Redis commands:
- Check key existence: `EXISTS keyname`
- Get all keys: `KEYS *`
- Get value: `GET keyname`
- Delete key: `DEL keyname`
- Clear all data: `FLUSHALL`

## Building and Running the Application

1. Build the application:
```bash
./mvnw clean package
```

2. Run the application:
```bash
./mvnw spring-boot:run
```

The service will start on port 8084.

## Testing the Setup

1. Check if the application is running:
```bash
curl http://localhost:8084/actuator/health
```

2. Send a test message using the API:
```bash
curl -X POST http://localhost:8084/test/api/v1/publish \
  -H "Content-Type: application/json" \
  -d '{
    "type": "tweet",
    "id": "1234858592",
    "created_timestamp": "1392078023603",
    "text": "Hello World!"
  }'
```

3. Monitor metrics:
```bash
curl http://localhost:8084/actuator/prometheus
```

## Stopping the Services

1. Stop the Spring Boot application (press Ctrl+C)

2. Stop Kafka:
```bash
cd src/main/resources
docker-compose down
```

## Troubleshooting

### Common Issues

1. Port conflicts:
   - Ensure ports 8084 and 9092 are not in use
   - Change ports in `application.yaml` if needed

2. Kafka connection issues:
   - Verify Kafka is running: `docker ps`
   - Check logs: `docker logs broker`
   - Ensure bootstrap server configuration matches in both Docker Compose and application properties

3. Application startup failures:
   - Check application logs
   - Verify Java version: `java -version`
   - Ensure Maven is using correct Java version: `./mvnw -v`

### Useful Commands

- View Kafka logs:
```bash
docker logs broker
```

- Access Kafka container:
```bash
docker exec -it broker /bin/bash
```

- List all Kafka topics:
```bash
docker exec broker /opt/kafka/bin/kafka-topics.sh --bootstrap-server broker:9092 --list
```

- View messages in a topic:
```bash
docker exec broker /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server broker:9092 --topic twitter-feed-topic --from-beginning
```

- Produce test messages:
```bash
docker exec -it broker /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server broker:9092 --topic twitter-feed-topic
```

## Additional Resources

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring for Apache Kafka Documentation](https://docs.spring.io/spring-kafka/reference/html/)
- [Docker Documentation](https://docs.docker.com/)