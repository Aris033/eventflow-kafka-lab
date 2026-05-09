/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.application.service;

import com.eventflow.auditservice.application.usecase.FindAuditEventsByCorrelationIdUseCase;
import com.eventflow.auditservice.application.usecase.FindAuditEventsByOrderIdUseCase;
import com.eventflow.auditservice.application.usecase.FindAuditEventsUseCase;
import com.eventflow.auditservice.application.usecase.RegisterAuditEventUseCase;
import com.eventflow.auditservice.domain.model.AuditEvent;
import com.eventflow.auditservice.domain.port.AuditEventRepositoryPort;
import com.eventflow.sharedevents.BaseEvent;
import com.eventflow.sharedevents.NotificationFailedEvent;
import com.eventflow.sharedevents.NotificationSentEvent;
import com.eventflow.sharedevents.OrderCreatedEvent;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuditApplicationService implements
        RegisterAuditEventUseCase,
        FindAuditEventsUseCase,
        FindAuditEventsByCorrelationIdUseCase,
        FindAuditEventsByOrderIdUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuditApplicationService.class);

    private final AuditEventRepositoryPort auditEventRepository;

    public AuditApplicationService(AuditEventRepositoryPort auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Override
    @Transactional
    public void register(BaseEvent event, String sourceTopic, String messageKey, String payload) {
        if (auditEventRepository.existsByEventId(event.eventId())) {
            log.info(
                    "Audit event already registered: eventId={}, correlationId={}, eventType={}",
                    event.eventId(),
                    event.correlationId(),
                    event.eventType()
            );
            return;
        }

        try {
            auditEventRepository.save(new AuditEvent(
                    UUID.randomUUID(),
                    event.eventId(),
                    event.correlationId(),
                    extractOrderId(event),
                    event.eventType().name(),
                    sourceTopic,
                    messageKey,
                    payload,
                    event.occurredAt(),
                    Instant.now()
            ));
            log.info(
                    "Audit event registered: eventId={}, correlationId={}, orderId={}, eventType={}, topic={}",
                    event.eventId(),
                    event.correlationId(),
                    extractOrderId(event),
                    event.eventType(),
                    sourceTopic
            );
        } catch (DataIntegrityViolationException ex) {
            log.info("Audit event duplicate ignored after unique constraint: eventId={}", event.eventId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEvent> findAll() {
        return auditEventRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEvent> findByEventType(String eventType) {
        return auditEventRepository.findByEventType(eventType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEvent> findByCorrelationId(UUID correlationId) {
        return auditEventRepository.findByCorrelationId(correlationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEvent> findByOrderId(UUID orderId) {
        return auditEventRepository.findByOrderId(orderId);
    }

    private UUID extractOrderId(BaseEvent event) {
        if (event instanceof OrderCreatedEvent orderCreatedEvent) {
            return orderCreatedEvent.orderId();
        }
        if (event instanceof PaymentCompletedEvent paymentCompletedEvent) {
            return paymentCompletedEvent.orderId();
        }
        if (event instanceof PaymentFailedEvent paymentFailedEvent) {
            return paymentFailedEvent.orderId();
        }
        if (event instanceof NotificationSentEvent notificationSentEvent) {
            return notificationSentEvent.orderId();
        }
        if (event instanceof NotificationFailedEvent notificationFailedEvent) {
            return notificationFailedEvent.orderId();
        }
        return null;
    }
}
