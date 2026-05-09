/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit Service")
public class HealthCheckController {

    @GetMapping("/health-check")
    @Operation(summary = "Checks whether the audit service is running")
    public HealthCheckResponse healthCheck() {
        return new HealthCheckResponse(
                "audit-service",
                "UP",
                "Audit service is running"
        );
    }

    public record HealthCheckResponse(String service, String status, String message) {
    }
}
