package com.eventflow.orderservice.application.service;

import com.eventflow.orderservice.domain.model.OutboxEvent;
import com.eventflow.orderservice.domain.model.OutboxEventStatus;
import com.eventflow.orderservice.domain.port.OutboxEventPublisherPort;
import com.eventflow.orderservice.domain.port.OutboxEventRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxEventApplicationServiceTest {

    private final OutboxEventRepositoryPort repository = mock(OutboxEventRepositoryPort.class);
    private final OutboxEventPublisherPort publisher = mock(OutboxEventPublisherPort.class);
    private final OutboxEventApplicationService service = new OutboxEventApplicationService(repository, publisher, 20, 3);

    @Test
    void publishPendingMarksEventAsPublished() {
        OutboxEvent event = event();
        when(repository.findPublishable(3, 20)).thenReturn(List.of(event));

        service.publishPending();

        verify(publisher).publish(event);
        verify(repository).save(org.mockito.ArgumentMatchers.argThat(saved -> saved.status() == OutboxEventStatus.PUBLISHED));
    }

    @Test
    void publishPendingMarksEventAsFailedWhenPublisherFails() {
        OutboxEvent event = event();
        when(repository.findPublishable(3, 20)).thenReturn(List.of(event));
        doThrow(new IllegalStateException("Kafka unavailable")).when(publisher).publish(event);

        service.publishPending();

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.status() == OutboxEventStatus.PENDING
                        && saved.retryCount() == 1
                        && saved.lastError().contains("Kafka unavailable")
        ));
    }

    private OutboxEvent event() {
        return OutboxEvent.pending(UUID.randomUUID(), "ORDER", UUID.randomUUID(), "ORDER_CREATED", "orders.events", "key", "{}");
    }
}
