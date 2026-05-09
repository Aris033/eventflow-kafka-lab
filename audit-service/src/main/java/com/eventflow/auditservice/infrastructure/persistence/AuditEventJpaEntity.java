/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events", schema = "audit_schema")
public class AuditEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "source_topic", nullable = false)
    private String sourceTopic;

    @Column(name = "message_key")
    private String messageKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    protected AuditEventJpaEntity() {
    }

    public AuditEventJpaEntity(
            UUID id,
            UUID eventId,
            UUID correlationId,
            UUID orderId,
            String eventType,
            String sourceTopic,
            String messageKey,
            String payload,
            Instant occurredAt,
            Instant receivedAt
    ) {
        this.id = id;
        this.eventId = eventId;
        this.correlationId = correlationId;
        this.orderId = orderId;
        this.eventType = eventType;
        this.sourceTopic = sourceTopic;
        this.messageKey = messageKey;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSourceTopic() {
        return sourceTopic;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
