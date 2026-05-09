package com.eventflow.paymentservice.infrastructure.persistence;

import com.eventflow.paymentservice.domain.model.Payment;

public final class PaymentPersistenceMapper {

    private PaymentPersistenceMapper() {
    }

    public static PaymentJpaEntity toEntity(Payment payment) {
        return new PaymentJpaEntity(
                payment.id(),
                payment.orderId(),
                payment.amount(),
                payment.status(),
                payment.failureReason(),
                payment.createdAt()
        );
    }

    public static Payment toDomain(PaymentJpaEntity entity) {
        return Payment.restore(
                entity.getId(),
                entity.getOrderId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getFailureReason(),
                entity.getCreatedAt()
        );
    }
}
