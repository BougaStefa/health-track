package com.bougastefa.database;

import com.bougastefa.models.Drug;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for Drug entities.
 * This class provides methods for CRUD (Create, Read, Update, Delete) operations
 * on Drug records in the database, as well as specialized search capabilities
 * by various drug attributes.
 */
public class DrugDAO {
  /**
   * Inserts a new drug record into the database.
   *
   * @param drug The drug object to be added to the database
   * @throws SQLException If a database access error occurs
   */
  public void addDrug(Drug drug) throws SQLException {
    String sql = "INSERT INTO Drug (drugID, drugname, sideeffects, benefits) VALUES (?, ?, ?, ?)";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, drug.getDrugId());
      stmt.setString(2, drug.getName());
      stmt.setString(3, drug.getSideEffects());
      stmt.setString(4, drug.getBenefits());
      stmt.executeUpdate();
    }
  }

  /**
   * Retrieves all drug records from the database.
   *
   * @return A list containing all drugs in the database
   * @throws SQLException If a database access error occurs
   */
  public List<Drug> getAllDrugs() throws SQLException {
    List<Drug> drugs = new ArrayList<>();
    String sql = "SELECT * FROM Drug";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        Drug drug =
            new Drug(
                rs.getString("drugID"),
                rs.getString("drugname"),
                rs.getString("sideeffects"),
                rs.getString("benefits"));
        drugs.add(drug);
      }
    }
    return drugs;
  }

  /**
   * Retrieves a drug record from the database by its ID.
   *
   * @param drugId The unique identifier of the drug to retrieve
   * @return The drug object if found, null otherwise
   * @throws SQLException If a database access error occurs
   */
  public Drug getDrugById(String drugId) throws SQLException {
    String sql = "SELECT * FROM Drug WHERE drugID = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, drugId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return new Drug(
              rs.getString("drugID"),
              rs.getString("drugname"),
              rs.getString("sideeffects"),
              rs.getString("benefits"));
        }
      }
    }
    return null;
  }

  /**
   * Updates an existing drug record in the database.
   *
   * @param drug The drug object with updated information
   * @throws SQLException If a database access error occurs
   */
  public void updateDrug(Drug drug) throws SQLException {
    String sql = "UPDATE Drug SET drugname = ?, sideeffects = ?, benefits = ? WHERE drugID = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, drug.getName());
      stmt.setString(2, drug.getSideEffects());
      stmt.setString(3, drug.getBenefits());
      stmt.setString(4, drug.getDrugId());
      stmt.executeUpdate();
    }
  }

  /**
   * Deletes a drug record from the database based on its ID.
   *
   * @param drugId The unique identifier of the drug to delete
   * @throws SQLException If a database access error occurs
   */
  public void deleteDrug(String drugId) throws SQLException {
    String sql = "DELETE FROM Drug WHERE drugID = ?";
    // Try-with-resources block to automatically close the connection
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, drugId);
      stmt.executeUpdate();
    }
  }

  /**
   * Helper method to retrieve drugs by a given column using partial matching.
   * This method uses SQL LIKE operator for substring searching within the specified column.
   *
   * @param column The database column to search in
   * @param value The value to search for (will be matched partially)
   * @return A list of drugs matching the search criteria
   * @throws SQLException If a database access error occurs
   */
  private List<Drug> getDrugsByColumn(String column, String value) throws SQLException {
    List<Drug> drugs = new ArrayList<>();
    String sql = "SELECT * FROM Drug WHERE " + column + " LIKE ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, "%" + value + "%");
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Drug drug =
              new Drug(
                  rs.getString("drugID"),
                  rs.getString("drugname"),
                  rs.getString("sideeffects"),
                  rs.getString("benefits"));
          drugs.add(drug);
        }
      }
    }

    return drugs;
  }

  /**
   * Retrieves drugs by name using partial matching.
   * Allows searching for drugs with names containing the specified string.
   *
   * @param name The drug name or partial name to search for
   * @return A list of drugs with matching names
   * @throws SQLException If a database access error occurs
   */
  public List<Drug> getDrugsByName(String name) throws SQLException {
    return getDrugsByColumn("drugname", name);
  }

  /**
   * Retrieves drugs by side effects using partial matching.
   * Allows searching for drugs with side effects containing the specified string.
   *
   * @param sideEffects The side effects or partial side effects to search for
   * @return A list of drugs with matching side effects
   * @throws SQLException If a database access error occurs
   */
  public List<Drug> getDrugsBySideEffects(String sideEffects) throws SQLException {
    return getDrugsByColumn("sideeffects", sideEffects);
  }

  /**
   * Retrieves drugs by benefits using partial matching.
   * Allows searching for drugs with benefits containing the specified string.
   *
   * @param benefits The benefits or partial benefits to search for
   * @return A list of drugs with matching benefits
   * @throws SQLException If a database access error occurs
   */
  public List<Drug> getDrugsByBenefits(String benefits) throws SQLException {
    return getDrugsByColumn("benefits", benefits);
  }
}
