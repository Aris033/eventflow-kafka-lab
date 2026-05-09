/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.interfaces.rest.response;

import com.eventflow.notificationservice.domain.model.Notification;
import com.eventflow.notificationservice.domain.model.NotificationChannel;
import com.eventflow.notificationservice.domain.model.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID orderId,
        String recipient,
        NotificationChannel channel,
        NotificationStatus status,
        String message,
        String failureReason,
        Instant createdAt
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.id(),
                notification.orderId(),
                notification.recipient(),
                notification.channel(),
                notification.status(),
                notification.message(),
                notification.failureReason(),
                notification.createdAt()
        );
    }
}
