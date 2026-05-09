/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.infrastructure.persistence;

import com.eventflow.paymentservice.domain.model.Payment;
import com.eventflow.paymentservice.domain.port.PaymentRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final SpringDataPaymentJpaRepository repository;

    public JpaPaymentRepositoryAdapter(SpringDataPaymentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        return PaymentPersistenceMapper.toDomain(repository.save(PaymentPersistenceMapper.toEntity(payment)));
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId).map(PaymentPersistenceMapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return repository.findAll().stream()
                .map(PaymentPersistenceMapper::toDomain)
                .toList();
    }
}
