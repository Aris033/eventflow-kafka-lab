package com.eventflow.notificationservice.application.service;

import com.eventflow.notificationservice.application.usecase.PublishPendingOutboxEventsUseCase;
import com.eventflow.notificationservice.domain.model.OutboxEvent;
import com.eventflow.notificationservice.domain.port.OutboxEventPublisherPort;
import com.eventflow.notificationservice.domain.port.OutboxEventRepositoryPort;
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
    private final int batchSize;
    private final int maxRetries;

    public OutboxEventApplicationService(OutboxEventRepositoryPort outboxEventRepository,
                                         OutboxEventPublisherPort outboxEventPublisher,
                                         @Value("${eventflow.outbox.publisher.batch-size:20}") int batchSize,
                                         @Value("${eventflow.outbox.publisher.max-retries:3}") int maxRetries) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxEventPublisher = outboxEventPublisher;
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
            } catch (Exception ex) {
                outboxEventRepository.save(event.markFailed(ex.getMessage(), maxRetries));
                log.warn("Outbox event publication failed: eventId={}, topic={}, error={}", event.eventId(), event.topic(), ex.getMessage());
            }
        }
    }
}
