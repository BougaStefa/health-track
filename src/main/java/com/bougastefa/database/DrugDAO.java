package com.bougastefa.database;

import com.bougastefa.models.Drug;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrugDAO {
  // Insert a new drug
  public void addDrug(Drug drug) throws SQLException {
    String sql = "INSERT INTO Drug (drugID, drugname, sideeffects, benefits) VALUES (?, ?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, drug.getDrugId());
      stmt.setString(2, drug.getName());
      stmt.setString(3, drug.getSideEffects());
      stmt.setString(4, drug.getBenefits());
      stmt.executeUpdate();
    }
  }

  // Retrieve all drugs
  public List<Drug> getAllDrugs() throws SQLException {
    List<Drug> drugs = new ArrayList<>();
    String sql = "SELECT * FROM Drug";
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

  // Retrieve drug by ID
  public Drug getDrugById(String drugId) throws SQLException {
    String sql = "SELECT * FROM Drug WHERE drugID = ?";
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

  // Update a drug
  public void updateDrug(Drug drug) throws SQLException {
    String sql = "UPDATE Drug SET drugname = ?, sideeffects = ?, benefits = ? WHERE drugID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, drug.getName());
      stmt.setString(2, drug.getSideEffects());
      stmt.setString(3, drug.getBenefits());
      stmt.setString(4, drug.getDrugId());
      stmt.executeUpdate();
    }
  }

  // Delete a drug
  public void deleteDrug(String drugId) throws SQLException {
    String sql = "DELETE FROM Drug WHERE drugID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, drugId);
      stmt.executeUpdate();
    }
  }
}
