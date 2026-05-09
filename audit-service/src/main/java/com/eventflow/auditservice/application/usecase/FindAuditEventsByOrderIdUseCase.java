/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.application.usecase;

import com.eventflow.auditservice.domain.model.AuditEvent;

import java.util.List;
import java.util.UUID;

public interface FindAuditEventsByOrderIdUseCase {

    List<AuditEvent> findByOrderId(UUID orderId);
}
