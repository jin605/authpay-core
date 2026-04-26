CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(20) NULL,
                       nickname VARCHAR(20) NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       image_url VARCHAR(255) NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER'
                           CHECK (role IN ('USER','ADMIN')),
                       status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                           CHECK (status IN ('PENDING','ACTIVE','INACTIVE','DELETED')),
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE social_account (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                provider VARCHAR(20) NOT NULL,
                                provider_id VARCHAR(255) NOT NULL,
                                provider_email VARCHAR(255) NULL,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                CONSTRAINT uq_social_provider UNIQUE (provider, provider_id),
                                CONSTRAINT uq_social_user_provider UNIQUE (user_id, provider),
                                CONSTRAINT fk_social_user FOREIGN KEY (user_id) REFERENCES users(id)
);
