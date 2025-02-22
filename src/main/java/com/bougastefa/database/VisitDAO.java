package com.bougastefa.database;

import com.bougastefa.models.Visit;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VisitDAO {
  // Insert a new visit
  public void addVisit(Visit visit) throws SQLException {
    String sql =
        "INSERT INTO Visit (patientID, doctorID, dateOfVisit, symptoms, diagnosis) VALUES (?, ?,"
            + " ?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, visit.getPatientId());
      stmt.setString(2, visit.getDoctorId());
      stmt.setDate(3, Date.valueOf(visit.getDateOfVisit()));
      stmt.setString(4, visit.getSymptoms());
      stmt.setString(5, visit.getDiagnosis());
      stmt.executeUpdate();
    }
  }

  // Retrieve all visits
  public List<Visit> getAllVisits() throws SQLException {
    List<Visit> visits = new ArrayList<>();
    String sql = "SELECT * FROM Visit";
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        Visit visit =
            new Visit(
                rs.getDate("dateOfVisit").toLocalDate(),
                rs.getString("patientID"),
                rs.getString("doctorID"),
                rs.getString("symptoms"),
                rs.getString("diagnosis"));
        visits.add(visit);
      }
    }
    return visits;
  }

  // Retrieve visit by ID(s)
  public Visit getVisit(String patientId, String doctorId, LocalDate dateOfVisit)
      throws SQLException {
    String sql = "SELECT * FROM Visit WHERE patientID = ? AND doctorID = ? AND dateOfVisit = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      stmt.setString(2, doctorId);
      stmt.setDate(3, Date.valueOf(dateOfVisit));
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return new Visit(
              rs.getDate("dateOfVisit").toLocalDate(),
              rs.getString("patientID"),
              rs.getString("doctorID"),
              rs.getString("symptoms"),
              rs.getString("diagnosis"));
        }
      }
    }
    return null;
  }

  // Update a visit
  public void updateVisit(Visit visit) throws SQLException {
    String sql =
        "UPDATE Visit SET symptoms = ?, diagnosis = ? WHERE patientID = ? AND doctorID = ? AND"
            + " dateOfVisit = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, visit.getSymptoms());
      stmt.setString(2, visit.getDiagnosis());
      stmt.setString(3, visit.getPatientId());
      stmt.setString(4, visit.getDoctorId());
      stmt.setDate(5, Date.valueOf(visit.getDateOfVisit()));
      stmt.executeUpdate();
    }
  }

  // Delete a visit
  public void deleteVisit(String patientId, String doctorId, LocalDate dateOfVisit)
      throws SQLException {
    String sql = "DELETE FROM Visit WHERE patientID = ? AND doctorID = ? AND dateOfVisit = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      stmt.setString(2, doctorId);
      stmt.setDate(3, Date.valueOf(dateOfVisit));
      stmt.executeUpdate();
    }
  }

  // Find Primary Doctor based on amount of visit for patient
  public String getPrimaryDoctorId(String patientId) throws SQLException {
    String sql =
        """
            SELECT doctorID, COUNT(*) as visit_count
            FROM Visit
            WHERE patientID = ?
            GROUP BY doctorID
            ORDER BY visit_count DESC
            LIMIT 1
        """;

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return rs.getString("doctorID");
        }
      }
    }
    return null;
  }
}
