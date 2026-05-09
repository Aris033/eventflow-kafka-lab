/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.infrastructure.observability;

import com.eventflow.auditservice.application.observability.AuditMetrics;
import com.eventflow.auditservice.domain.port.AuditEventRepositoryPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerAuditMetrics implements AuditMetrics {

    private final MeterRegistry registry;
    private final Counter eventsReceived;
    private final Counter eventsStored;
    private final Counter duplicatedEvents;
    private final Counter eventsFailed;

    public MicrometerAuditMetrics(MeterRegistry registry, AuditEventRepositoryPort auditEventRepository) {
        this.registry = registry;
        this.eventsReceived = registry.counter("eventflow_audit_events_received");
        this.eventsStored = registry.counter("eventflow_audit_events_stored");
        this.duplicatedEvents = registry.counter("eventflow_audit_events_duplicated");
        this.eventsFailed = registry.counter("eventflow_audit_events_failed");
        registry.gauge("eventflow_audit_events_total", auditEventRepository, AuditEventRepositoryPort::countAll);
    }

    @Override
    public void eventReceived() {
        eventsReceived.increment();
    }

    @Override
    public void eventStored() {
        eventsStored.increment();
    }

    @Override
    public void duplicatedEvent() {
        duplicatedEvents.increment();
    }

    @Override
    public void eventFailed() {
        eventsFailed.increment();
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
