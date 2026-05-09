/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record AuditEvent(
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

    public AuditEvent {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(correlationId, "correlationId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(sourceTopic, "sourceTopic must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        Objects.requireNonNull(receivedAt, "receivedAt must not be null");
    }
}
