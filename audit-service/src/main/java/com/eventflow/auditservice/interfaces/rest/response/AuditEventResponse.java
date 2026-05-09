/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.interfaces.rest.response;

import com.eventflow.auditservice.domain.model.AuditEvent;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(
        UUID id,
        UUID eventId,
        UUID correlationId,
        UUID orderId,
        String eventType,
        String sourceTopic,
        String messageKey,
        String payload,
        Instant occurredAt,
        Instant receivedAt
) {

    public static AuditEventResponse from(AuditEvent auditEvent) {
        return new AuditEventResponse(
                auditEvent.id(),
                auditEvent.eventId(),
                auditEvent.correlationId(),
                auditEvent.orderId(),
                auditEvent.eventType(),
                auditEvent.sourceTopic(),
                auditEvent.messageKey(),
                auditEvent.payload(),
                auditEvent.occurredAt(),
                auditEvent.receivedAt()
        );
    }
}
