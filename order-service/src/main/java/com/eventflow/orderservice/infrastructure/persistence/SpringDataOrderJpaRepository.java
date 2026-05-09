package com.eventflow.orderservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataOrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {
}
