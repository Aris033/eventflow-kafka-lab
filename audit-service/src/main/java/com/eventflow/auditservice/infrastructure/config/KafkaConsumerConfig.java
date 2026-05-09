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
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Bean
    public ConsumerFactory<String, JsonNode> auditEventConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties(null));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        JsonDeserializer<JsonNode> valueDeserializer = new JsonDeserializer<>(JsonNode.class);
        valueDeserializer.addTrustedPackages("com.eventflow.sharedevents");
        valueDeserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JsonNode> auditEventKafkaListenerContainerFactory(
            ConsumerFactory<String, JsonNode> auditEventConsumerFactory,
            DefaultErrorHandler auditKafkaErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, JsonNode> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(auditEventConsumerFactory);
        factory.setCommonErrorHandler(auditKafkaErrorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler auditKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(dltTopic(record), record.partition())
        );
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 2L));
        errorHandler.setRetryListeners((record, exception, deliveryAttempt) -> log.warn(
                "Retrying audit event consumption: topic={}, key={}, attempt={}, error={}",
                record.topic(),
                record.key(),
                deliveryAttempt,
                exception.getMessage()
        ));
        return errorHandler;
    }

    private static String dltTopic(ConsumerRecord<?, ?> record) {
        return switch (record.topic()) {
            case EventTopics.ORDERS_EVENTS -> EventTopics.ORDERS_EVENTS_DLT;
            case EventTopics.PAYMENTS_EVENTS -> EventTopics.PAYMENTS_EVENTS_DLT;
            case EventTopics.NOTIFICATIONS_EVENTS -> EventTopics.NOTIFICATIONS_EVENTS_DLT;
            default -> record.topic() + ".dlt";
        };
    }
}
