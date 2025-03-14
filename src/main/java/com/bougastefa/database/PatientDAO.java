package com.bougastefa.database;

import com.bougastefa.models.InsuredPatient;
import com.bougastefa.models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for handling database operations related to Patient entities.
 * This class provides methods for performing CRUD (Create, Read, Update, Delete) operations
 * on the Patient table in the database.
 */
public class PatientDAO {
  /**
   * Adds a new patient to the database.
   * Handles both regular patients and insured patients with different SQL statements.
   *
   * @param patient The patient object to be added to the database
   * @throws SQLException If a database access error occurs
   */
  public void addPatient(Patient patient) throws SQLException {
    String sql;
    // Use different SQL statements for insured vs non-insured patients
    if (patient instanceof InsuredPatient) {
      sql =
          "INSERT INTO Patient (patientID, firstname, surname, postcode, address, phone, email,"
              + " insuranceID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    } else {
      sql =
          "INSERT INTO Patient (patientID, firstname, surname, postcode, address, phone, email)"
              + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    // Try-with-resources ensures connection resources are automatically closed
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      // Set common parameters for all patient types
      stmt.setString(1, patient.getPatientId());
      stmt.setString(2, patient.getFirstName());
      stmt.setString(3, patient.getSurname());
      stmt.setString(4, patient.getPostcode());
      stmt.setString(5, patient.getAddress());
      stmt.setString(6, patient.getEmail());
      stmt.setString(7, patient.getPhone());

      // Add insurance ID as an additional parameter for insured patients
      if (patient instanceof InsuredPatient) {
        InsuredPatient insuredPatient = (InsuredPatient) patient;
        stmt.setString(8, insuredPatient.getInsuranceId());
      }

      stmt.executeUpdate();
    }
  }

  /**
   * Retrieves all patients from the database.
   * This method constructs appropriate Patient or InsuredPatient objects based on database data.
   *
   * @return A List containing all patients in the database
   * @throws SQLException If a database access error occurs
   */
  public List<Patient> getAllPatients() throws SQLException {
    List<Patient> patients = new ArrayList<>();
    String sql = "SELECT * FROM Patient";
    
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        // Extract patient data from the current row
        String patientid = rs.getString("patientID");
        String firstname = rs.getString("firstname");
        String surname = rs.getString("surname");
        String postcode = rs.getString("postcode");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        String insuranceid = rs.getString("insuranceID");

        Patient patient;
        // Create either an InsuredPatient or regular Patient object based on insuranceID presence
        if (insuranceid != null) {
          patient =
              new InsuredPatient(
                  patientid, firstname, surname, postcode, address, email, phone, insuranceid);
        } else {
          patient = new Patient(patientid, firstname, surname, postcode, address, email, phone);
        }
        patients.add(patient);
      }
    }
    return patients;
  }

  /**
   * Retrieves a specific patient from the database by their ID.
   *
   * @param patientId The ID of the patient to retrieve
   * @return The Patient object if found, or null if no patient exists with the given ID
   * @throws SQLException If a database access error occurs
   */
  public Patient getPatientById(String patientId) throws SQLException {
    String sql = "SELECT * FROM Patient WHERE patientID = ?";
    
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          String insuranceId = rs.getString("insuranceID");
          // Return appropriate patient type based on whether insurance ID exists
          if (insuranceId != null) {
            return new InsuredPatient(
                rs.getString("patientID"),
                rs.getString("firstname"),
                rs.getString("surname"),
                rs.getString("postcode"),
                rs.getString("address"),
                rs.getString("email"),
                rs.getString("phone"),
                insuranceId);
          } else {
            return new Patient(
                rs.getString("patientID"),
                rs.getString("firstname"),
                rs.getString("surname"),
                rs.getString("postcode"),
                rs.getString("address"),
                rs.getString("email"),
                rs.getString("phone"));
          }
        }
      }
    }
    // Return null if no matching patient was found
    return null;
  }

  /**
   * Updates an existing patient's information in the database.
   * Uses different SQL statements depending on whether the patient is insured.
   *
   * @param patient The patient object with updated information
   * @throws SQLException If a database access error occurs
   */
  public void updatePatient(Patient patient) throws SQLException {
    String sql;
    // Different SQL statements for different patient types
    if (patient instanceof InsuredPatient) {
      sql =
          "UPDATE Patient SET firstname = ?, surname = ?, postcode = ?, address = ?, phone = ?,"
              + " email = ?, insuranceID = ? WHERE patientID = ?";
    } else {
      sql =
          "UPDATE Patient SET firstname = ?, surname = ?, postcode = ?, address = ?, phone = ?,"
              + " email = ? WHERE patientID = ?";
    }

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      // Set common parameters for all patient types
      stmt.setString(1, patient.getFirstName());
      stmt.setString(2, patient.getSurname());
      stmt.setString(3, patient.getPostcode());
      stmt.setString(4, patient.getAddress());
      stmt.setString(5, patient.getPhone());
      stmt.setString(6, patient.getEmail());

      // Handle parameter positions differently based on patient type
      if (patient instanceof InsuredPatient) {
        InsuredPatient insuredPatient = (InsuredPatient) patient;
        stmt.setString(7, insuredPatient.getInsuranceId());
        stmt.setString(8, patient.getPatientId());
      } else {
        stmt.setString(7, patient.getPatientId());
      }

      stmt.executeUpdate();
    }
  }

  /**
   * Deletes a patient from the database by their ID.
   *
   * @param patientId The ID of the patient to delete
   * @throws SQLException If a database access error occurs
   */
  public void deletePatient(String patientId) throws SQLException {
    String sql = "DELETE FROM Patient WHERE patientid = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      stmt.executeUpdate();
    }
  }
}
