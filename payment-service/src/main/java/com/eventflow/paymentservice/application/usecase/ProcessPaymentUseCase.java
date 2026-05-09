package com.eventflow.paymentservice.application.usecase;

import com.eventflow.sharedevents.OrderCreatedEvent;

public interface ProcessPaymentUseCase {

    void process(OrderCreatedEvent event);
}
