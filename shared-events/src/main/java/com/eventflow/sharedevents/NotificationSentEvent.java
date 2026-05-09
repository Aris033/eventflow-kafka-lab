package com.eventflow.sharedevents;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record NotificationSentEvent(
        UUID eventId,
        UUID correlationId,
        EventType eventType,
        UUID notificationId,
        UUID orderId,
        String channel,
        String recipient,
        Instant occurredAt,
        int version
) implements BaseEvent {

    public NotificationSentEvent {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(correlationId, "correlationId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(notificationId, "notificationId must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (eventType != EventType.NOTIFICATION_SENT) {
            throw new IllegalArgumentException("eventType must be NOTIFICATION_SENT");
        }
        if (channel.isBlank()) {
            throw new IllegalArgumentException("channel must not be blank");
        }
        if (recipient.isBlank()) {
            throw new IllegalArgumentException("recipient must not be blank");
        }
        if (version < 1) {
            throw new IllegalArgumentException("version must be positive");
        }
    }

    public static NotificationSentEvent create(UUID correlationId, UUID notificationId, UUID orderId, String channel, String recipient) {
        return new NotificationSentEvent(
                UUID.randomUUID(),
                correlationId,
                EventType.NOTIFICATION_SENT,
                notificationId,
                orderId,
                channel,
                recipient,
                Instant.now(),
                1
        );
    }
}
