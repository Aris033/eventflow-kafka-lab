/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Service")
public class HealthCheckController {

    @GetMapping("/health-check")
    @Operation(summary = "Checks whether the payment service is running")
    public HealthCheckResponse healthCheck() {
        return new HealthCheckResponse(
                "payment-service",
                "UP",
                "Payment service is running"
        );
    }

    public record HealthCheckResponse(String service, String status, String message) {
    }
}
