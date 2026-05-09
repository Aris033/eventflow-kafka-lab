/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.application.service;

import com.eventflow.orderservice.application.usecase.CreateOrderUseCase;
import com.eventflow.orderservice.application.usecase.GetOrderUseCase;
import com.eventflow.orderservice.application.observability.OrderMetrics;
import com.eventflow.orderservice.domain.exception.OrderNotFoundException;
import com.eventflow.orderservice.domain.model.Order;
import com.eventflow.orderservice.domain.model.OutboxEvent;
import com.eventflow.orderservice.domain.port.OrderRepositoryPort;
import com.eventflow.orderservice.domain.port.OutboxEventRepositoryPort;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.OrderCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrderApplicationService.class);

    private final OrderRepositoryPort orderRepository;
    private final OutboxEventRepositoryPort outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final OrderMetrics orderMetrics;

    public OrderApplicationService(
            OrderRepositoryPort orderRepository,
            OutboxEventRepositoryPort outboxEventRepository,
            ObjectMapper objectMapper,
            OrderMetrics orderMetrics
    ) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.orderMetrics = orderMetrics;
    }

    @Override
    @Transactional
    public Order createOrder(String customerId, BigDecimal totalAmount) {
        Order order = orderRepository.save(Order.create(customerId, totalAmount));
        orderMetrics.orderCreated();
        log.info("Order created: orderId={}, customerId={}, totalAmount={}", order.id(), order.customerId(), order.totalAmount());

        OrderCreatedEvent event = OrderCreatedEvent.create(
                order.id(),
                order.id(),
                order.customerId(),
                order.totalAmount()
        );
        outboxEventRepository.save(OutboxEvent.pending(
                order.id(),
                "ORDER",
                event.eventId(),
                event.eventType().name(),
                EventTopics.ORDERS_EVENTS,
                order.id().toString(),
                serialize(event)
        ));
        orderMetrics.outboxEventCreated();
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private String serialize(OrderCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize OrderCreatedEvent", ex);
        }
    }
}
