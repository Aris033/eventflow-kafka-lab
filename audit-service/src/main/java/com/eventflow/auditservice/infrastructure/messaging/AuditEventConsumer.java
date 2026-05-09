/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.infrastructure.messaging;

import com.eventflow.auditservice.application.observability.AuditMetrics;
import com.eventflow.auditservice.application.usecase.RegisterAuditEventUseCase;
import com.eventflow.sharedevents.BaseEvent;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.EventType;
import com.eventflow.sharedevents.NotificationFailedEvent;
import com.eventflow.sharedevents.NotificationSentEvent;
import com.eventflow.sharedevents.OrderCreatedEvent;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditEventConsumer.class);

    private final RegisterAuditEventUseCase registerAuditEventUseCase;
    private final ObjectMapper objectMapper;
    private final AuditMetrics auditMetrics;
    private final String failOnEventType;

    public AuditEventConsumer(
            RegisterAuditEventUseCase registerAuditEventUseCase,
            ObjectMapper objectMapper,
            AuditMetrics auditMetrics,
            @Value("${eventflow.audit.simulation.fail-on-event-type:}") String failOnEventType
    ) {
        this.registerAuditEventUseCase = registerAuditEventUseCase;
        this.objectMapper = objectMapper;
        this.auditMetrics = auditMetrics;
        this.failOnEventType = failOnEventType;
    }

    @KafkaListener(
            topics = {
                    EventTopics.ORDERS_EVENTS,
                    EventTopics.PAYMENTS_EVENTS,
                    EventTopics.NOTIFICATIONS_EVENTS
            },
            groupId = "audit-service",
            containerFactory = "auditEventKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, JsonNode> record) throws JsonProcessingException {
        try {
            BaseEvent event = toEvent(record.value());
            putMdc(event);
            auditMetrics.eventReceived();
            log.info(
                    "Event received for audit: eventId={}, correlationId={}, eventType={}, topic={}, key={}",
                    event.eventId(),
                    event.correlationId(),
                    event.eventType(),
                    record.topic(),
                    record.key()
            );

            if (event.eventType().name().equalsIgnoreCase(failOnEventType)) {
                throw new IllegalStateException("Controlled audit failure for eventType " + event.eventType());
            }

            registerAuditEventUseCase.register(
                    event,
                    record.topic(),
                    record.key(),
                    objectMapper.writeValueAsString(record.value())
            );
        } catch (RuntimeException | JsonProcessingException ex) {
            auditMetrics.eventFailed();
            throw ex;
        } finally {
            MDC.clear();
        }
    }

    private BaseEvent toEvent(JsonNode payload) {
        EventType eventType = EventType.valueOf(payload.get("eventType").asText());
        return switch (eventType) {
            case ORDER_CREATED -> objectMapper.convertValue(payload, OrderCreatedEvent.class);
            case PAYMENT_COMPLETED -> objectMapper.convertValue(payload, PaymentCompletedEvent.class);
            case PAYMENT_FAILED -> objectMapper.convertValue(payload, PaymentFailedEvent.class);
            case NOTIFICATION_SENT -> objectMapper.convertValue(payload, NotificationSentEvent.class);
            case NOTIFICATION_FAILED -> objectMapper.convertValue(payload, NotificationFailedEvent.class);
        };
    }

    private void putMdc(BaseEvent event) {
        MDC.put("correlationId", event.correlationId().toString());
        MDC.put("eventId", event.eventId().toString());
        UUID orderId = extractOrderId(event);
        if (orderId != null) {
            MDC.put("orderId", orderId.toString());
        }
    }

    private UUID extractOrderId(BaseEvent event) {
        if (event instanceof OrderCreatedEvent orderCreatedEvent) {
            return orderCreatedEvent.orderId();
        }
        if (event instanceof PaymentCompletedEvent paymentCompletedEvent) {
            return paymentCompletedEvent.orderId();
        }
        if (event instanceof PaymentFailedEvent paymentFailedEvent) {
            return paymentFailedEvent.orderId();
        }
        if (event instanceof NotificationSentEvent notificationSentEvent) {
            return notificationSentEvent.orderId();
        }
        if (event instanceof NotificationFailedEvent notificationFailedEvent) {
            return notificationFailedEvent.orderId();
        }
        return null;
    }
}
