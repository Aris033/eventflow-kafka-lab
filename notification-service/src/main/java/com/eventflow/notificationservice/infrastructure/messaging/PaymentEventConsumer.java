/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.infrastructure.messaging;

import com.eventflow.notificationservice.application.observability.NotificationMetrics;
import com.eventflow.notificationservice.application.usecase.SendPaymentCompletedNotificationUseCase;
import com.eventflow.notificationservice.application.usecase.SendPaymentFailedNotificationUseCase;
import com.eventflow.sharedevents.BaseEvent;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.EventType;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final SendPaymentCompletedNotificationUseCase sendPaymentCompletedNotificationUseCase;
    private final SendPaymentFailedNotificationUseCase sendPaymentFailedNotificationUseCase;
    private final ObjectMapper objectMapper;
    private final NotificationMetrics notificationMetrics;

    public PaymentEventConsumer(
            SendPaymentCompletedNotificationUseCase sendPaymentCompletedNotificationUseCase,
            SendPaymentFailedNotificationUseCase sendPaymentFailedNotificationUseCase,
            ObjectMapper objectMapper,
            NotificationMetrics notificationMetrics
    ) {
        this.sendPaymentCompletedNotificationUseCase = sendPaymentCompletedNotificationUseCase;
        this.sendPaymentFailedNotificationUseCase = sendPaymentFailedNotificationUseCase;
        this.objectMapper = objectMapper;
        this.notificationMetrics = notificationMetrics;
    }

    @KafkaListener(
            topics = EventTopics.PAYMENTS_EVENTS,
            groupId = "notification-service",
            containerFactory = "paymentEventKafkaListenerContainerFactory"
    )
    public void consume(JsonNode eventPayload) {
        EventType eventType = EventType.valueOf(eventPayload.get("eventType").asText());
        if (eventType == EventType.PAYMENT_COMPLETED) {
            PaymentCompletedEvent event = objectMapper.convertValue(eventPayload, PaymentCompletedEvent.class);
            try {
                putMdc(event);
                notificationMetrics.eventConsumed();
                log.info(
                        "PaymentCompletedEvent received: eventId={}, correlationId={}, orderId={}",
                        event.eventId(),
                        event.correlationId(),
                        event.orderId()
                );
                sendPaymentCompletedNotificationUseCase.send(event);
            } finally {
                MDC.clear();
            }
            return;
        }

        if (eventType == EventType.PAYMENT_FAILED) {
            PaymentFailedEvent event = objectMapper.convertValue(eventPayload, PaymentFailedEvent.class);
            try {
                putMdc(event);
                notificationMetrics.eventConsumed();
                log.info(
                        "PaymentFailedEvent received: eventId={}, correlationId={}, orderId={}",
                        event.eventId(),
                        event.correlationId(),
                        event.orderId()
                );
                sendPaymentFailedNotificationUseCase.send(event);
            } finally {
                MDC.clear();
            }
            return;
        }

        log.warn("Unsupported payment event ignored: eventType={}", eventType);
    }

    private void putMdc(BaseEvent event) {
        MDC.put("correlationId", event.correlationId().toString());
        MDC.put("eventId", event.eventId().toString());
        if (event instanceof PaymentCompletedEvent paymentCompletedEvent) {
            MDC.put("orderId", paymentCompletedEvent.orderId().toString());
        }
        if (event instanceof PaymentFailedEvent paymentFailedEvent) {
            MDC.put("orderId", paymentFailedEvent.orderId().toString());
        }
    }
}
