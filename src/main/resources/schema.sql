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

