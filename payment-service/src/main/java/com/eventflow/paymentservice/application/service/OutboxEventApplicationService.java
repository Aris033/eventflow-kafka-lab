package com.eventflow.paymentservice.application.service;

import com.eventflow.paymentservice.application.usecase.PublishPendingOutboxEventsUseCase;
import com.eventflow.paymentservice.application.observability.PaymentMetrics;
import com.eventflow.paymentservice.domain.model.OutboxEvent;
import com.eventflow.paymentservice.domain.port.OutboxEventPublisherPort;
import com.eventflow.paymentservice.domain.port.OutboxEventRepositoryPort;
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
    private final PaymentMetrics paymentMetrics;
    private final int batchSize;
    private final int maxRetries;

    public OutboxEventApplicationService(OutboxEventRepositoryPort outboxEventRepository,
                                         OutboxEventPublisherPort outboxEventPublisher,
                                         PaymentMetrics paymentMetrics,
                                         @Value("${eventflow.outbox.publisher.batch-size:20}") int batchSize,
                                         @Value("${eventflow.outbox.publisher.max-retries:3}") int maxRetries) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxEventPublisher = outboxEventPublisher;
        this.paymentMetrics = paymentMetrics;
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
                paymentMetrics.outboxEventPublished();
            } catch (Exception ex) {
                outboxEventRepository.save(event.markFailed(ex.getMessage(), maxRetries));
                paymentMetrics.outboxEventPublishFailed();
                log.warn("Outbox event publication failed: eventId={}, topic={}, error={}", event.eventId(), event.topic(), ex.getMessage());
            }
        }
    }
}
