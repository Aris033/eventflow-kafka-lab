package com.eventflow.orderservice.application.service;

import com.eventflow.orderservice.application.usecase.CreateOrderUseCase;
import com.eventflow.orderservice.application.usecase.GetOrderUseCase;
import com.eventflow.orderservice.domain.exception.OrderNotFoundException;
import com.eventflow.orderservice.domain.model.Order;
import com.eventflow.orderservice.domain.port.OrderEventPublisherPort;
import com.eventflow.orderservice.domain.port.OrderRepositoryPort;
import com.eventflow.sharedevents.OrderCreatedEvent;
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
    private final OrderEventPublisherPort orderEventPublisher;

    public OrderApplicationService(OrderRepositoryPort orderRepository, OrderEventPublisherPort orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public Order createOrder(String customerId, BigDecimal totalAmount) {
        Order order = orderRepository.save(Order.create(customerId, totalAmount));
        log.info("Order created: orderId={}, customerId={}, totalAmount={}", order.id(), order.customerId(), order.totalAmount());

        OrderCreatedEvent event = OrderCreatedEvent.create(
                order.id(),
                order.id(),
                order.customerId(),
                order.totalAmount()
        );
        orderEventPublisher.publish(event);
        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
