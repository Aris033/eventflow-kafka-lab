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
import com.eventflow.auditservice.domain.port.AuditEventRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaAuditEventRepositoryAdapter implements AuditEventRepositoryPort {

    private final SpringDataAuditEventJpaRepository repository;

    public JpaAuditEventRepositoryAdapter(SpringDataAuditEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditEvent save(AuditEvent auditEvent) {
        return AuditEventPersistenceMapper.toDomain(repository.save(AuditEventPersistenceMapper.toEntity(auditEvent)));
    }

    @Override
    public boolean existsByEventId(UUID eventId) {
        return repository.existsByEventId(eventId);
    }

    @Override
    public List<AuditEvent> findAll() {
        return repository.findAllByOrderByReceivedAtDesc().stream()
                .map(AuditEventPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<AuditEvent> findByCorrelationId(UUID correlationId) {
        return repository.findByCorrelationIdOrderByReceivedAtAsc(correlationId).stream()
                .map(AuditEventPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<AuditEvent> findByOrderId(UUID orderId) {
        return repository.findByOrderIdOrderByReceivedAtAsc(orderId).stream()
                .map(AuditEventPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<AuditEvent> findByEventType(String eventType) {
        return repository.findByEventTypeOrderByReceivedAtAsc(eventType).stream()
                .map(AuditEventPersistenceMapper::toDomain)
                .toList();
    }
}
