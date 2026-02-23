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
    password_hash VARCHAR(255) NOT NULL,   
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
    category_name VARCHAR(50)   NOT NULL,    
    price_per_night DECIMAL(10,2) NOT NULL,
    description   VARCHAR(255),
    PRIMARY KEY (category_id),
    UNIQUE KEY uq_category_name (category_name)
);


CREATE TABLE rooms (
    room_id     SMALLINT     NOT NULL AUTO_INCREMENT,
    room_number VARCHAR(10)  NOT NULL,
    category_id TINYINT      NOT NULL,
    floor_number TINYINT     NOT NULL,
    capacity    TINYINT      NOT NULL DEFAULT 2,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (room_id),
    UNIQUE KEY uq_room_number (room_number),
    CONSTRAINT fk_rooms_category FOREIGN KEY (category_id) REFERENCES room_categories(category_id),
    INDEX idx_rooms_category (category_id)
);

CREATE TABLE guests (
    guest_id       INT          NOT NULL AUTO_INCREMENT,
    guest_name     VARCHAR(100) NOT NULL,
    address        VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20)  NOT NULL,
    email          VARCHAR(150),
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (guest_id),
    INDEX idx_guests_name (guest_name)
);

CREATE TABLE reservations (
    reservation_id   INT          NOT NULL AUTO_INCREMENT,
    reservation_ref  VARCHAR(20)  NOT NULL,    
    guest_id         INT          NOT NULL,
    room_id          SMALLINT     NOT NULL,
    user_id          INT          NOT NULL,     
    checkin_date     DATE         NOT NULL,
    checkout_date    DATE         NOT NULL,
    num_nights       SMALLINT     NOT NULL,
    status           ENUM('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED')
                                  NOT NULL DEFAULT 'CONFIRMED',
    special_requests TEXT,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (reservation_id),
    UNIQUE KEY uq_reservation_ref (reservation_ref),
    CONSTRAINT fk_res_guest  FOREIGN KEY (guest_id)  REFERENCES guests(guest_id),
    CONSTRAINT fk_res_room   FOREIGN KEY (room_id)   REFERENCES rooms(room_id),
    CONSTRAINT fk_res_user   FOREIGN KEY (user_id)   REFERENCES users(user_id),
    INDEX idx_res_checkin  (checkin_date),
    INDEX idx_res_checkout (checkout_date),
    INDEX idx_res_room     (room_id),
    INDEX idx_res_status   (status),
    CONSTRAINT chk_dates CHECK (checkout_date > checkin_date)
);


CREATE TABLE bills (
    bill_id        INT            NOT NULL AUTO_INCREMENT,
    reservation_id INT            NOT NULL,
    room_charge    DECIMAL(10,2)  NOT NULL,
    tax_amount     DECIMAL(10,2)  NOT NULL,
    discount       DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    total_amount   DECIMAL(10,2)  NOT NULL,
    tax_rate       DECIMAL(5,2)   NOT NULL DEFAULT 10.00,
    is_paid        TINYINT(1)     NOT NULL DEFAULT 0,
    generated_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at        DATETIME,
    PRIMARY KEY (bill_id),
    UNIQUE KEY uq_bill_reservation (reservation_id),
    CONSTRAINT fk_bill_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);



