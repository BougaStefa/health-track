package com.bougastefa.database;

import com.bougastefa.models.InsuredPatient;
import com.bougastefa.models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
  // Insert a new patient
  public void addPatient(Patient patient) throws SQLException {
    String sql;
    // Check if the patient is an insured patient and insert the insurance ID if it is
    if (patient instanceof InsuredPatient) {
      sql =
          "INSERT INTO Patient (patientID, firstname, surname, postcode, address, phone, email,"
              + " insuranceID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    } else {
      sql =
          "INSERT INTO Patient (patientID, firstname, surname, postcode, address, phone, email)"
              + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patient.getPatientId());
      stmt.setString(2, patient.getFirstName());
      stmt.setString(3, patient.getSurname());
      stmt.setString(4, patient.getPostcode());
      stmt.setString(5, patient.getAddress());
      stmt.setString(6, patient.getPhone());
      stmt.setString(7, patient.getEmail());

      // Set the insurance ID if the patient is an insured patient
      if (patient instanceof InsuredPatient) {
        InsuredPatient insuredPatient = (InsuredPatient) patient;
        stmt.setString(8, insuredPatient.getInsuranceId());
      }

      stmt.executeUpdate();
    }
  }

  // Retrieve all patients
  public List<Patient> getAllPatients() throws SQLException {
    List<Patient> patients = new ArrayList<>();
    String sql = "SELECT * FROM Patient";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        // Create a new patient object for each row
        String patientid = rs.getString("patientID");
        String firstname = rs.getString("firstname");
        String surname = rs.getString("surname");
        String postcode = rs.getString("postcode");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        String insuranceid = rs.getString("insuranceID");

        Patient patient;
        // Check if the patient is an insured patient and create the appropriate object
        if (insuranceid != null) {
          patient =
              new InsuredPatient(
                  patientid, firstname, surname, postcode, address, phone, email, insuranceid);
        } else {
          patient = new Patient(patientid, firstname, surname, postcode, address, phone, email);
        }
        patients.add(patient);
      }
    }
    return patients;
  }

  // Retrieve patient by ID
  public Patient getPatientById(String patientId) throws SQLException {
    String sql = "SELECT * FROM Patient WHERE patientID = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          String insuranceId = rs.getString("insuranceID");
          // Check if the patient is an insured patient and create the appropriate object
          if (insuranceId != null) {
            return new InsuredPatient(
                rs.getString("patientID"),
                rs.getString("firstname"),
                rs.getString("surname"),
                rs.getString("postcode"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("email"),
                insuranceId);
          } else {
            return new Patient(
                rs.getString("patientID"),
                rs.getString("firstname"),
                rs.getString("surname"),
                rs.getString("postcode"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("email"));
          }
        }
      }
    }
    return null;
  }

  // Update a patient
  public void updatePatient(Patient patient) throws SQLException {
    String sql;
    // Check if the patient is an insured patient and update the insurance ID if it is
    if (patient instanceof InsuredPatient) {
      sql =
          "UPDATE Patient SET firstname = ?, surname = ?, postcode = ?, address = ?, phone = ?,"
              + " email = ?, insuranceID = ? WHERE patientID = ?";
    } else {
      sql =
          "UPDATE Patient SET firstname = ?, surname = ?, postcode = ?, address = ?, phone = ?,"
              + " email = ? WHERE patientID = ?";
    }

    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patient.getFirstName());
      stmt.setString(2, patient.getSurname());
      stmt.setString(3, patient.getPostcode());
      stmt.setString(4, patient.getAddress());
      stmt.setString(5, patient.getPhone());
      stmt.setString(6, patient.getEmail());

      // Set the insurance ID if the patient is an insured patient
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

  // Delete a patient
  public void deletePatient(String patientId) throws SQLException {
    String sql = "DELETE FROM Patient WHERE patientid = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      stmt.executeUpdate();
    }
  }
}
