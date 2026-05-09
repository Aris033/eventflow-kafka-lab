package com.eventflow.paymentservice.domain.model;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
