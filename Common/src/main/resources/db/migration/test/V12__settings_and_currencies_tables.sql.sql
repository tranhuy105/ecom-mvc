CREATE TABLE IF NOT EXISTS settings (
    `key` VARCHAR(128) NOT NULL,
    `value` VARCHAR(1024) NOT NULL,
    `category` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`key`),
    CONSTRAINT `chk_category`
        CHECK (`category` IN ('GENERAL', 'MAIL_SERVER', 'MAIL_TEMPLATES', 'CURRENCY', 'PAYMENT'))
);

CREATE TABLE IF NOT EXISTS currencies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    symbol VARCHAR(4) NOT NULL,
    code VARCHAR(4) NOT NULL
);

CREATE INDEX idx_settings_category ON settings (category);
CREATE INDEX idx_currencies_code ON currencies (code);