ALTER TABLE outbox_events
ADD COLUMN attempts INT DEFAULT 0 NOT NULL;

ALTER TABLE outbox_events
ADD COLUMN last_error VARCHAR(1000);

ALTER TABLE outbox_events
ADD COLUMN published_at TIMESTAMP;