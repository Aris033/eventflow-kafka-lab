CREATE TABLE IF NOT EXISTS payment_schema.outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(100) NOT NULL,
    event_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    topic VARCHAR(150) NOT NULL,
    message_key VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    retry_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    CONSTRAINT uk_payment_outbox_events_event_id UNIQUE (event_id)
);

CREATE INDEX IF NOT EXISTS idx_payment_outbox_events_status ON payment_schema.outbox_events (status);
CREATE INDEX IF NOT EXISTS idx_payment_outbox_events_created_at ON payment_schema.outbox_events (created_at);
