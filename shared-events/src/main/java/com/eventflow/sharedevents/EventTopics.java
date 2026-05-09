/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.sharedevents;

public final class EventTopics {

    public static final String ORDERS_EVENTS = "orders.events";
    public static final String PAYMENTS_EVENTS = "payments.events";
    public static final String NOTIFICATIONS_EVENTS = "notifications.events";

    public static final String ORDERS_EVENTS_DLT = "orders.events.dlt";
    public static final String PAYMENTS_EVENTS_DLT = "payments.events.dlt";
    public static final String NOTIFICATIONS_EVENTS_DLT = "notifications.events.dlt";

    private EventTopics() {
    }
}
