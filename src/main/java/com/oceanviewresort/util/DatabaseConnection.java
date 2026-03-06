package com.oceanviewresort.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseConnection {
   private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
   private static final String DB_URL = System.getProperty("db.url", "jdbc:mysql://localhost:3306/ocean_view_resort?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
   private static final String DB_USER = System.getProperty("db.user", "root");
   private static final String DB_PASSWORD = System.getProperty("db.password", "root");
   private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
   private static volatile DatabaseConnection instance;
   private Connection connection;

   private DatabaseConnection() {
      this.connect();
   }

   public static DatabaseConnection getInstance() {
      if (instance == null) {
         synchronized(DatabaseConnection.class) {
            if (instance == null) {
               instance = new DatabaseConnection();
            }
         }
      }

      return instance;
   }

   public Connection getConnection() {
      try {
         if (this.connection == null || this.connection.isClosed()) {
            this.connect();
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Failed to verify connection state", e);
         this.connect();
      }

      return this.connection;
   }

   private void connect() {
      try {
         Class.forName("com.mysql.cj.jdbc.Driver");
         this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         LOGGER.info("Database connection established successfully.");
      } catch (ClassNotFoundException e) {
         LOGGER.log(Level.SEVERE, "MySQL JDBC driver not found.", e);
         throw new RuntimeException("Database driver missing: com.mysql.cj.jdbc.Driver", e);
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Cannot connect to database.", e);
         throw new RuntimeException("Database connection failed.", e);
      }
   }

   public void closeConnection() {
      if (this.connection != null) {
         try {
            this.connection.close();
            LOGGER.info("Database connection closed.");
         } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing connection.", e);
         }
      }

   }

   protected Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException("Singleton cannot be cloned.");
   }
}
