CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         order_id VARCHAR(255) NOT NULL UNIQUE,
                         payment_key VARCHAR(255) UNIQUE,
                         payment_method VARCHAR(20) NOT NULL,
                         amount BIGINT NOT NULL CHECK (amount >= 0),
                         status VARCHAR(20) NOT NULL
                             CHECK (status IN ('WAITING_FOR_DEPOSIT','DONE','CANCELED','PARTIAL_CANCELED','ABORTED','EXPIRED')),
                         approved_at DATETIME NULL,
                         created_at DATETIME NOT NULL,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE virtual_account (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 payment_id BIGINT NOT NULL,
                                 bank_code VARCHAR(20) NOT NULL,
                                 bank_account VARCHAR(20) NOT NULL,
                                 account_holder_name VARCHAR(20) NOT NULL,
                                 due_date DATETIME NOT NULL,
                                 created_at DATETIME NOT NULL,
                                 FOREIGN KEY (payment_id) REFERENCES payment(id)
);
