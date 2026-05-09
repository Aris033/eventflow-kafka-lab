package com.eventflow.paymentservice.infrastructure.messaging;

import com.eventflow.paymentservice.domain.port.PaymentEventPublisherPort;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPaymentEventPublisher implements PaymentEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaPaymentEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaPaymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(PaymentCompletedEvent event) {
        kafkaTemplate.send(EventTopics.PAYMENTS_EVENTS, event.orderId().toString(), event);
        log.info(
                "PaymentCompletedEvent published: eventId={}, correlationId={}, orderId={}, topic={}",
                event.eventId(),
                event.correlationId(),
                event.orderId(),
                EventTopics.PAYMENTS_EVENTS
        );
    }

    @Override
    public void publish(PaymentFailedEvent event) {
        kafkaTemplate.send(EventTopics.PAYMENTS_EVENTS, event.orderId().toString(), event);
        log.info(
                "PaymentFailedEvent published: eventId={}, correlationId={}, orderId={}, topic={}",
                event.eventId(),
                event.correlationId(),
                event.orderId(),
                EventTopics.PAYMENTS_EVENTS
        );
    }
}
