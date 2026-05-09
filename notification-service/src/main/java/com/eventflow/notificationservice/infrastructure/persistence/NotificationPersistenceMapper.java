/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.infrastructure.persistence;

import com.eventflow.notificationservice.domain.model.Notification;

public final class NotificationPersistenceMapper {

    private NotificationPersistenceMapper() {
    }

    public static NotificationJpaEntity toEntity(Notification notification) {
        return new NotificationJpaEntity(
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

    public static Notification toDomain(NotificationJpaEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getOrderId(),
                entity.getRecipient(),
                entity.getChannel(),
                entity.getStatus(),
                entity.getMessage(),
                entity.getFailureReason(),
                entity.getCreatedAt()
        );
    }
}
