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
import com.eventflow.notificationservice.application.usecase.SendPaymentCompletedNotificationUseCase;
import com.eventflow.notificationservice.application.usecase.SendPaymentFailedNotificationUseCase;
import com.eventflow.notificationservice.domain.model.Notification;
import com.eventflow.notificationservice.domain.model.NotificationStatus;
import com.eventflow.notificationservice.domain.port.NotificationEventPublisherPort;
import com.eventflow.notificationservice.domain.port.NotificationRepositoryPort;
import com.eventflow.notificationservice.domain.port.ProcessedEventRepositoryPort;
import com.eventflow.sharedevents.NotificationFailedEvent;
import com.eventflow.sharedevents.NotificationSentEvent;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final NotificationEventPublisherPort notificationEventPublisher;

    public NotificationApplicationService(
            NotificationRepositoryPort notificationRepository,
            ProcessedEventRepositoryPort processedEventRepository,
            NotificationEventPublisherPort notificationEventPublisher
    ) {
        this.notificationRepository = notificationRepository;
        this.processedEventRepository = processedEventRepository;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    @Override
    @Transactional
    public void send(PaymentCompletedEvent event) {
        if (isDuplicate(event.eventId(), event.correlationId(), event.orderId())) {
            return;
        }

        Notification notification = notificationRepository.save(Notification.send(
                event.orderId(),
                recipientFor(event.orderId()),
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

        Notification notification = notificationRepository.save(Notification.send(
                event.orderId(),
                recipientFor(event.orderId()),
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
            NotificationSentEvent sentEvent = NotificationSentEvent.create(
                    correlationId,
                    notification.id(),
                    notification.orderId(),
                    notification.channel().name(),
                    notification.recipient()
            );
            notificationEventPublisher.publish(sentEvent);
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
        notificationEventPublisher.publish(failedEvent);
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
        return "customer-" + orderId + "@eventflow.local";
    }

    private String eventRecipient(String recipient) {
        return recipient == null || recipient.isBlank() ? "unknown@eventflow.local" : recipient;
    }
}
