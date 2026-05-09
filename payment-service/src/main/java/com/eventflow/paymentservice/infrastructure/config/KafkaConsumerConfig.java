package com.eventflow.paymentservice.infrastructure.config;

import com.eventflow.sharedevents.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

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
            ConsumerFactory<String, OrderCreatedEvent> orderCreatedEventConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedEventConsumerFactory);
        return factory;
    }
}
