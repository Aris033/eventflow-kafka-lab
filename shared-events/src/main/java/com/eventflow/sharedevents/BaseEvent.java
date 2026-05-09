package com.eventflow.sharedevents;

import java.time.Instant;
import java.util.UUID;

public interface BaseEvent {

    UUID eventId();

    UUID correlationId();

    EventType eventType();

    Instant occurredAt();

    int version();
}
