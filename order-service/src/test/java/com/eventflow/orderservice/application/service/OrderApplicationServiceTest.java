package com.eventflow.orderservice.application.service;

import com.eventflow.orderservice.application.observability.OrderMetrics;
import com.eventflow.orderservice.domain.model.Order;
import com.eventflow.orderservice.domain.model.OutboxEvent;
import com.eventflow.orderservice.domain.port.OrderRepositoryPort;
import com.eventflow.orderservice.domain.port.OutboxEventRepositoryPort;
import com.eventflow.sharedevents.EventTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderApplicationServiceTest {

    private final OrderRepositoryPort orderRepository = mock(OrderRepositoryPort.class);
    private final OutboxEventRepositoryPort outboxEventRepository = mock(OutboxEventRepositoryPort.class);
    private final OrderMetrics orderMetrics = mock(OrderMetrics.class);
    private final OrderApplicationService service = new OrderApplicationService(
            orderRepository,
            outboxEventRepository,
            new ObjectMapper().findAndRegisterModules(),
            orderMetrics
    );

    @Test
    void createOrderSavesOrderAndOutboxEvent() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outboxEventRepository.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = service.createOrder("customer-1", new BigDecimal("99.99"));

        assertThat(order.customerId()).isEqualTo("customer-1");
        verify(orderRepository).save(any(Order.class));
        verify(orderMetrics).orderCreated();
        verify(orderMetrics).outboxEventCreated();
        verify(outboxEventRepository).save(org.mockito.ArgumentMatchers.argThat(event ->
                event.topic().equals(EventTopics.ORDERS_EVENTS)
                        && event.aggregateId().equals(order.id())
                        && event.messageKey().equals(order.id().toString())
                        && event.eventType().equals("ORDER_CREATED")
        ));
    }
}
