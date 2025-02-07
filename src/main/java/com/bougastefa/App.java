package com.bougastefa;

import com.bougastefa.database.DatabaseConnection;
import com.bougastefa.database.PatientDAO;
import com.bougastefa.models.Patient;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
    PatientDAO patientDAO = new PatientDAO();

    try {
      // Retrieve all patients from the database
      List<Patient> patients = patientDAO.getAllPatients();

      // Print the results
      if (patients.isEmpty()) {
        System.out.println("No patients found in the database.");
      } else {
        System.out.println("Patients in the database:");
        for (Patient patient : patients) {
          System.out.println(patient);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error retrieving patients: " + e.getMessage());
    }
  }
}
