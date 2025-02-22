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
    if (patient instanceof InsuredPatient) {
      sql =
          "INSERT INTO Patient (patientID, firstname, surname, postcode, address, phone, email,"
              + " insuranceID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    } else {
      sql =
          "INSERT INTO Patient (patientID, firstname, surname, postcode, address, phone, email)"
              + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patient.getPatientId());
      stmt.setString(2, patient.getFirstName());
      stmt.setString(3, patient.getSurname());
      stmt.setString(4, patient.getPostcode());
      stmt.setString(5, patient.getAddress());
      stmt.setString(6, patient.getPhone());
      stmt.setString(7, patient.getEmail());

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
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        String patientid = rs.getString("patientID");
        String firstname = rs.getString("firstname");
        String surname = rs.getString("surname");
        String postcode = rs.getString("postcode");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        String insuranceid = rs.getString("insuranceID");

        Patient patient;
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
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          String insuranceId = rs.getString("insuranceID");
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
      stmt.setString(1, patient.getFirstName());
      stmt.setString(2, patient.getSurname());
      stmt.setString(3, patient.getPostcode());
      stmt.setString(4, patient.getAddress());
      stmt.setString(5, patient.getPhone());
      stmt.setString(6, patient.getEmail());

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
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      stmt.executeUpdate();
    }
  }
}
