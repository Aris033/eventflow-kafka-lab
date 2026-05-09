package com.eventflow.auditservice.application.service;

import com.eventflow.auditservice.domain.model.AuditEvent;
import com.eventflow.auditservice.domain.port.AuditEventRepositoryPort;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.OrderCreatedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditApplicationServiceTest {

    private final AuditEventRepositoryPort repository = mock(AuditEventRepositoryPort.class);
    private final AuditApplicationService service = new AuditApplicationService(repository);

    @Test
    void registerStoresAuditEventWithExtractedOrderId() {
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = OrderCreatedEvent.create(orderId, orderId, "customer-1", new BigDecimal("99.99"));

        service.register(event, EventTopics.ORDERS_EVENTS, orderId.toString(), "{\"eventType\":\"ORDER_CREATED\"}");

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(auditEvent ->
                auditEvent.eventId().equals(event.eventId())
                        && auditEvent.correlationId().equals(event.correlationId())
                        && auditEvent.orderId().equals(orderId)
                        && auditEvent.eventType().equals("ORDER_CREATED")
                        && auditEvent.sourceTopic().equals(EventTopics.ORDERS_EVENTS)
        ));
    }

    @Test
    void registerDuplicateDoesNotStoreAgain() {
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = OrderCreatedEvent.create(orderId, orderId, "customer-1", new BigDecimal("99.99"));
        when(repository.existsByEventId(event.eventId())).thenReturn(true);

        service.register(event, EventTopics.ORDERS_EVENTS, orderId.toString(), "{}");

        verify(repository, never()).save(any());
    }

    @Test
    void findByOrderIdDelegatesTimelineQuery() {
        UUID orderId = UUID.randomUUID();
        AuditEvent auditEvent = new AuditEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), orderId,
                "ORDER_CREATED", EventTopics.ORDERS_EVENTS, orderId.toString(), "{}", Instant.now(), Instant.now());
        when(repository.findByOrderId(orderId)).thenReturn(List.of(auditEvent));

        List<AuditEvent> result = service.findByOrderId(orderId);

        assertThat(result).containsExactly(auditEvent);
    }
}
