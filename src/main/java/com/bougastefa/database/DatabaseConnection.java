package com.bougastefa.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
  private static final String URL;
  private static final String USER;
  private static final String PASSWORD;

  // Ensures that the database properties are loaded only once
  static {
    // Load database properties
    try (InputStream input =
        DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
      Properties props = new Properties();
      props.load(input);
      // Initialize database connection parameters
      URL = props.getProperty("db.url");
      USER = props.getProperty("db.user");
      PASSWORD = props.getProperty("db.password");
    } catch (IOException e) {
      throw new RuntimeException("Failed to load database properties", e);
    }
  }

  // Create a connection to the database
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}
