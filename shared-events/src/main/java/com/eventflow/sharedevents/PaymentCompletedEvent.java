package com.eventflow.sharedevents;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID correlationId,
        EventType eventType,
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        Instant occurredAt,
        int version
) implements BaseEvent {

    public PaymentCompletedEvent {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(correlationId, "correlationId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(paymentId, "paymentId must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (eventType != EventType.PAYMENT_COMPLETED) {
            throw new IllegalArgumentException("eventType must be PAYMENT_COMPLETED");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        if (version < 1) {
            throw new IllegalArgumentException("version must be positive");
        }
    }

    public static PaymentCompletedEvent create(UUID correlationId, UUID paymentId, UUID orderId, BigDecimal amount) {
        return new PaymentCompletedEvent(
                UUID.randomUUID(),
                correlationId,
                EventType.PAYMENT_COMPLETED,
                paymentId,
                orderId,
                amount,
                Instant.now(),
                1
        );
    }
}
