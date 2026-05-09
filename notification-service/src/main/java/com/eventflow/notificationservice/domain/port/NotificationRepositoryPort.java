/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.domain.port;

import com.eventflow.notificationservice.domain.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryPort {

    Notification save(Notification notification);

    List<Notification> findByOrderId(UUID orderId);

    List<Notification> findAll();
}
