/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.interfaces.rest;

import com.eventflow.auditservice.application.usecase.FindAuditEventsByCorrelationIdUseCase;
import com.eventflow.auditservice.application.usecase.FindAuditEventsByOrderIdUseCase;
import com.eventflow.auditservice.application.usecase.FindAuditEventsUseCase;
import com.eventflow.auditservice.interfaces.rest.response.AuditEventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit/events")
@Tag(name = "Audit Events")
public class AuditController {

    private final FindAuditEventsUseCase findAuditEventsUseCase;
    private final FindAuditEventsByCorrelationIdUseCase findAuditEventsByCorrelationIdUseCase;
    private final FindAuditEventsByOrderIdUseCase findAuditEventsByOrderIdUseCase;

    public AuditController(
            FindAuditEventsUseCase findAuditEventsUseCase,
            FindAuditEventsByCorrelationIdUseCase findAuditEventsByCorrelationIdUseCase,
            FindAuditEventsByOrderIdUseCase findAuditEventsByOrderIdUseCase
    ) {
        this.findAuditEventsUseCase = findAuditEventsUseCase;
        this.findAuditEventsByCorrelationIdUseCase = findAuditEventsByCorrelationIdUseCase;
        this.findAuditEventsByOrderIdUseCase = findAuditEventsByOrderIdUseCase;
    }

    @GetMapping
    @Operation(summary = "Lists audited events ordered by receivedAt descending")
    public List<AuditEventResponse> findAll() {
        return findAuditEventsUseCase.findAll().stream()
                .map(AuditEventResponse::from)
                .toList();
    }

    @GetMapping("/correlation/{correlationId}")
    @Operation(summary = "Gets the event timeline by correlation id")
    public List<AuditEventResponse> findByCorrelationId(@PathVariable("correlationId") UUID correlationId) {
        return findAuditEventsByCorrelationIdUseCase.findByCorrelationId(correlationId).stream()
                .map(AuditEventResponse::from)
                .toList();
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Gets the event timeline by order id")
    public List<AuditEventResponse> findByOrderId(@PathVariable("orderId") UUID orderId) {
        return findAuditEventsByOrderIdUseCase.findByOrderId(orderId).stream()
                .map(AuditEventResponse::from)
                .toList();
    }

    @GetMapping("/type/{eventType}")
    @Operation(summary = "Gets audited events by event type")
    public List<AuditEventResponse> findByEventType(@PathVariable("eventType") String eventType) {
        return findAuditEventsUseCase.findByEventType(eventType).stream()
                .map(AuditEventResponse::from)
                .toList();
    }
}
