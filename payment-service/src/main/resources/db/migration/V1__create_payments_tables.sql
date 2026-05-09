CREATE SCHEMA IF NOT EXISTS payment_schema;

CREATE TABLE IF NOT EXISTS payment_schema.payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    failure_reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payment_schema.payments (order_id);

CREATE TABLE IF NOT EXISTS payment_schema.processed_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL
);
