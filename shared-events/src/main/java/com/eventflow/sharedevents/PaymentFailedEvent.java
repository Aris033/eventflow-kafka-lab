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

public record PaymentFailedEvent(
        UUID eventId,
        UUID correlationId,
        EventType eventType,
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        String reason,
        Instant occurredAt,
        int version
) implements BaseEvent {

    public PaymentFailedEvent {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(correlationId, "correlationId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(paymentId, "paymentId must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(reason, "reason must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (eventType != EventType.PAYMENT_FAILED) {
            throw new IllegalArgumentException("eventType must be PAYMENT_FAILED");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        if (reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        if (version < 1) {
            throw new IllegalArgumentException("version must be positive");
        }
    }

    public static PaymentFailedEvent create(UUID correlationId, UUID paymentId, UUID orderId, BigDecimal amount, String reason) {
        return new PaymentFailedEvent(
                UUID.randomUUID(),
                correlationId,
                EventType.PAYMENT_FAILED,
                paymentId,
                orderId,
                amount,
                reason,
                Instant.now(),
                1
        );
    }
}
