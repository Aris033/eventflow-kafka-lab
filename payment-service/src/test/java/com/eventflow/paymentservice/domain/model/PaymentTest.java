package com.eventflow.paymentservice.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    void processCompletesPaymentWithinLimit() {
        Payment payment = Payment.process(UUID.randomUUID(), new BigDecimal("99.99"));

        assertThat(payment.status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.failureReason()).isNull();
    }

    @Test
    void processFailsPaymentAboveLimit() {
        Payment payment = Payment.process(UUID.randomUUID(), new BigDecimal("1500"));

        assertThat(payment.status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.failureReason()).isEqualTo("Amount exceeds payment limit");
    }
}
