/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.application.usecase;

import com.eventflow.notificationservice.domain.model.Notification;

import java.util.List;

public interface ListNotificationsUseCase {

    List<Notification> listNotifications();
}
