# EventFlow Kafka Lab

Portfolio project for a Java 21, Spring Boot 3, Maven multi-module microservices system prepared for event-driven architecture with Kafka.

## Local Infrastructure

Start the base infrastructure:

```bash
docker compose up -d
```

Stop the infrastructure:

```bash
docker compose down
```

Stop the infrastructure and remove persisted volumes:

```bash
docker compose down -v
```

Check running containers:

```bash
docker compose ps
```

## Useful URLs

- Kafka UI: http://localhost:8090
- Adminer: http://localhost:8085
- PostgreSQL: `localhost:5433`
- Kafka: `localhost:9092`

PostgreSQL is published on host port `5433` to avoid conflicts with a PostgreSQL server already running locally on `5432`. Containers still use `eventflow-postgres:5432` inside the Docker network.

## Adminer Credentials

- System: PostgreSQL
- Server: `eventflow-postgres`
- Username: `eventflow`
- Password: `eventflow`
- Database: `eventflow`

## Planned Kafka Topics

- `orders.events`
- `payments.events`
- `notifications.events`

DLT topic names are defined in `shared-events` for future use, but DLQ handling is not implemented yet.

## Planned PostgreSQL Schemas

- `order_schema`
- `payment_schema`
- `notification_schema`
- `audit_schema`

The schemas are created by `docker/postgres/init.sql` when the PostgreSQL volume is initialized for the first time.

## Build

Build all Maven modules from the repository root:

```bash
mvn clean install
```

## Current Scope

Implemented so far:

- Maven multi-module base.
- Four independent Spring Boot services.
- Shared event contract library.
- Local Docker infrastructure for PostgreSQL, Kafka, Kafka UI, and Adminer.
- Order creation with PostgreSQL persistence and Kafka event publication.
- Payment processing from Kafka events with basic idempotency and PostgreSQL persistence.
- Notification processing from payment events with basic idempotency and PostgreSQL persistence.

Not implemented yet:

- Outbox pattern.
- Advanced retries or DLQ processing.
- Advanced observability.

## Order, Payment And Notification Flow

Start the infrastructure:

```bash
docker compose up -d
```

Start `order-service`, `payment-service`, and `notification-service` from the IDE, or from the repository root:

```bash
mvn -pl order-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

Create an order that should produce a completed payment:

```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-1","totalAmount":99.99}'
```

Create an order that should produce a failed payment:

```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-2","totalAmount":1500}'
```

Query a payment by order id:

```bash
curl http://localhost:8082/api/payments/order/{orderId}
```

Query notifications by order id:

```bash
curl http://localhost:8083/api/notifications/order/{orderId}
```

Useful runtime URLs:

- Order Swagger: http://localhost:8081/swagger-ui.html
- Payment Swagger: http://localhost:8082/swagger-ui.html
- Notification Swagger: http://localhost:8083/swagger-ui.html
- Order health: http://localhost:8081/actuator/health
- Payment health: http://localhost:8082/actuator/health
- Notification health: http://localhost:8083/actuator/health
- Kafka UI: http://localhost:8090
- Adminer: http://localhost:8085

Relevant database tables:

- `payment_schema.payments`
- `payment_schema.processed_events`
- `notification_schema.notifications`
- `notification_schema.processed_events`

Expected flow:

`POST /api/orders` creates an order in `order_schema.orders`, publishes `OrderCreatedEvent` to `orders.events`, `payment-service` consumes it, stores the payment in `payment_schema.payments`, stores the processed event id in `payment_schema.processed_events`, and publishes a payment result event to `payments.events`. `notification-service` consumes payment result events, stores notifications in `notification_schema.notifications`, stores processed event ids in `notification_schema.processed_events`, and publishes notification result events to `notifications.events`.
