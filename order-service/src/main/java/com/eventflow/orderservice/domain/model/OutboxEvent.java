/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OutboxEvent(
        UUID id,
        UUID aggregateId,
        String aggregateType,
        UUID eventId,
        String eventType,
        String topic,
        String messageKey,
        String payload,
        OutboxEventStatus status,
        Instant createdAt,
        Instant publishedAt,
        int retryCount,
        String lastError
) {

    public OutboxEvent {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(aggregateId, "aggregateId must not be null");
        Objects.requireNonNull(aggregateType, "aggregateType must not be null");
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(topic, "topic must not be null");
        Objects.requireNonNull(messageKey, "messageKey must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static OutboxEvent pending(
            UUID aggregateId,
            String aggregateType,
            UUID eventId,
            String eventType,
            String topic,
            String messageKey,
            String payload
    ) {
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateId,
                aggregateType,
                eventId,
                eventType,
                topic,
                messageKey,
                payload,
                OutboxEventStatus.PENDING,
                Instant.now(),
                null,
                0,
                null
        );
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
