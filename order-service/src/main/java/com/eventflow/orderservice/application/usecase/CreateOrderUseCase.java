package com.eventflow.orderservice.application.usecase;

import com.eventflow.orderservice.domain.model.Order;

import java.math.BigDecimal;

public interface CreateOrderUseCase {

    Order createOrder(String customerId, BigDecimal totalAmount);
}
