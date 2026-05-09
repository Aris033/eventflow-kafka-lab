/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.infrastructure.config;

import com.eventflow.sharedevents.EventTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    private static final int PARTITIONS = 1;
    private static final int REPLICAS = 1;

    @Bean
    public NewTopic ordersEventsTopic() {
        return topic(EventTopics.ORDERS_EVENTS);
    }

    @Bean
    public NewTopic paymentsEventsTopic() {
        return topic(EventTopics.PAYMENTS_EVENTS);
    }

    @Bean
    public NewTopic notificationsEventsTopic() {
        return topic(EventTopics.NOTIFICATIONS_EVENTS);
    }

    @Bean
    public NewTopic ordersEventsDltTopic() {
        return topic(EventTopics.ORDERS_EVENTS_DLT);
    }

    @Bean
    public NewTopic paymentsEventsDltTopic() {
        return topic(EventTopics.PAYMENTS_EVENTS_DLT);
    }

    @Bean
    public NewTopic notificationsEventsDltTopic() {
        return topic(EventTopics.NOTIFICATIONS_EVENTS_DLT);
    }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name)
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .build();
    }
}
