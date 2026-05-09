package com.eventflow.notificationservice.application.service;

import com.eventflow.notificationservice.application.observability.NotificationMetrics;
import com.eventflow.notificationservice.domain.model.Notification;
import com.eventflow.notificationservice.domain.model.OutboxEvent;
import com.eventflow.notificationservice.domain.port.NotificationRepositoryPort;
import com.eventflow.notificationservice.domain.port.OutboxEventRepositoryPort;
import com.eventflow.notificationservice.domain.port.ProcessedEventRepositoryPort;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationApplicationServiceTest {

    private final NotificationRepositoryPort notificationRepository = mock(NotificationRepositoryPort.class);
    private final ProcessedEventRepositoryPort processedEventRepository = mock(ProcessedEventRepositoryPort.class);
    private final OutboxEventRepositoryPort outboxEventRepository = mock(OutboxEventRepositoryPort.class);
    private final NotificationMetrics notificationMetrics = mock(NotificationMetrics.class);

    @Test
    void sendPaymentCompletedNotificationCreatesSentOutboxEvent() {
        NotificationApplicationService service = service("");
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.send(PaymentCompletedEvent.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("99.99")));

        verify(processedEventRepository).save(any(UUID.class), any());
        verify(notificationMetrics).notificationSent();
        verify(notificationMetrics).outboxEventCreated();
        verify(outboxEventRepository).save(org.mockito.ArgumentMatchers.argThat(event ->
                event.eventType().equals("NOTIFICATION_SENT")
                        && event.topic().equals("notifications.events")
        ));
    }

    @Test
    void sendPaymentFailedNotificationStillCreatesSentOutboxEvent() {
        NotificationApplicationService service = service("");
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.send(PaymentFailedEvent.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("1500"), "Amount exceeds payment limit"));

        verify(outboxEventRepository).save(org.mockito.ArgumentMatchers.argThat(event -> event.eventType().equals("NOTIFICATION_SENT")));
    }

    @Test
    void duplicateEventDoesNothing() {
        NotificationApplicationService service = service("");
        PaymentCompletedEvent event = PaymentCompletedEvent.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("99.99"));
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(true);

        service.send(event);

        verify(notificationRepository, never()).save(any());
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
        verify(notificationMetrics).duplicatedEvent();
    }

    @Test
    void failRecipientThrowsControlledException() {
        NotificationApplicationService service = service("fail@eventflow.local");

        assertThatThrownBy(() -> service.send(PaymentCompletedEvent.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("99.99"))))
                .isInstanceOf(IllegalStateException.class);
    }

    private NotificationApplicationService service(String recipientOverride) {
        return new NotificationApplicationService(
                notificationRepository,
                processedEventRepository,
                outboxEventRepository,
                new ObjectMapper().findAndRegisterModules(),
                notificationMetrics,
                recipientOverride
        );
    }
}
