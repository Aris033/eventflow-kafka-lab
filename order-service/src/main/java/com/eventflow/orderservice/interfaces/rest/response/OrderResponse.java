package com.eventflow.orderservice.interfaces.rest.response;

import com.eventflow.orderservice.domain.model.Order;
import com.eventflow.orderservice.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.id(),
                order.customerId(),
                order.totalAmount(),
                order.status(),
                order.createdAt(),
                order.updatedAt()
        );
    }
}
