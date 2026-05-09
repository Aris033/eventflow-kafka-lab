CREATE SCHEMA IF NOT EXISTS audit_schema;

CREATE TABLE IF NOT EXISTS audit_schema.audit_events (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    correlation_id UUID NOT NULL,
    order_id UUID,
    event_type VARCHAR(100) NOT NULL,
    source_topic VARCHAR(150) NOT NULL,
    message_key VARCHAR(255),
    payload TEXT NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_audit_events_event_id UNIQUE (event_id)
);

CREATE INDEX IF NOT EXISTS idx_audit_events_correlation_id ON audit_schema.audit_events (correlation_id);
CREATE INDEX IF NOT EXISTS idx_audit_events_order_id ON audit_schema.audit_events (order_id);
CREATE INDEX IF NOT EXISTS idx_audit_events_event_type ON audit_schema.audit_events (event_type);
CREATE INDEX IF NOT EXISTS idx_audit_events_received_at ON audit_schema.audit_events (received_at);
