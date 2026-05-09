/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.infrastructure.persistence;

import com.eventflow.notificationservice.domain.model.Notification;
import com.eventflow.notificationservice.domain.port.NotificationRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaNotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final SpringDataNotificationJpaRepository repository;

    public JpaNotificationRepositoryAdapter(SpringDataNotificationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification save(Notification notification) {
        return NotificationPersistenceMapper.toDomain(repository.save(NotificationPersistenceMapper.toEntity(notification)));
    }

    @Override
    public List<Notification> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId).stream()
                .map(NotificationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Notification> findAll() {
        return repository.findAll().stream()
                .map(NotificationPersistenceMapper::toDomain)
                .toList();
    }
}
