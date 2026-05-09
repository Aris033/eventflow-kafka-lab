/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.application.observability;

public interface PaymentMetrics {

    void eventConsumed();

    void duplicatedEvent();

    void paymentCompleted();

    void paymentFailed();

    void outboxEventCreated();

    void outboxEventPublished();

    void outboxEventPublishFailed();

    void kafkaConsumerError(String topic);

    void eventSentToDlt(String topic);
}
