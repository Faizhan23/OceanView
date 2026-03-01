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
    is_active     TINYINT   NOT NULL DEFAULT 1,
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
    is_active   TINYINT   NOT NULL DEFAULT 1,
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
    is_paid        TINYINT     NOT NULL DEFAULT 0,
    generated_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at        DATETIME,
    PRIMARY KEY (bill_id),
    UNIQUE KEY uq_bill_reservation (reservation_id),
    CONSTRAINT fk_bill_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);


CREATE TABLE audit_log (
    log_id     BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    INT,
    action     VARCHAR(100) NOT NULL,
    table_name VARCHAR(50)  NOT NULL,
    record_id  INT,
    log_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    PRIMARY KEY (log_id),
    INDEX idx_audit_time (log_time),
    INDEX idx_audit_user (user_id)
);


DELIMITER $$

CREATE PROCEDURE sp_calculate_bill(
    IN  p_reservation_id INT,
    IN  p_tax_rate       DECIMAL(5,2),
    IN  p_discount       DECIMAL(10,2),
    OUT p_room_charge    DECIMAL(10,2),
    OUT p_tax_amount     DECIMAL(10,2),
    OUT p_total          DECIMAL(10,2)
)
BEGIN
    DECLARE v_num_nights    SMALLINT;
    DECLARE v_price_night   DECIMAL(10,2);
    DECLARE v_existing_bill INT DEFAULT 0;

    -- Fetch nights and nightly rate
    SELECT r.num_nights, rc.price_per_night
    INTO   v_num_nights, v_price_night
    FROM   reservations r
    JOIN   rooms        rm ON rm.room_id   = r.room_id
    JOIN   room_categories rc ON rc.category_id = rm.category_id
    WHERE  r.reservation_id = p_reservation_id;

    -- Calculate charges
    SET p_room_charge = v_num_nights * v_price_night;
    SET p_tax_amount  = (p_room_charge - p_discount) * (p_tax_rate / 100);
    SET p_total       = p_room_charge - p_discount + p_tax_amount;

    -- Upsert into bills
    SELECT COUNT(*) INTO v_existing_bill
    FROM   bills WHERE reservation_id = p_reservation_id;

    IF v_existing_bill = 0 THEN
        INSERT INTO bills (reservation_id, room_charge, tax_amount, discount, total_amount, tax_rate)
        VALUES (p_reservation_id, p_room_charge, p_tax_amount, p_discount, p_total, p_tax_rate);
    ELSE
        UPDATE bills
        SET    room_charge = p_room_charge,
               tax_amount  = p_tax_amount,
               discount    = p_discount,
               total_amount = p_total,
               tax_rate    = p_tax_rate
        WHERE  reservation_id = p_reservation_id;
    END IF;
END $$



DELIMITER $$

CREATE PROCEDURE sp_monthly_revenue(
    IN p_year  INT,
    IN p_month INT
)
BEGIN
    SELECT
        DATE_FORMAT(r.checkin_date, '%Y-%m')            AS period,
        COUNT(r.reservation_id)                          AS total_reservations,
        SUM(b.room_charge)                               AS total_room_charge,
        SUM(b.tax_amount)                                AS total_tax,
        SUM(b.discount)                                  AS total_discount,
        SUM(b.total_amount)                              AS total_revenue
    FROM   reservations r
    JOIN   bills b ON b.reservation_id = r.reservation_id
    WHERE  YEAR(r.checkin_date)  = p_year
      AND  MONTH(r.checkin_date) = p_month
      AND  r.status NOT IN ('CANCELLED')
    GROUP  BY DATE_FORMAT(r.checkin_date, '%Y-%m');
END $$



DELIMITER $$

CREATE PROCEDURE sp_room_occupancy(
    IN p_start_date DATE,
    IN p_end_date   DATE
)
BEGIN
    DECLARE v_total_days INT;
    SET v_total_days = DATEDIFF(p_end_date, p_start_date) + 1;

    SELECT
        rm.room_number,
        rc.category_name,
        COUNT(r.reservation_id)                           AS times_booked,
        COALESCE(SUM(r.num_nights), 0)                    AS nights_occupied,
        v_total_days                                      AS total_days,
        ROUND(
            (COALESCE(SUM(r.num_nights), 0) / v_total_days) * 100, 2
        )                                                 AS occupancy_pct
    FROM   rooms rm
    JOIN   room_categories rc ON rc.category_id = rm.category_id
    LEFT   JOIN reservations r ON  r.room_id = rm.room_id
                                AND r.status NOT IN ('CANCELLED')
                                AND r.checkin_date  >= p_start_date
                                AND r.checkout_date <= p_end_date
    WHERE  rm.is_active = 1
    GROUP  BY rm.room_id, rm.room_number, rc.category_name
    ORDER  BY occupancy_pct DESC;
END $$

DELIMITER ;


DELIMITER $$

CREATE TRIGGER trg_prevent_double_booking
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
    DECLARE v_conflict_count INT DEFAULT 0;

    SELECT COUNT(*) INTO v_conflict_count
    FROM   reservations
    WHERE  room_id       = NEW.room_id
      AND  status        NOT IN ('CANCELLED')
      AND  checkin_date  < NEW.checkout_date
      AND  checkout_date > NEW.checkin_date;

    IF v_conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'DOUBLE_BOOKING: Room is already reserved for the selected dates.';
    END IF;
END $$


DELIMITER $$

CREATE TRIGGER trg_prevent_double_booking_update
BEFORE UPDATE ON reservations
FOR EACH ROW
BEGIN
    DECLARE v_conflict_count INT DEFAULT 0;

    IF NEW.checkin_date != OLD.checkin_date OR NEW.checkout_date != OLD.checkout_date OR NEW.room_id != OLD.room_id THEN
        SELECT COUNT(*) INTO v_conflict_count
        FROM   reservations
        WHERE  room_id         = NEW.room_id
          AND  reservation_id != NEW.reservation_id
          AND  status          NOT IN ('CANCELLED')
          AND  checkin_date    < NEW.checkout_date
          AND  checkout_date   > NEW.checkin_date;

        IF v_conflict_count > 0 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'DOUBLE_BOOKING: Room is already reserved for the selected dates.';
        END IF;
    END IF;
END $$

DELIMITER ;


INSERT INTO roles (role_name) VALUES ('STAFF'), ('ADMIN');

INSERT INTO users (username, password_hash, full_name, email, role_id)
VALUES ('admin', 'Admin@1234',
        'System Administrator', 'admin@oceanviewresort.com', 2);

INSERT INTO room_categories (category_name, price_per_night, description) VALUES
('Standard',    120.00, 'Comfortable standard room with garden view'),
('Deluxe',      180.00, 'Spacious deluxe room with partial sea view'),
('Suite',       280.00, 'Luxury suite with full ocean view and living area'),
('Ocean View',  350.00, 'Premium ocean-front room with panoramic views');

INSERT INTO rooms (room_number, category_id, floor_number, capacity) VALUES
('101', 1, 1, 2), ('102', 1, 1, 2), ('103', 1, 1, 2),
('201', 2, 2, 2), ('202', 2, 2, 3), ('203', 2, 2, 2),
('301', 3, 3, 4), ('302', 3, 3, 4),
('401', 4, 4, 2), ('402', 4, 4, 2);





