/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.notificationservice.domain.port;

import com.eventflow.sharedevents.EventType;

import java.util.UUID;

public interface ProcessedEventRepositoryPort {

    boolean existsByEventId(UUID eventId);

    void save(UUID eventId, EventType eventType);
}
