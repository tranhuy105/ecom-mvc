CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(45) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    phone_number VARCHAR(15),
    verification_code VARCHAR(32) UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at DATETIME,
    profile_picture_url VARCHAR(255),
    date_of_birth DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE addresses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    address_line_1 VARCHAR(128) NOT NULL,
    address_line_2 VARCHAR(128),
    city VARCHAR(45) NOT NULL,
    state VARCHAR(45) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    customer_id INT NOT NULL,
    country_id INT NOT NULL,
    is_main_address BOOLEAN NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT
);

CREATE INDEX idx_customer_id ON addresses(customer_id);
CREATE INDEX idx_country_id ON addresses(country_id);
CREATE INDEX idx_customer_main_address ON addresses(customer_id, is_main_address);
