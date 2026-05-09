/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.infrastructure.observability;

import com.eventflow.paymentservice.application.observability.PaymentMetrics;
import com.eventflow.paymentservice.domain.port.OutboxEventRepositoryPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerPaymentMetrics implements PaymentMetrics {

    private final MeterRegistry registry;
    private final Counter eventsConsumed;
    private final Counter duplicatedEvents;
    private final Counter paymentsCompleted;
    private final Counter paymentsFailed;
    private final Counter outboxEventsCreated;
    private final Counter outboxEventsPublished;
    private final Counter outboxEventsPublishFailed;

    public MicrometerPaymentMetrics(MeterRegistry registry, OutboxEventRepositoryPort outboxEventRepository) {
        this.registry = registry;
        this.eventsConsumed = registry.counter("eventflow_payment_events_consumed");
        this.duplicatedEvents = registry.counter("eventflow_payment_events_duplicated");
        this.paymentsCompleted = registry.counter("eventflow_payments_completed");
        this.paymentsFailed = registry.counter("eventflow_payments_failed");
        this.outboxEventsCreated = registry.counter("eventflow_payment_events_outbox_created");
        this.outboxEventsPublished = registry.counter("eventflow_payment_events_published");
        this.outboxEventsPublishFailed = registry.counter("eventflow_payment_events_publish_failed");
        registry.gauge("eventflow_payment_outbox_pending", outboxEventRepository, OutboxEventRepositoryPort::countPending);
    }

    @Override
    public void eventConsumed() {
        eventsConsumed.increment();
    }

    @Override
    public void duplicatedEvent() {
        duplicatedEvents.increment();
    }

    @Override
    public void paymentCompleted() {
        paymentsCompleted.increment();
    }

    @Override
    public void paymentFailed() {
        paymentsFailed.increment();
    }

    @Override
    public void outboxEventCreated() {
        outboxEventsCreated.increment();
    }

    @Override
    public void outboxEventPublished() {
        outboxEventsPublished.increment();
    }

    @Override
    public void outboxEventPublishFailed() {
        outboxEventsPublishFailed.increment();
    }

    @Override
    public void kafkaConsumerError(String topic) {
        Counter.builder("eventflow_kafka_consumer_errors")
                .tag("topic", topic)
                .register(registry)
                .increment();
    }

    @Override
    public void eventSentToDlt(String topic) {
        Counter.builder("eventflow_kafka_events_sent_to_dlt")
                .tag("topic", topic)
                .register(registry)
                .increment();
    }
}
