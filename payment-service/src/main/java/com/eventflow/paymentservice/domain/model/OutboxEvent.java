package com.eventflow.paymentservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OutboxEvent(UUID id, UUID aggregateId, String aggregateType, UUID eventId, String eventType,
                          String topic, String messageKey, String payload, OutboxEventStatus status,
                          Instant createdAt, Instant publishedAt, int retryCount, String lastError) {

    public OutboxEvent {
        Objects.requireNonNull(id);
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(aggregateType);
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(topic);
        Objects.requireNonNull(messageKey);
        Objects.requireNonNull(payload);
        Objects.requireNonNull(status);
        Objects.requireNonNull(createdAt);
    }

    public static OutboxEvent pending(UUID aggregateId, String aggregateType, UUID eventId, String eventType,
                                      String topic, String messageKey, String payload) {
        return new OutboxEvent(UUID.randomUUID(), aggregateId, aggregateType, eventId, eventType, topic,
                messageKey, payload, OutboxEventStatus.PENDING, Instant.now(), null, 0, null);
    }

    public OutboxEvent markPublished() {
        return new OutboxEvent(id, aggregateId, aggregateType, eventId, eventType, topic, messageKey, payload,
                OutboxEventStatus.PUBLISHED, createdAt, Instant.now(), retryCount, null);
    }

    public OutboxEvent markFailed(String error, int maxRetries) {
        int nextRetryCount = retryCount + 1;
        OutboxEventStatus nextStatus = nextRetryCount >= maxRetries ? OutboxEventStatus.FAILED : OutboxEventStatus.PENDING;
        return new OutboxEvent(id, aggregateId, aggregateType, eventId, eventType, topic, messageKey, payload,
                nextStatus, createdAt, publishedAt, nextRetryCount, error);
    }
}
