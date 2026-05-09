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
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
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

DLT topic names are defined in `shared-events` and used by the Kafka consumer retry configuration.

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

Run tests:

```bash
mvn clean test
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
- Audit timeline storage for order, payment, and notification events.
- Controlled Kafka retries with Dead Letter Topics for consumer failures.
- Polling-based Transactional Outbox Pattern in producer services.
- Local observability with Spring Boot Actuator, Micrometer, Prometheus, Grafana, custom event metrics, outbox metrics, DLT metrics, and correlation-aware logs.
- Unit and application tests for domain behavior, idempotency, outbox creation, outbox publishing, and audit registration.

Not implemented yet:

- Kubernetes, API Gateway, frontend, security, CI/CD, and advanced distributed tracing.

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
mvn -pl audit-service spring-boot:run
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

Query the audit timeline by order id:

```bash
curl http://localhost:8084/api/audit/events/order/{orderId}
```

Query the audit timeline by correlation id:

```bash
curl http://localhost:8084/api/audit/events/correlation/{correlationId}
```

List all audited events:

```bash
curl http://localhost:8084/api/audit/events
```

Useful runtime URLs:

- Order Swagger: http://localhost:8081/swagger-ui.html
- Payment Swagger: http://localhost:8082/swagger-ui.html
- Notification Swagger: http://localhost:8083/swagger-ui.html
- Audit Swagger: http://localhost:8084/swagger-ui.html
- Order health: http://localhost:8081/actuator/health
- Payment health: http://localhost:8082/actuator/health
- Notification health: http://localhost:8083/actuator/health
- Audit health: http://localhost:8084/actuator/health
- Order Prometheus metrics: http://localhost:8081/actuator/prometheus
- Payment Prometheus metrics: http://localhost:8082/actuator/prometheus
- Notification Prometheus metrics: http://localhost:8083/actuator/prometheus
- Audit Prometheus metrics: http://localhost:8084/actuator/prometheus
- Kafka UI: http://localhost:8090
- Adminer: http://localhost:8085
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

Relevant database tables:

- `payment_schema.payments`
- `payment_schema.processed_events`
- `order_schema.outbox_events`
- `payment_schema.outbox_events`
- `notification_schema.notifications`
- `notification_schema.processed_events`
- `notification_schema.outbox_events`
- `audit_schema.audit_events`

Expected flow:

`POST /api/orders` creates an order in `order_schema.orders` and stores an `OrderCreatedEvent` in `order_schema.outbox_events`. The order outbox publisher sends it to `orders.events`. `payment-service` consumes it, stores the payment and processed event id, then stores a payment result event in `payment_schema.outbox_events`. The payment outbox publisher sends it to `payments.events`. `notification-service` consumes payment events, stores notifications and processed event ids, then stores notification result events in `notification_schema.outbox_events`. The notification outbox publisher sends them to `notifications.events`. `audit-service` consumes `orders.events`, `payments.events`, and `notifications.events`, and stores the complete event timeline in `audit_schema.audit_events`.

## Transactional Outbox

The Transactional Outbox Pattern stores integration events in the same database transaction as the business change. A background publisher later sends those events to Kafka, reducing the risk of inconsistencies between the database and the message broker.

This project uses a polling-based outbox in:

- `order-service`
- `payment-service`
- `notification-service`

Each producer owns its own outbox table:

- `order_schema.outbox_events`
- `payment_schema.outbox_events`
- `notification_schema.outbox_events`

Inspect outbox state from Adminer or DBeaver:

```sql
SELECT * FROM order_schema.outbox_events ORDER BY created_at DESC;
SELECT * FROM payment_schema.outbox_events ORDER BY created_at DESC;
SELECT * FROM notification_schema.outbox_events ORDER BY created_at DESC;
```

Outbox publisher settings:

```yaml
eventflow:
  outbox:
    publisher:
      enabled: true
      fixed-delay: 5000
      batch-size: 20
      max-retries: 3
```

For tests, schedulers are disabled with `eventflow.outbox.publisher.enabled=false`.

## Observability

This project includes local observability with Spring Boot Actuator, Micrometer, Prometheus and Grafana. It exposes technical and business metrics for HTTP requests, Kafka event processing, outbox publishing, DLT handling and audit timelines.

Prometheus and Grafana are part of Docker Compose:

```bash
docker compose up -d
```

The microservices are still normally started from the IDE or Maven, so Prometheus scrapes them through `host.docker.internal`:

- `host.docker.internal:8081` -> `order-service`
- `host.docker.internal:8082` -> `payment-service`
- `host.docker.internal:8083` -> `notification-service`
- `host.docker.internal:8084` -> `audit-service`

Useful observability URLs:

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Grafana username: `admin`
- Grafana password: `admin`
- Order Actuator Prometheus: http://localhost:8081/actuator/prometheus
- Payment Actuator Prometheus: http://localhost:8082/actuator/prometheus
- Notification Actuator Prometheus: http://localhost:8083/actuator/prometheus
- Audit Actuator Prometheus: http://localhost:8084/actuator/prometheus

Grafana is provisioned with Prometheus as the default datasource and an `EventFlow Overview` dashboard. The dashboard covers service health, HTTP traffic, HTTP latency, business events, outbox state, retries/DLT, JVM memory and Hikari database metrics.

Useful Prometheus queries:

```promql
eventflow_orders_created_total
eventflow_payments_completed_total
eventflow_payments_failed_total
eventflow_notifications_sent_total
eventflow_notifications_failed_total
eventflow_audit_events_stored_total
eventflow_order_outbox_pending
eventflow_payment_outbox_pending
eventflow_notification_outbox_pending
eventflow_order_events_published_total
eventflow_payment_events_published_total
eventflow_notification_events_published_total
eventflow_order_events_publish_failed_total
eventflow_payment_events_publish_failed_total
eventflow_notification_events_publish_failed_total
eventflow_kafka_consumer_errors_total
eventflow_kafka_events_sent_to_dlt_total
http_server_requests_seconds_count
jvm_memory_used_bytes
hikaricp_connections_active
```

To see custom metrics move, create a normal order and a payment-failed order:

```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-1","totalAmount":99.99}'

curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"customer-2","totalAmount":1500}'
```

Micrometer is the instrumentation facade used by the services. Prometheus scrapes the `/actuator/prometheus` endpoints, and Grafana visualizes the scraped metrics. Business counters track orders, payments, notifications, audit events, outbox publishing, consumer errors and DLT sends.

Logs include `service`, `correlationId`, `eventId` and `orderId` when an event is being processed. These IDs are added through MDC in Kafka consumers so related log lines can be followed without turning high-cardinality IDs into metric tags. Metrics intentionally avoid `orderId`, `eventId` and `correlationId` tags because those values would create too many time series.

## Audit Timeline

`audit-service` stores every event with its `eventId`, `correlationId`, `orderId`, `eventType`, source topic, Kafka key, original JSON payload, event time, and receive time.

Use the audit endpoints to reconstruct the event timeline of an order or a correlation:

```bash
curl http://localhost:8084/api/audit/events/order/{orderId}
curl http://localhost:8084/api/audit/events/correlation/{correlationId}
curl http://localhost:8084/api/audit/events/type/ORDER_CREATED
```

## Retries And DLT

Kafka consumers use controlled retries with a fixed 1 second backoff and 3 total delivery attempts. When processing still fails, the message is published to the matching Dead Letter Topic:

- `orders.events` -> `orders.events.dlt`
- `payments.events` -> `payments.events.dlt`
- `notifications.events` -> `notifications.events.dlt`

DLT messages can be inspected in Kafka UI at http://localhost:8090.

Controlled failure examples:

- Payment processing: create an order with `customerId` starting with `fail-payment-processing` to send the `OrderCreatedEvent` to `orders.events.dlt` after retries.
- Notification processing: set `eventflow.notification.simulation.recipient-override=fail@eventflow.local` to force payment events into `payments.events.dlt`.
- Audit processing: set `eventflow.audit.simulation.fail-on-event-type=ORDER_CREATED` or another event type to force events from the source topic into the matching DLT.
