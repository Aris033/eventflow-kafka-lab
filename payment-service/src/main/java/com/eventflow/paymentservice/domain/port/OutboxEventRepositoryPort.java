package com.eventflow.paymentservice.domain.port;

import com.eventflow.paymentservice.domain.model.OutboxEvent;

import java.util.List;

public interface OutboxEventRepositoryPort {
    OutboxEvent save(OutboxEvent outboxEvent);
    List<OutboxEvent> findPublishable(int maxRetries, int batchSize);
}
