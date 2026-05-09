package com.eventflow.paymentservice.interfaces.rest.response;

import com.eventflow.paymentservice.domain.model.Payment;
import com.eventflow.paymentservice.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        PaymentStatus status,
        String failureReason,
        Instant createdAt
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.id(),
                payment.orderId(),
                payment.amount(),
                payment.status(),
                payment.failureReason(),
                payment.createdAt()
        );
    }
}
