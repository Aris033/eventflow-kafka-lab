/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.infrastructure.messaging;

import com.eventflow.orderservice.domain.model.OutboxEvent;
import com.eventflow.orderservice.domain.port.OutboxEventPublisherPort;
import com.eventflow.sharedevents.EventType;
import com.eventflow.sharedevents.OrderCreatedEvent;
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
            Object event = toEvent(outboxEvent);
            kafkaTemplate.send(outboxEvent.topic(), outboxEvent.messageKey(), event).get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to publish outbox event " + outboxEvent.eventId(), ex);
        }
    }

    private Object toEvent(OutboxEvent outboxEvent) throws Exception {
        EventType eventType = EventType.valueOf(outboxEvent.eventType());
        if (eventType == EventType.ORDER_CREATED) {
            return objectMapper.readValue(outboxEvent.payload(), OrderCreatedEvent.class);
        }
        throw new IllegalArgumentException("Unsupported outbox event type " + outboxEvent.eventType());
    }
}
