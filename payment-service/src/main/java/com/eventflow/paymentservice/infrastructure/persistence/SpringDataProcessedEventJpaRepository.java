package com.eventflow.paymentservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataProcessedEventJpaRepository extends JpaRepository<ProcessedEventJpaEntity, UUID> {
}
