package com.eventflow.paymentservice.domain.port;

import com.eventflow.paymentservice.domain.model.OutboxEvent;

public interface OutboxEventPublisherPort {
    void publish(OutboxEvent outboxEvent);
}
