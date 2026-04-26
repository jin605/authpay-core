CREATE TABLE payment_webhook_event (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       event_id VARCHAR(100) NOT NULL UNIQUE,
                                       event_type VARCHAR(100) NOT NULL,
                                       order_id VARCHAR(255) NULL,
                                       payment_key VARCHAR(255) NULL,
                                       payload JSON NOT NULL,
                                       processed TINYINT(1) NOT NULL DEFAULT 0,
                                       processed_at DATETIME NULL,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_webhook_processed ON payment_webhook_event(processed, created_at);
CREATE INDEX idx_webhook_order_id ON payment_webhook_event(order_id);
