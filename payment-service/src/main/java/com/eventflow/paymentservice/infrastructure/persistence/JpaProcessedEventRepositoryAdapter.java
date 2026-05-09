/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.infrastructure.persistence;

import com.eventflow.paymentservice.domain.port.ProcessedEventRepositoryPort;
import com.eventflow.sharedevents.EventType;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public class JpaProcessedEventRepositoryAdapter implements ProcessedEventRepositoryPort {

    private final SpringDataProcessedEventJpaRepository repository;

    public JpaProcessedEventRepositoryAdapter(SpringDataProcessedEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEventId(UUID eventId) {
        return repository.existsById(eventId);
    }

    @Override
    public void save(UUID eventId, EventType eventType) {
        repository.save(new ProcessedEventJpaEntity(eventId, eventType, Instant.now()));
    }
}
