package com.eventflow.paymentservice.domain.port;

import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;

public interface PaymentEventPublisherPort {

    void publish(PaymentCompletedEvent event);

    void publish(PaymentFailedEvent event);
}
