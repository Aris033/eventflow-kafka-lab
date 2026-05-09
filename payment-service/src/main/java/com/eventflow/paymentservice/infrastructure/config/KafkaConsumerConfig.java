/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.infrastructure.config;

import com.eventflow.paymentservice.application.observability.PaymentMetrics;
import com.eventflow.sharedevents.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
    public ConsumerFactory<String, OrderCreatedEvent> orderCreatedEventConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties(null));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        JsonDeserializer<OrderCreatedEvent> valueDeserializer = new JsonDeserializer<>(OrderCreatedEvent.class);
        valueDeserializer.addTrustedPackages("com.eventflow.sharedevents");
        valueDeserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> orderCreatedEventKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderCreatedEvent> orderCreatedEventConsumerFactory,
            DefaultErrorHandler paymentKafkaErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedEventConsumerFactory);
        factory.setCommonErrorHandler(paymentKafkaErrorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler paymentKafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate, PaymentMetrics paymentMetrics) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> {
                    String dltTopic = "orders.events.dlt";
                    paymentMetrics.eventSentToDlt(record.topic());
                    log.error(
                            "Sending event to DLT after retries: topic={}, dltTopic={}, key={}, error={}",
                            record.topic(),
                            dltTopic,
                            record.key(),
                            exception.getMessage()
                    );
                    return new TopicPartition(dltTopic, record.partition());
                }
        );
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 2L));
        errorHandler.setRetryListeners((record, exception, deliveryAttempt) -> {
            paymentMetrics.kafkaConsumerError(record.topic());
            log.warn(
                    "Retrying payment event consumption: topic={}, key={}, attempt={}, error={}",
                    record.topic(),
                    record.key(),
                    deliveryAttempt,
                    exception.getMessage()
            );
        });
        return errorHandler;
    }
}
