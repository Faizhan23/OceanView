# 🌊 Ocean View Resort — Hotel Management System

A full-stack Java EE Hotel Management System for Ocean View Resort, implementing a clean 3-tier architecture, multiple design patterns, stored procedures, and comprehensive test coverage. Built as part of the CIS6003 Advanced Programming module at Cardiff Metropolitan University.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Database Design](#database-design)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [Version History](#version-history)

---

## 📖 Project Overview

The Ocean View Resort Hotel Management System is a web-based application that enables resort staff to manage room reservations, guest information, billing, and reporting. The system is built on the Java EE Servlet/JSP stack, backed by a MySQL database normalized to Third Normal Form (3NF), and follows industry-standard patterns for security, maintainability, and scalability.

---

## ✨ Features

### Core Business Functions
- **Authentication** — BCrypt-secured login, session management, and role-based access control via `AuthFilter`
- **Room Management** — View, filter, and manage room inventory across multiple categories (Standard, Deluxe, Suite, Ocean View)
- **Reservation Management** — Create, view, search, cancel reservations with duplicate booking prevention
- **Billing System** — Automated bill generation via stored procedure `sp_calculate_bill`, mark-as-paid workflow
- **Reports & Analytics** — Monthly revenue (`sp_monthly_revenue`) and room occupancy (`sp_room_occupancy`) reports

### Technical Highlights
- Double-booking prevention at both application layer and MySQL trigger layer
- Unique reservation reference generator (`OVR-YYYY-NNNNNN` format)
- SQL injection prevention via `PreparedStatement` throughout
- Audit logging with timestamps and IP addresses
- Input validation via `ValidationUtil` (email, phone, date format, blank checks)
- Session expiration with redirect to login

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 8+ |
| Web Framework | Java EE — Servlets & JSP |
| Server | Apache Tomcat 9.0+ |
| Database | MySQL 8.0 |
| Build Tool | Maven |
| Security | BCrypt password hashing |
| Testing | JUnit + JaCoCo |
| Version Control | Git / GitHub |

---

## 🏗 Architecture

The system follows a strict **3-Tier Architecture**:

```
┌─────────────────────────────────┐
│  Presentation Layer (JSP/HTML)  │   ← Views rendered server-side
├─────────────────────────────────┤
│  Business Logic Layer (Service) │   ← Validation, rules, orchestration
├─────────────────────────────────┤
│  Data Access Layer (DAO/MySQL)  │   ← Persistence, stored procedures
└─────────────────────────────────┘
```

Each module (Reservation, Billing, Room, User, Report) follows the same vertical slice:

```
Servlet (HTTP) → Service (Business Logic) → DAO (Database)
```

---

## 🎨 Design Patterns

### 1. Singleton — `DatabaseConnection.java`
Ensures a single, thread-safe database connection instance across the application using **double-checked locking** and the `volatile` keyword.

```java
private static volatile DatabaseConnection instance;

public static DatabaseConnection getInstance() {
    if (instance == null) {
        synchronized (DatabaseConnection.class) {
            if (instance == null) {
                instance = new DatabaseConnection();
            }
        }
    }
    return instance;
}
```

**Benefit:** Prevents resource exhaustion, centralizes DB configuration, and supports concurrent users safely.

---

### 2. Factory — `BillingService.java`
Implements **Dependency Injection Factory** — the service accepts injected DAO implementations via constructor overloading, enabling testability without altering production code.

```java
// Production constructor (acts as factory)
public BillingService() {
    this.billDAO = new BillDAOImpl();
    this.reservationDAO = new ReservationDAOImpl();
}

// Test constructor (inject mocks)
public BillingService(BillDAO billDAO, ReservationDAO reservationDAO) {
    this.billDAO = billDAO;
    this.reservationDAO = reservationDAO;
}
```

**Benefit:** Loose coupling, testability, and easy extension for new billing strategies.

---

### 3. Chain of Responsibility — `AuthFilter` + `EncodingFilter`
All HTTP requests pass through an ordered filter chain before reaching any Servlet.

```
Request → EncodingFilter (UTF-8) → AuthFilter (Session check) → Servlet
```

**Benefit:** Cross-cutting concerns (encoding, authentication) enforced once centrally — no duplication across Servlets.

---

## 🗃 Database Design

The database (`ocean_view_resort`) is normalized to **Third Normal Form (3NF)** and consists of 8 tables:

| Table | Purpose |
|---|---|
| `users` | Staff accounts with BCrypt passwords and roles |
| `roles` | Role definitions (Admin, Receptionist) |
| `rooms` | Physical room inventory |
| `room_categories` | Pricing tiers (Standard, Deluxe, Suite, Ocean View) |
| `guests` | Guest personal information |
| `reservations` | Booking records with status lifecycle |
| `bills` | Financial records (charge, tax, discount, total) |
| `audit_log` | Complete system action trail |

### Stored Procedures
- `sp_calculate_bill` — Computes room charge, tax, discount, and total
- `sp_monthly_revenue` — Aggregates revenue for a given month/year
- `sp_room_occupancy` — Calculates occupancy % by room over a date range

### Triggers
- `trg_prevent_double_booking` — Prevents overlapping reservations on INSERT
- `trg_prevent_double_booking_update` — Prevents overlapping reservations on UPDATE

---

## ✅ Prerequisites

- Java JDK 8 or higher
- Apache Tomcat 9.0+
- XAMPP (MySQL 8.0)
- Maven 3.6+
- IntelliJ IDEA or VS Code

---

## 🚀 Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ocean-view-resort
```

### 2. Database Setup

Start XAMPP and ensure MySQL is running, then open phpMyAdmin at `http://localhost/phpmyadmin`.

```sql
CREATE DATABASE ocean_view_resort;
```

Import the schema:

```bash
mysql -u root -p ocean_view_resort < src/main/resources/db.sql
```

Default database configuration:

| Setting | Value |
|---|---|
| Database | `ocean_view_resort` |
| Host | `localhost` |
| Port | `3306` |
| Username | `root` |
| Password | *(empty)* |

### 3. Build the Project

```bash
mvn clean package
```

This produces `target/ocean-view-resort.war`.

### 4. Deploy to Tomcat

```bash
cp target/ocean-view-resort.war /path/to/tomcat/webapps/
# Start Tomcat
bin/startup.sh        # Linux/Mac
bin/startup.bat       # Windows
```

### 5. Access the Application

```
URL: http://localhost:8080/ocean-view-resort
```

**Default Admin Credentials:**
| Field | Value |
|---|---|
| Username | `admin` |
| Password | `admin123` |

> ⚠️ Change the default password immediately after first login.

---

## 🧪 Running Tests

```bash
# Run all unit tests
mvn test

# Run a specific test class
mvn test -Dtest=BillingServiceTest

# Generate JaCoCo coverage report
mvn jacoco:report
# Report available at: target/site/jacoco/index.html
```

### Test Coverage Summary

| Module | Coverage |
|---|---|
| Authentication & Session | ✅ Covered |
| Reservation CRUD | ✅ Covered |
| Billing (sp_calculate_bill) | ✅ Covered |
| Input Validation (ValidationUtil) | ✅ Covered |
| SQL Injection Prevention | ✅ Covered |
| Double-Booking Prevention | ✅ Covered |
| Singleton (DatabaseConnection) | ✅ Covered |

---

## 📁 Project Structure

```
src/main/java/com/oceanviewresort/
├── dao/                    # Data Access Objects (interfaces + impl)
│   ├── BillDAO.java
│   ├── BillDAOImpl.java
│   ├── ReservationDAO.java
│   └── ...
├── model/                  # Domain entity classes
│   ├── Bill.java
│   ├── Reservation.java
│   ├── Room.java
│   └── ...
├── service/                # Business logic layer
│   ├── BillingService.java
│   ├── ReservationService.java
│   └── ...
├── servlet/                # HTTP controllers
│   ├── BillingServlet.java
│   ├── ReservationServlet.java
│   └── ...
├── filter/                 # Web filters
│   ├── AuthFilter.java
│   └── EncodingFilter.java
└── util/                   # Utilities
    ├── DatabaseConnection.java
    ├── ValidationUtil.java
    └── ReservationRefGenerator.java

src/main/webapp/
├── WEB-INF/
│   └── web.xml
├── views/                  # JSP pages
└── assets/                 # CSS, JS, images

src/test/java/              # JUnit test classes
src/main/resources/
└── db.sql                  # Full database schema + seed data
```

---



---

## 👤 Author

**Mohamed Nafaz Mohamed Faizhan**
Student ID: Cl/BSCSD/33/156
Cardiff Metropolitan University — B.Sc. (Hons) Software Engineering
Module: CIS6003 Advanced Programming | Lecturer: Mr. Bhagya Rathnayake
