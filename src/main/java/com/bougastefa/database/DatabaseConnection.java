package com.bougastefa.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
  private static Connection connection = null;

  public static Connection getConnection() {
    if (connection == null) {
      try (InputStream input =
          DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
        Properties props = new Properties();
        props.load(input);

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Connected to the database!");
      } catch (IOException | SQLException e) {
        e.printStackTrace();
      }
    }
    return connection;
  }
}
