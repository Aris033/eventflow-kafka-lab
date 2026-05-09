/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.sharedevents;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID correlationId,
        EventType eventType,
        UUID orderId,
        String customerId,
        BigDecimal totalAmount,
        Instant occurredAt,
        int version
) implements BaseEvent {

    public OrderCreatedEvent {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(correlationId, "correlationId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(totalAmount, "totalAmount must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (eventType != EventType.ORDER_CREATED) {
            throw new IllegalArgumentException("eventType must be ORDER_CREATED");
        }
        if (customerId.isBlank()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
        if (totalAmount.signum() < 0) {
            throw new IllegalArgumentException("totalAmount must not be negative");
        }
        if (version < 1) {
            throw new IllegalArgumentException("version must be positive");
        }
    }

    public static OrderCreatedEvent create(UUID correlationId, UUID orderId, String customerId, BigDecimal totalAmount) {
        return new OrderCreatedEvent(
                UUID.randomUUID(),
                correlationId,
                EventType.ORDER_CREATED,
                orderId,
                customerId,
                totalAmount,
                Instant.now(),
                1
        );
    }
}
