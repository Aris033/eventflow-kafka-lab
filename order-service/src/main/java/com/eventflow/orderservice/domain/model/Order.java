/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final String customerId;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Order(UUID id, String customerId, BigDecimal totalAmount, OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.totalAmount = Objects.requireNonNull(totalAmount, "totalAmount must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        if (customerId.isBlank()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
        if (totalAmount.signum() <= 0) {
            throw new IllegalArgumentException("totalAmount must be greater than zero");
        }
    }

    public static Order create(String customerId, BigDecimal totalAmount) {
        Instant now = Instant.now();
        return new Order(UUID.randomUUID(), customerId, totalAmount, OrderStatus.PAYMENT_PENDING, now, now);
    }

    public static Order restore(UUID id, String customerId, BigDecimal totalAmount, OrderStatus status, Instant createdAt, Instant updatedAt) {
        return new Order(id, customerId, totalAmount, status, createdAt, updatedAt);
    }

    public UUID id() {
        return id;
    }

    public String customerId() {
        return customerId;
    }

    public BigDecimal totalAmount() {
        return totalAmount;
    }

    public OrderStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
