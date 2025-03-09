package com.bougastefa.database;

import com.bougastefa.models.Visit;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing Visit entities in the database.
 * Provides methods for performing CRUD operations (Create, Read, Update, Delete)
 * on the Visit table in the database. Visit records represent patient consultations
 * with doctors and contain medical information such as symptoms and diagnosis.
 */
public class VisitDAO {
  /**
   * Inserts a new visit record into the database.
   * 
   * @param visit The Visit object containing the data to be inserted
   * @throws SQLException If a database access error occurs
   */
  public void addVisit(Visit visit) throws SQLException {
    // SQL query to insert a new visit
    String sql =
        "INSERT INTO Visit (patientID, doctorID, dateOfVisit, symptoms, diagnosis) VALUES (?, ?,"
            + " ?, ?, ?)";
    // Try-with-resources block to automatically close the connection
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

  /**
   * Retrieves all visits from the database.
   * 
   * @return A List containing all visit records in the database
   * @throws SQLException If a database access error occurs
   */
  public List<Visit> getAllVisits() throws SQLException {
    List<Visit> visits = new ArrayList<>();
    String sql = "SELECT * FROM Visit";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      // Iterate over the result set and create a new visit object for each row
      while (rs.next()) {
        Visit visit =
            new Visit(
                rs.getDate("dateOfVisit").toLocalDate(),
                rs.getString("symptoms"),
                rs.getString("diagnosis"),
                rs.getString("doctorID"),
                rs.getString("patientID"));
        visits.add(visit);
      }
    }
    return visits;
  }

  /**
   * Retrieves a specific visit from the database using the composite primary key.
   * The visit is uniquely identified by a combination of patientId, doctorId, and dateOfVisit.
   * 
   * @param patientId The ID of the patient involved in the visit
   * @param doctorId The ID of the doctor conducting the visit
   * @param dateOfVisit The date when the visit occurred
   * @return The Visit object if found, or null if no matching visit exists
   * @throws SQLException If a database access error occurs
   */
  public Visit getVisit(String patientId, String doctorId, LocalDate dateOfVisit)
      throws SQLException {
    // SQL query to retrieve a visit by patient ID, doctor ID, and date of visit (composite key)
    String sql = "SELECT * FROM Visit WHERE patientID = ? AND doctorID = ? AND dateOfVisit = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      stmt.setString(2, doctorId);
      // Convert LocalDate to SQL Date
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

  /**
   * Updates an existing visit record in the database.
   * Only the symptoms and diagnosis fields can be updated; the composite key
   * (patientId, doctorId, dateOfVisit) is used to identify which record to update.
   * 
   * @param visit The Visit object containing the updated information
   * @throws SQLException If a database access error occurs
   */
  public void updateVisit(Visit visit) throws SQLException {
    String sql =
        "UPDATE Visit SET symptoms = ?, diagnosis = ? WHERE patientID = ? AND doctorID = ? AND"
            + " dateOfVisit = ?";
    // Try-with-resources block to automatically close the connection
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

  /**
   * Deletes a visit record from the database using the composite primary key.
   * 
   * @param patientId The ID of the patient involved in the visit
   * @param doctorId The ID of the doctor conducting the visit
   * @param dateOfVisit The date when the visit occurred
   * @throws SQLException If a database access error occurs
   */
  public void deleteVisit(String patientId, String doctorId, LocalDate dateOfVisit)
      throws SQLException {
    String sql = "DELETE FROM Visit WHERE patientID = ? AND doctorID = ? AND dateOfVisit = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, patientId);
      stmt.setString(2, doctorId);
      stmt.setDate(3, Date.valueOf(dateOfVisit));
      stmt.executeUpdate();
    }
  }

  /**
   * Determines which doctor has seen a particular patient most frequently,
   * making them the patient's "primary doctor".
   * 
   * @param patientId The ID of the patient to find the primary doctor for
   * @return The ID of the doctor who has conducted the most visits with this patient,
   *         or null if the patient has no recorded visits
   * @throws SQLException If a database access error occurs
   */
  public String getPrimaryDoctorId(String patientId) throws SQLException {
    /* SQL query to retrieve the doctor with the most visits for a patient
    Not checking if there are multiple doctors with the same number of visits
    If there are multiple doctors with the same number of visits, the first one will be returned */
    String sql =
        """
            SELECT doctorID, COUNT(*) as visit_count
            FROM Visit
            WHERE patientID = ?
            GROUP BY doctorID
            ORDER BY visit_count DESC
            LIMIT 1
        """;

    // Try-with-resources block to automatically close the connection
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
