package com.eventflow.orderservice.application.usecase;

import com.eventflow.orderservice.domain.model.Order;

import java.util.UUID;

public interface GetOrderUseCase {

    Order getOrder(UUID orderId);
}
