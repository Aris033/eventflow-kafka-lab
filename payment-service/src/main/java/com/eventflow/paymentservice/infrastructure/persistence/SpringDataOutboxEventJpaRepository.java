package com.eventflow.paymentservice.infrastructure.persistence;

import com.eventflow.paymentservice.domain.model.OutboxEventStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SpringDataOutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
    List<OutboxEventJpaEntity> findByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
            Collection<OutboxEventStatus> statuses, int maxRetries, Pageable pageable);

    long countByStatus(OutboxEventStatus status);
}
