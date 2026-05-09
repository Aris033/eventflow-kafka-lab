/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.infrastructure.persistence;

import com.eventflow.orderservice.domain.model.Order;
import com.eventflow.orderservice.domain.port.OrderRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderJpaRepository repository;

    public JpaOrderRepositoryAdapter(SpringDataOrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return OrderPersistenceMapper.toDomain(repository.save(OrderPersistenceMapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(OrderPersistenceMapper::toDomain);
    }
}
