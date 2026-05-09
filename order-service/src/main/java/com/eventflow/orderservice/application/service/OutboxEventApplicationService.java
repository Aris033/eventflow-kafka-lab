/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.application.service;

import com.eventflow.orderservice.application.usecase.PublishPendingOutboxEventsUseCase;
import com.eventflow.orderservice.application.observability.OrderMetrics;
import com.eventflow.orderservice.domain.model.OutboxEvent;
import com.eventflow.orderservice.domain.port.OutboxEventPublisherPort;
import com.eventflow.orderservice.domain.port.OutboxEventRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxEventApplicationService implements PublishPendingOutboxEventsUseCase {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventApplicationService.class);

    private final OutboxEventRepositoryPort outboxEventRepository;
    private final OutboxEventPublisherPort outboxEventPublisher;
    private final OrderMetrics orderMetrics;
    private final int batchSize;
    private final int maxRetries;

    public OutboxEventApplicationService(
            OutboxEventRepositoryPort outboxEventRepository,
            OutboxEventPublisherPort outboxEventPublisher,
            OrderMetrics orderMetrics,
            @Value("${eventflow.outbox.publisher.batch-size:20}") int batchSize,
            @Value("${eventflow.outbox.publisher.max-retries:3}") int maxRetries
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxEventPublisher = outboxEventPublisher;
        this.orderMetrics = orderMetrics;
        this.batchSize = batchSize;
        this.maxRetries = maxRetries;
    }

    @Override
    @Transactional
    public void publishPending() {
        for (OutboxEvent event : outboxEventRepository.findPublishable(maxRetries, batchSize)) {
            try {
                outboxEventPublisher.publish(event);
                outboxEventRepository.save(event.markPublished());
                orderMetrics.outboxEventPublished();
                log.info("Outbox event published: eventId={}, topic={}", event.eventId(), event.topic());
            } catch (Exception ex) {
                outboxEventRepository.save(event.markFailed(ex.getMessage(), maxRetries));
                orderMetrics.outboxEventPublishFailed();
                log.warn("Outbox event publication failed: eventId={}, topic={}, error={}", event.eventId(), event.topic(), ex.getMessage());
            }
        }
    }
}
