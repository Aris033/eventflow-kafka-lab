package com.eventflow.sharedevents;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record NotificationFailedEvent(
        UUID eventId,
        UUID correlationId,
        EventType eventType,
        UUID notificationId,
        UUID orderId,
        String channel,
        String recipient,
        String reason,
        Instant occurredAt,
        int version
) implements BaseEvent {

    public NotificationFailedEvent {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(correlationId, "correlationId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(notificationId, "notificationId must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(reason, "reason must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (eventType != EventType.NOTIFICATION_FAILED) {
            throw new IllegalArgumentException("eventType must be NOTIFICATION_FAILED");
        }
        if (channel.isBlank()) {
            throw new IllegalArgumentException("channel must not be blank");
        }
        if (recipient.isBlank()) {
            throw new IllegalArgumentException("recipient must not be blank");
        }
        if (reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        if (version < 1) {
            throw new IllegalArgumentException("version must be positive");
        }
    }

    public static NotificationFailedEvent create(UUID correlationId, UUID notificationId, UUID orderId, String channel, String recipient, String reason) {
        return new NotificationFailedEvent(
                UUID.randomUUID(),
                correlationId,
                EventType.NOTIFICATION_FAILED,
                notificationId,
                orderId,
                channel,
                recipient,
                reason,
                Instant.now(),
                1
        );
    }
}
