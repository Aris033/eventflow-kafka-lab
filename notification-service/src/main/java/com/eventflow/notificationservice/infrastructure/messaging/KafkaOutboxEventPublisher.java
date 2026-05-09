package com.eventflow.notificationservice.infrastructure.messaging;

import com.eventflow.notificationservice.domain.model.OutboxEvent;
import com.eventflow.notificationservice.domain.port.OutboxEventPublisherPort;
import com.eventflow.sharedevents.EventType;
import com.eventflow.sharedevents.NotificationFailedEvent;
import com.eventflow.sharedevents.NotificationSentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaOutboxEventPublisher implements OutboxEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOutboxEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxEvent outboxEvent) {
        try {
            kafkaTemplate.send(outboxEvent.topic(), outboxEvent.messageKey(), toEvent(outboxEvent)).get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to publish outbox event " + outboxEvent.eventId(), ex);
        }
    }

    private Object toEvent(OutboxEvent outboxEvent) throws Exception {
        EventType eventType = EventType.valueOf(outboxEvent.eventType());
        return switch (eventType) {
            case NOTIFICATION_SENT -> objectMapper.readValue(outboxEvent.payload(), NotificationSentEvent.class);
            case NOTIFICATION_FAILED -> objectMapper.readValue(outboxEvent.payload(), NotificationFailedEvent.class);
            default -> throw new IllegalArgumentException("Unsupported outbox event type " + outboxEvent.eventType());
        };
    }
}
