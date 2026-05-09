package com.eventflow.notificationservice.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void sendCreatesSentNotificationForDeliverableRecipient() {
        Notification notification = Notification.send(UUID.randomUUID(), "customer@eventflow.local", "message");

        assertThat(notification.status()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.failureReason()).isNull();
    }

    @Test
    void sendCreatesFailedNotificationForBlankRecipient() {
        Notification notification = Notification.send(UUID.randomUUID(), "", "message");

        assertThat(notification.status()).isEqualTo(NotificationStatus.FAILED);
        assertThat(notification.failureReason()).isEqualTo("Recipient is not deliverable");
    }
}
