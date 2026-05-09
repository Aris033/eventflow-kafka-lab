/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.domain.port;

import com.eventflow.auditservice.domain.model.AuditEvent;

import java.util.List;
import java.util.UUID;

public interface AuditEventRepositoryPort {

    AuditEvent save(AuditEvent auditEvent);

    boolean existsByEventId(UUID eventId);

    List<AuditEvent> findAll();

    List<AuditEvent> findByCorrelationId(UUID correlationId);

    List<AuditEvent> findByOrderId(UUID orderId);

    List<AuditEvent> findByEventType(String eventType);

    long countAll();
}
