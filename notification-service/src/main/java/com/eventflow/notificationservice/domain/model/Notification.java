/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Notification(
        UUID id,
        UUID orderId,
        String recipient,
        NotificationChannel channel,
        NotificationStatus status,
        String message,
        String failureReason,
        Instant createdAt
) {

    private static final String FAILURE_REASON = "Recipient is not deliverable";

    public Notification {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        if (message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }

    public static Notification send(UUID orderId, String recipient, String message) {
        String safeRecipient = recipient == null ? "" : recipient;
        NotificationStatus status = isDeliverable(safeRecipient) ? NotificationStatus.SENT : NotificationStatus.FAILED;
        String failureReason = status == NotificationStatus.FAILED ? FAILURE_REASON : null;
        return new Notification(
                UUID.randomUUID(),
                orderId,
                safeRecipient,
                NotificationChannel.EMAIL,
                status,
                message,
                failureReason,
                Instant.now()
        );
    }

    private static boolean isDeliverable(String recipient) {
        return recipient != null && !recipient.isBlank() && !recipient.toLowerCase().contains("fail");
    }
}
