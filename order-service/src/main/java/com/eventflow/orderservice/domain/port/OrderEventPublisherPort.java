package com.eventflow.orderservice.domain.port;

import com.eventflow.sharedevents.OrderCreatedEvent;

public interface OrderEventPublisherPort {

    void publish(OrderCreatedEvent event);
}
