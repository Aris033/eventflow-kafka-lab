/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.domain.port;

import com.eventflow.orderservice.domain.model.OutboxEvent;

public interface OutboxEventPublisherPort {

    void publish(OutboxEvent outboxEvent);
}
