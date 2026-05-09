package com.eventflow.notificationservice.infrastructure.scheduling;

import com.eventflow.notificationservice.application.usecase.PublishPendingOutboxEventsUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "eventflow.outbox.publisher", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OutboxEventPublisherScheduler {
    private final PublishPendingOutboxEventsUseCase publishPendingOutboxEventsUseCase;

    public OutboxEventPublisherScheduler(PublishPendingOutboxEventsUseCase publishPendingOutboxEventsUseCase) {
        this.publishPendingOutboxEventsUseCase = publishPendingOutboxEventsUseCase;
    }

    @Scheduled(fixedDelayString = "${eventflow.outbox.publisher.fixed-delay:5000}")
    public void publishPending() {
        publishPendingOutboxEventsUseCase.publishPending();
    }
}
