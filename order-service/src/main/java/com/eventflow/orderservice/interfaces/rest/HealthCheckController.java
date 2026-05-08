package com.eventflow.orderservice.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Service")
public class HealthCheckController {

    @GetMapping("/health-check")
    @Operation(summary = "Checks whether the order service is running")
    public HealthCheckResponse healthCheck() {
        return new HealthCheckResponse(
                "order-service",
                "UP",
                "Order service is running"
        );
    }

    public record HealthCheckResponse(String service, String status, String message) {
    }
}
