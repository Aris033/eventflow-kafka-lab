package com.eventflow.paymentservice.domain.port;

import com.eventflow.sharedevents.EventType;

import java.util.UUID;

public interface ProcessedEventRepositoryPort {

    boolean existsByEventId(UUID eventId);

    void save(UUID eventId, EventType eventType);
}
