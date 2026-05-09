/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.infrastructure.messaging;

import com.eventflow.notificationservice.domain.port.NotificationEventPublisherPort;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.NotificationFailedEvent;
import com.eventflow.sharedevents.NotificationSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaNotificationEventPublisher implements NotificationEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaNotificationEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(NotificationSentEvent event) {
        kafkaTemplate.send(EventTopics.NOTIFICATIONS_EVENTS, event.orderId().toString(), event);
        log.info(
                "NotificationSentEvent published: eventId={}, correlationId={}, orderId={}, topic={}",
                event.eventId(),
                event.correlationId(),
                event.orderId(),
                EventTopics.NOTIFICATIONS_EVENTS
        );
    }

    @Override
    public void publish(NotificationFailedEvent event) {
        kafkaTemplate.send(EventTopics.NOTIFICATIONS_EVENTS, event.orderId().toString(), event);
        log.info(
                "NotificationFailedEvent published: eventId={}, correlationId={}, orderId={}, topic={}",
                event.eventId(),
                event.correlationId(),
                event.orderId(),
                EventTopics.NOTIFICATIONS_EVENTS
        );
    }
}
