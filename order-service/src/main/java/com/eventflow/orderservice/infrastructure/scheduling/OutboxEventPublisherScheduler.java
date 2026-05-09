/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.infrastructure.scheduling;

import com.eventflow.orderservice.application.usecase.PublishPendingOutboxEventsUseCase;
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
