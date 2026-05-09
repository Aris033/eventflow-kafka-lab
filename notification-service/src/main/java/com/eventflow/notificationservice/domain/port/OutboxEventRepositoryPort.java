package com.eventflow.notificationservice.domain.port;

import com.eventflow.notificationservice.domain.model.OutboxEvent;

import java.util.List;

public interface OutboxEventRepositoryPort {
    OutboxEvent save(OutboxEvent outboxEvent);
    List<OutboxEvent> findPublishable(int maxRetries, int batchSize);
}
