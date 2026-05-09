/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.infrastructure.messaging;

import com.eventflow.paymentservice.application.usecase.ProcessPaymentUseCase;
import com.eventflow.paymentservice.application.observability.PaymentMetrics;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentMetrics paymentMetrics;

    public OrderCreatedEventConsumer(ProcessPaymentUseCase processPaymentUseCase, PaymentMetrics paymentMetrics) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.paymentMetrics = paymentMetrics;
    }

    @KafkaListener(
            topics = EventTopics.ORDERS_EVENTS,
            groupId = "payment-service",
            containerFactory = "orderCreatedEventKafkaListenerContainerFactory"
    )
    public void consume(OrderCreatedEvent event) {
        try {
            putMdc(event);
            paymentMetrics.eventConsumed();
            log.info(
                    "OrderCreatedEvent received: eventId={}, correlationId={}, orderId={}",
                    event.eventId(),
                    event.correlationId(),
                    event.orderId()
            );
            processPaymentUseCase.process(event);
        } finally {
            MDC.clear();
        }
    }

    private void putMdc(OrderCreatedEvent event) {
        MDC.put("correlationId", event.correlationId().toString());
        MDC.put("eventId", event.eventId().toString());
        MDC.put("orderId", event.orderId().toString());
    }
}
