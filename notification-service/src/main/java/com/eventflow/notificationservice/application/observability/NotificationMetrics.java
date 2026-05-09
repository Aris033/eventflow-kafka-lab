/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.application.observability;

public interface NotificationMetrics {

    void eventConsumed();

    void duplicatedEvent();

    void notificationSent();

    void notificationFailed();

    void outboxEventCreated();

    void outboxEventPublished();

    void outboxEventPublishFailed();

    void kafkaConsumerError(String topic);

    void eventSentToDlt(String topic);
}
