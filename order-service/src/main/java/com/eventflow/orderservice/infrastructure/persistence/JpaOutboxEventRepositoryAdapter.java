/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.infrastructure.persistence;

import com.eventflow.orderservice.domain.model.OutboxEvent;
import com.eventflow.orderservice.domain.model.OutboxEventStatus;
import com.eventflow.orderservice.domain.port.OutboxEventRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaOutboxEventRepositoryAdapter implements OutboxEventRepositoryPort {

    private final SpringDataOutboxEventJpaRepository repository;

    public JpaOutboxEventRepositoryAdapter(SpringDataOutboxEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return OutboxEventPersistenceMapper.toDomain(repository.save(OutboxEventPersistenceMapper.toEntity(outboxEvent)));
    }

    @Override
    public List<OutboxEvent> findPublishable(int maxRetries, int batchSize) {
        return repository.findByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
                        List.of(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED),
                        maxRetries,
                        PageRequest.of(0, batchSize)
                ).stream()
                .map(OutboxEventPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countPending() {
        return repository.countByStatus(OutboxEventStatus.PENDING);
    }
}
