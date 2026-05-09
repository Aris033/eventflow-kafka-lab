package com.eventflow.notificationservice.domain.port;

import com.eventflow.notificationservice.domain.model.OutboxEvent;

public interface OutboxEventPublisherPort {
    void publish(OutboxEvent outboxEvent);
}
