/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Payment {

    private static final BigDecimal PAYMENT_LIMIT = new BigDecimal("1000.00");

    private final UUID id;
    private final UUID orderId;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String failureReason;
    private final Instant createdAt;

    private Payment(UUID id, UUID orderId, BigDecimal amount, PaymentStatus status, String failureReason, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.failureReason = failureReason;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (status == PaymentStatus.FAILED && (failureReason == null || failureReason.isBlank())) {
            throw new IllegalArgumentException("failureReason is required for failed payments");
        }
    }

    public static Payment process(UUID orderId, BigDecimal amount) {
        if (amount.compareTo(PAYMENT_LIMIT) > 0) {
            return failed(orderId, amount, "Amount exceeds payment limit");
        }
        return completed(orderId, amount);
    }

    public static Payment completed(UUID orderId, BigDecimal amount) {
        return new Payment(UUID.randomUUID(), orderId, amount, PaymentStatus.COMPLETED, null, Instant.now());
    }

    public static Payment failed(UUID orderId, BigDecimal amount, String failureReason) {
        return new Payment(UUID.randomUUID(), orderId, amount, PaymentStatus.FAILED, failureReason, Instant.now());
    }

    public static Payment restore(UUID id, UUID orderId, BigDecimal amount, PaymentStatus status, String failureReason, Instant createdAt) {
        return new Payment(id, orderId, amount, status, failureReason, createdAt);
    }

    public UUID id() {
        return id;
    }

    public UUID orderId() {
        return orderId;
    }

    public BigDecimal amount() {
        return amount;
    }

    public PaymentStatus status() {
        return status;
    }

    public String failureReason() {
        return failureReason;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
