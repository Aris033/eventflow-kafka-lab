/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.application.service;

import com.eventflow.notificationservice.application.usecase.GetNotificationsUseCase;
import com.eventflow.notificationservice.application.usecase.ListNotificationsUseCase;
import com.eventflow.notificationservice.application.observability.NotificationMetrics;
import com.eventflow.notificationservice.application.usecase.SendPaymentCompletedNotificationUseCase;
import com.eventflow.notificationservice.application.usecase.SendPaymentFailedNotificationUseCase;
import com.eventflow.notificationservice.domain.model.Notification;
import com.eventflow.notificationservice.domain.model.NotificationStatus;
import com.eventflow.notificationservice.domain.model.OutboxEvent;
import com.eventflow.notificationservice.domain.port.NotificationRepositoryPort;
import com.eventflow.notificationservice.domain.port.OutboxEventRepositoryPort;
import com.eventflow.notificationservice.domain.port.ProcessedEventRepositoryPort;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.NotificationFailedEvent;
import com.eventflow.sharedevents.NotificationSentEvent;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationApplicationService implements
        SendPaymentCompletedNotificationUseCase,
        SendPaymentFailedNotificationUseCase,
        GetNotificationsUseCase,
        ListNotificationsUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificationApplicationService.class);

    private final NotificationRepositoryPort notificationRepository;
    private final ProcessedEventRepositoryPort processedEventRepository;
    private final OutboxEventRepositoryPort outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final NotificationMetrics notificationMetrics;
    private final String recipientOverride;

    public NotificationApplicationService(
            NotificationRepositoryPort notificationRepository,
            ProcessedEventRepositoryPort processedEventRepository,
            OutboxEventRepositoryPort outboxEventRepository,
            ObjectMapper objectMapper,
            NotificationMetrics notificationMetrics,
            @Value("${eventflow.notification.simulation.recipient-override:}") String recipientOverride
    ) {
        this.notificationRepository = notificationRepository;
        this.processedEventRepository = processedEventRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.notificationMetrics = notificationMetrics;
        this.recipientOverride = recipientOverride;
    }

    @Override
    @Transactional
    public void send(PaymentCompletedEvent event) {
        if (isDuplicate(event.eventId(), event.correlationId(), event.orderId())) {
            return;
        }

        Notification notification = notificationRepository.save(createNotification(
                event.orderId(),
                "Payment completed notification sent"
        ));
        processedEventRepository.save(event.eventId(), event.eventType());
        publishResult(event.correlationId(), notification);
    }

    @Override
    @Transactional
    public void send(PaymentFailedEvent event) {
        if (isDuplicate(event.eventId(), event.correlationId(), event.orderId())) {
            return;
        }

        Notification notification = notificationRepository.save(createNotification(
                event.orderId(),
                "Payment failed notification sent"
        ));
        processedEventRepository.save(event.eventId(), event.eventType());
        publishResult(event.correlationId(), notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByOrderId(UUID orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> listNotifications() {
        return notificationRepository.findAll();
    }

    private boolean isDuplicate(UUID eventId, UUID correlationId, UUID orderId) {
        if (!processedEventRepository.existsByEventId(eventId)) {
            return false;
        }
        notificationMetrics.duplicatedEvent();
        log.info(
                "Duplicate payment event ignored: eventId={}, correlationId={}, orderId={}",
                eventId,
                correlationId,
                orderId
        );
        return true;
    }

    private void publishResult(UUID correlationId, Notification notification) {
        if (notification.status() == NotificationStatus.SENT) {
            notificationMetrics.notificationSent();
            NotificationSentEvent sentEvent = NotificationSentEvent.create(
                    correlationId,
                    notification.id(),
                    notification.orderId(),
                    notification.channel().name(),
                    notification.recipient()
            );
            saveOutboxEvent(notification.id(), sentEvent.eventId(), sentEvent.eventType().name(), notification.orderId().toString(), serialize(sentEvent));
            log.info(
                    "Notification sent: eventId={}, correlationId={}, orderId={}, notificationId={}",
                    sentEvent.eventId(),
                    correlationId,
                    notification.orderId(),
                    notification.id()
            );
            return;
        }

        NotificationFailedEvent failedEvent = NotificationFailedEvent.create(
                correlationId,
                notification.id(),
                notification.orderId(),
                notification.channel().name(),
                eventRecipient(notification.recipient()),
                notification.failureReason()
        );
        saveOutboxEvent(notification.id(), failedEvent.eventId(), failedEvent.eventType().name(), notification.orderId().toString(), serialize(failedEvent));
        notificationMetrics.notificationFailed();
        log.info(
                "Notification failed: eventId={}, correlationId={}, orderId={}, notificationId={}, reason={}",
                failedEvent.eventId(),
                correlationId,
                notification.orderId(),
                notification.id(),
                notification.failureReason()
        );
    }

    private String recipientFor(UUID orderId) {
        if (recipientOverride != null && !recipientOverride.isBlank()) {
            return recipientOverride;
        }
        return "customer-" + orderId + "@eventflow.local";
    }

    private Notification createNotification(UUID orderId, String message) {
        String recipient = recipientFor(orderId);
        if (recipient.toLowerCase().contains("fail")) {
            throw new IllegalStateException("Controlled notification processing failure for recipient " + recipient);
        }
        return Notification.send(orderId, recipient, message);
    }

    private String eventRecipient(String recipient) {
        return recipient == null || recipient.isBlank() ? "unknown@eventflow.local" : recipient;
    }

    private void saveOutboxEvent(UUID aggregateId, UUID eventId, String eventType, String messageKey, String payload) {
        outboxEventRepository.save(OutboxEvent.pending(
                aggregateId,
                "NOTIFICATION",
                eventId,
                eventType,
                EventTopics.NOTIFICATIONS_EVENTS,
                messageKey,
                payload
        ));
        notificationMetrics.outboxEventCreated();
    }

    private String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize notification event", ex);
        }
    }
}
