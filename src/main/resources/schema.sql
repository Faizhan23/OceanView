DROP DATABASE IF EXISTS ocean_view_resort;
CREATE DATABASE ocean_view_resort
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ocean_view_resort;


CREATE TABLE roles (
    role_id   TINYINT      NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(50)  NOT NULL,
    PRIMARY KEY (role_id),
    UNIQUE KEY uq_role_name (role_name)
);

CREATE TABLE users (
    user_id       INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,   -- BCrypt hash
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    role_id       TINYINT      NOT NULL DEFAULT 1,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_username (username),
    UNIQUE KEY uq_email    (email),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE room_categories (
    category_id   TINYINT       NOT NULL AUTO_INCREMENT,
    category_name VARCHAR(50)   NOT NULL,    -- 'Standard','Deluxe','Suite','Ocean View'
    price_per_night DECIMAL(10,2) NOT NULL,
    description   VARCHAR(255),
    PRIMARY KEY (category_id),
    UNIQUE KEY uq_category_name (category_name)
);
