package com.eventflow.orderservice.infrastructure.persistence;

import com.eventflow.orderservice.domain.model.Order;

public final class OrderPersistenceMapper {

    private OrderPersistenceMapper() {
    }

    public static OrderJpaEntity toEntity(Order order) {
        return new OrderJpaEntity(
                order.id(),
                order.customerId(),
                order.totalAmount(),
                order.status(),
                order.createdAt(),
                order.updatedAt()
        );
    }

    public static Order toDomain(OrderJpaEntity entity) {
        return Order.restore(
                entity.getId(),
                entity.getCustomerId(),
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
