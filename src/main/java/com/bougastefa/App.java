package com.bougastefa;

import com.bougastefa.database.DatabaseConnection;
import java.sql.Connection;

public class App {
  public static void main(String[] args) {
    System.out.println("Starting App...");

    // Test database connection
    Connection conn = DatabaseConnection.getConnection();
    if (conn != null) {
      System.out.println("Database connection successful!");
    } else {
      System.out.println("Failed to connect to the database.");
    }
  }
}
