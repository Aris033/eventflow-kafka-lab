package com.eventflow.orderservice.infrastructure.messaging;

import com.eventflow.orderservice.domain.port.OrderEventPublisherPort;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisher implements OrderEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaOrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
        kafkaTemplate.send(EventTopics.ORDERS_EVENTS, event.orderId().toString(), event);
        log.info(
                "OrderCreatedEvent published: eventId={}, correlationId={}, orderId={}, topic={}",
                event.eventId(),
                event.correlationId(),
                event.orderId(),
                EventTopics.ORDERS_EVENTS
        );
    }
}
