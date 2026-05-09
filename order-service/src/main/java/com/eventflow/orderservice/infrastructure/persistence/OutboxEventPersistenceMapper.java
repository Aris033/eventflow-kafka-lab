/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.infrastructure.persistence;

import com.eventflow.orderservice.domain.model.OutboxEvent;

public final class OutboxEventPersistenceMapper {

    private OutboxEventPersistenceMapper() {
    }

    public static OutboxEventJpaEntity toEntity(OutboxEvent event) {
        return new OutboxEventJpaEntity(event.id(), event.aggregateId(), event.aggregateType(), event.eventId(),
                event.eventType(), event.topic(), event.messageKey(), event.payload(), event.status(),
                event.createdAt(), event.publishedAt(), event.retryCount(), event.lastError());
    }

    public static OutboxEvent toDomain(OutboxEventJpaEntity entity) {
        return new OutboxEvent(entity.getId(), entity.getAggregateId(), entity.getAggregateType(), entity.getEventId(),
                entity.getEventType(), entity.getTopic(), entity.getMessageKey(), entity.getPayload(),
                entity.getStatus(), entity.getCreatedAt(), entity.getPublishedAt(), entity.getRetryCount(),
                entity.getLastError());
    }
}
