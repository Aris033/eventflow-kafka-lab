/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Service")
public class HealthCheckController {

    @GetMapping("/health-check")
    @Operation(summary = "Checks whether the notification service is running")
    public HealthCheckResponse healthCheck() {
        return new HealthCheckResponse(
                "notification-service",
                "UP",
                "Notification service is running"
        );
    }

    public record HealthCheckResponse(String service, String status, String message) {
    }
}
