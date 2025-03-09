package com.bougastefa.database;

import com.bougastefa.models.Prescription;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing Prescription entities in the database.
 * Provides methods for performing CRUD operations (Create, Read, Update, Delete)
 * on the Prescription table in the database.
 */
public class PrescriptionDAO {
  /**
   * Inserts a new prescription record into the database.
   * 
   * @param prescription The Prescription object containing the data to be inserted
   * @throws SQLException If a database access error occurs
   */
  public void addPrescription(Prescription prescription) throws SQLException {
    String sql =
        "INSERT INTO Prescription (prescriptionID, dateprescribed, dosage, duration, comment,"
            + " drugID, doctorID, patientID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      // Set all parameters for the prepared statement
      stmt.setString(1, prescription.getPrescriptionId());
      stmt.setDate(2, Date.valueOf(prescription.getDateOfPrescribe()));
      stmt.setInt(3, prescription.getDosage());
      stmt.setInt(4, prescription.getDuration());
      stmt.setString(5, prescription.getComment());
      stmt.setString(6, prescription.getDrugId());
      stmt.setString(7, prescription.getDoctorId());
      stmt.setString(8, prescription.getPatientId());
      stmt.executeUpdate();
    }
  }

  /**
   * Retrieves all prescriptions from the database, ordered by prescription date (most recent first).
   * 
   * @return A List containing all prescriptions in the database
   * @throws SQLException If a database access error occurs
   */
  public List<Prescription> getAllPrescriptions() throws SQLException {
    List<Prescription> prescriptions = new ArrayList<>();
    // Retrieve all prescriptions in descending order of date prescribed
    String sql = "SELECT * FROM Prescription ORDER BY dateprescribed DESC";

    // Try-with-resources block to automatically close all database resources
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        // Create a new prescription object for each row in the result set
        Prescription prescription =
            new Prescription(
                rs.getString("prescriptionID"),
                rs.getDate("dateprescribed").toLocalDate(),
                rs.getInt("dosage"),
                rs.getInt("duration"),
                rs.getString("comment"),
                rs.getString("drugID"),
                rs.getString("doctorID"),
                rs.getString("patientID"));
        prescriptions.add(prescription);
      }
    }
    return prescriptions;
  }

  /**
   * Retrieves a specific prescription from the database by its ID.
   * 
   * @param prescriptionId The unique identifier of the prescription to retrieve
   * @return The Prescription object if found, or null if no prescription exists with the given ID
   * @throws SQLException If a database access error occurs
   */
  public Prescription getPrescriptionById(String prescriptionId) throws SQLException {
    String sql = "SELECT * FROM Prescription WHERE prescriptionID = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, prescriptionId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          // Create and return a Prescription object from the result set data
          return new Prescription(
              rs.getString("prescriptionID"),
              rs.getDate("dateprescribed").toLocalDate(),
              rs.getInt("dosage"),
              rs.getInt("duration"),
              rs.getString("comment"),
              rs.getString("drugID"),
              rs.getString("doctorID"),
              rs.getString("patientID"));
        }
      }
    }
    // Return null if no prescription with the specified ID was found
    return null;
  }

  /**
   * Updates an existing prescription record in the database.
   * 
   * @param prescription The Prescription object containing updated information
   * @throws SQLException If a database access error occurs
   */
  public void updatePrescription(Prescription prescription) throws SQLException {
    String sql =
        "UPDATE Prescription SET dateprescribed = ?, dosage = ?, duration = ?, comment = ?, drugID"
            + " = ?, doctorID = ?, patientID = ? WHERE prescriptionID = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      // Set all parameters for the update statement
      stmt.setDate(1, Date.valueOf(prescription.getDateOfPrescribe()));
      stmt.setInt(2, prescription.getDosage());
      stmt.setInt(3, prescription.getDuration());
      stmt.setString(4, prescription.getComment());
      stmt.setString(5, prescription.getDrugId());
      stmt.setString(6, prescription.getDoctorId());
      stmt.setString(7, prescription.getPatientId());
      stmt.setString(8, prescription.getPrescriptionId());
      stmt.executeUpdate();
    }
  }

  /**
   * Deletes a prescription from the database by its ID.
   * 
   * @param prescriptionId The unique identifier of the prescription to delete
   * @throws SQLException If a database access error occurs
   */
  public void deletePrescription(String prescriptionId) throws SQLException {
    String sql = "DELETE FROM Prescription WHERE prescriptionID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, prescriptionId);
      stmt.executeUpdate();
    }
  }
}
