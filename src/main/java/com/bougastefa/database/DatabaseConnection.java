package com.bougastefa.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provides centralized database connection management for the application.
 * This utility class handles the loading of database configuration from
 * an external properties file and provides methods to establish database
 * connections using those properties.
 */
public class DatabaseConnection {
  /** Database connection URL loaded from configuration */
  private static final String URL;
  /** Database username loaded from configuration */
  private static final String USER;
  /** Database password loaded from configuration */
  private static final String PASSWORD;

  /**
   * Static initialization block that loads database configuration parameters
   * from the properties file when the class is first loaded.
   * This ensures configuration is read only once during application startup.
   */
  static {
    // Load database properties from the configuration file
    try (InputStream input =
        DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
      Properties props = new Properties();
      props.load(input);
      // Initialize database connection parameters from the properties
      URL = props.getProperty("db.url");
      USER = props.getProperty("db.user");
      PASSWORD = props.getProperty("db.password");
    } catch (IOException e) {
      // Convert checked exception to unchecked for easier error handling
      throw new RuntimeException("Failed to load database properties", e);
    }
  }

  /**
   * Creates and returns a new database connection using the configured properties.
   * This method centralizes connection creation to ensure consistent configuration
   * across the application.
   *
   * @return A Connection object representing an active database connection
   * @throws SQLException If a database access error occurs or connection parameters are invalid
   */
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}
