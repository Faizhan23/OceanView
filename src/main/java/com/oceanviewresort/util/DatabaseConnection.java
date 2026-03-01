package com.oceanviewresort.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton pattern implementation for database connection management.
 * Provides thread-safe, centralised access to a single database connection.
 *
 * Design Pattern: Singleton (thread-safe via synchronized getInstance)
 */
public final class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // ── Connection parameters (override via environment variables in production) ──
    private static final String DB_URL      = System.getProperty("db.url",      "jdbc:mysql://localhost:3306/ocean_view_resort?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    private static final String DB_USER     = System.getProperty("db.user",     "root");
    private static final String DB_PASSWORD = System.getProperty("db.password", "root");
    private static final String DB_DRIVER   = "com.mysql.cj.jdbc.Driver";

    // Volatile ensures visibility across threads
    private static volatile DatabaseConnection instance;
    private Connection connection;

    /** Private constructor – enforces Singleton */
    private DatabaseConnection() {
        connect();
    }

    /**
     * Returns the single instance of DatabaseConnection.
     * Double-checked locking for thread safety.
     */
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

    /**
     * Returns a valid, open JDBC Connection.
     * Automatically reconnects if the connection has been lost.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to verify connection state", e);
            connect();
        }
        return connection;
    }

    /** Establishes the JDBC connection. */
    private void connect() {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            LOGGER.info("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC driver not found.", e);
            throw new RuntimeException("Database driver missing: " + DB_DRIVER, e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Cannot connect to database.", e);
            throw new RuntimeException("Database connection failed.", e);
        }
    }

    /** Closes the connection – call only on application shutdown. */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed.");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection.", e);
            }
        }
    }

    // Prevent cloning – Singleton must not be cloned
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton cannot be cloned.");
    }
}
