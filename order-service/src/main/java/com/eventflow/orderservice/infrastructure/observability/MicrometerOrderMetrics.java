/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.orderservice.infrastructure.observability;

import com.eventflow.orderservice.application.observability.OrderMetrics;
import com.eventflow.orderservice.domain.port.OutboxEventRepositoryPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerOrderMetrics implements OrderMetrics {

    private final Counter ordersCreated;
    private final Counter outboxEventsCreated;
    private final Counter outboxEventsPublished;
    private final Counter outboxEventsPublishFailed;

    public MicrometerOrderMetrics(MeterRegistry registry, OutboxEventRepositoryPort outboxEventRepository) {
        this.ordersCreated = registry.counter("eventflow_orders_created");
        this.outboxEventsCreated = registry.counter("eventflow_order_events_outbox_created");
        this.outboxEventsPublished = registry.counter("eventflow_order_events_published");
        this.outboxEventsPublishFailed = registry.counter("eventflow_order_events_publish_failed");
        registry.gauge("eventflow_order_outbox_pending", outboxEventRepository, OutboxEventRepositoryPort::countPending);
    }

    @Override
    public void orderCreated() {
        ordersCreated.increment();
    }

    @Override
    public void outboxEventCreated() {
        outboxEventsCreated.increment();
    }

    @Override
    public void outboxEventPublished() {
        outboxEventsPublished.increment();
    }

    @Override
    public void outboxEventPublishFailed() {
        outboxEventsPublishFailed.increment();
    }
}
