/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.auditservice.application.observability;

public interface AuditMetrics {

    void eventReceived();

    void eventStored();

    void duplicatedEvent();

    void eventFailed();

    void kafkaConsumerError(String topic);

    void eventSentToDlt(String topic);
}
