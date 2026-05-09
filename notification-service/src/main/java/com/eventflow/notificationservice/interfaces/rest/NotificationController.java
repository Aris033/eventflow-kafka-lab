/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.interfaces.rest;

import com.eventflow.notificationservice.application.usecase.GetNotificationsUseCase;
import com.eventflow.notificationservice.application.usecase.ListNotificationsUseCase;
import com.eventflow.notificationservice.interfaces.rest.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications")
public class NotificationController {

    private final GetNotificationsUseCase getNotificationsUseCase;
    private final ListNotificationsUseCase listNotificationsUseCase;

    public NotificationController(
            GetNotificationsUseCase getNotificationsUseCase,
            ListNotificationsUseCase listNotificationsUseCase
    ) {
        this.getNotificationsUseCase = getNotificationsUseCase;
        this.listNotificationsUseCase = listNotificationsUseCase;
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Gets notifications by order id")
    public List<NotificationResponse> getNotificationsByOrderId(@PathVariable("orderId") UUID orderId) {
        return getNotificationsUseCase.getNotificationsByOrderId(orderId).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @GetMapping
    @Operation(summary = "Lists processed notifications")
    public List<NotificationResponse> listNotifications() {
        return listNotificationsUseCase.listNotifications().stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
