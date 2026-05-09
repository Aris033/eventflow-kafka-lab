package com.eventflow.orderservice.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void createBuildsPaymentPendingOrder() {
        Order order = Order.create("customer-1", new BigDecimal("99.99"));

        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo("customer-1");
        assertThat(order.totalAmount()).isEqualByComparingTo("99.99");
        assertThat(order.status()).isEqualTo(OrderStatus.PAYMENT_PENDING);
        assertThat(order.createdAt()).isNotNull();
        assertThat(order.updatedAt()).isNotNull();
    }

    @Test
    void createRejectsInvalidAmount() {
        assertThatThrownBy(() -> Order.create("customer-1", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
