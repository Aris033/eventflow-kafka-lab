/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.infrastructure.persistence;

import com.eventflow.auditservice.domain.model.AuditEvent;

public final class AuditEventPersistenceMapper {

    private AuditEventPersistenceMapper() {
    }

    public static AuditEventJpaEntity toEntity(AuditEvent auditEvent) {
        return new AuditEventJpaEntity(
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

    public static AuditEvent toDomain(AuditEventJpaEntity entity) {
        return new AuditEvent(
                entity.getId(),
                entity.getEventId(),
                entity.getCorrelationId(),
                entity.getOrderId(),
                entity.getEventType(),
                entity.getSourceTopic(),
                entity.getMessageKey(),
                entity.getPayload(),
                entity.getOccurredAt(),
                entity.getReceivedAt()
        );
    }
}
