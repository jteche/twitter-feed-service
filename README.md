# Twitter Feed Kafka Service

A Spring Boot microservice that handles Twitter feed messages using Apache Kafka for message streaming.

## Overview

This microservice provides REST endpoints to publish Twitter messages to Kafka topics and includes metrics monitoring using Prometheus. It's built with Spring Boot 2.5.12 and Java 11.

## Features

- REST API endpoints for publishing Twitter messages
- Kafka message producer implementation
- Prometheus metrics integration
- Docker support with multi-platform builds (amd64/arm64)
- GitHub Actions CI pipeline

## Prerequisites

- Java 11+
- Docker
- Apache Kafka (provided via docker-compose)
- Maven 3.9.x (wrapper included)

## Quick Start

1. Start Kafka using Docker Compose:
```bash
cd src/main/resources
docker-compose up -d
```

2. Build the application:
```bash
./mvnw clean package
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The service will start on port 8084.

## API Endpoints

### Publish Twitter Message
- `POST /test/api/v1/publish`
- `POST /test/api/v2/publish` (includes sender details)

Example payload:
```json
{
  "type": "tweet",
  "id": "1234858592",
  "created_timestamp": "1392078023603",
  "text": "Hello World!"
}
```

## Metrics

Prometheus metrics are available at:
- `/actuator/prometheus`

Available metrics:
- `number_of_tweets`: Total number of processed tweets
- `error_counts`: Number of failed message processing attempts

## Docker Support

Build the Docker image:
```bash
docker build -t twitter-feed-service .
```

Run the container:
```bash
docker run -p 8084:8084 twitter-feed-service
```

## Configuration

Application configuration is available in:
- `src/main/resources/application-dev.yaml`

Key configurations:
- Kafka bootstrap servers
- Application port
- Topic configurations

## CI/CD

The project includes GitHub Actions workflows for:
- Building and testing the application
- Creating and publishing Docker images
- Multi-platform container support (amd64/arm64)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

[Add your license here]