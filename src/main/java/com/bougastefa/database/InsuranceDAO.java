package com.bougastefa.database;

import com.bougastefa.models.Insurance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDAO {
  // Insert a new insurance
  public void addInsurance(Insurance insurance) throws SQLException {
    String sql = "INSERT INTO Insurance (insuranceID, company, address, phone) VALUES (?, ?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, insurance.getInsuranceId());
      stmt.setString(2, insurance.getCompany());
      stmt.setString(3, insurance.getAddress());
      stmt.setString(4, insurance.getPhone());
      stmt.executeUpdate();
    }
  }

  // Retrieve all insurances
  public List<Insurance> getAllInsurances() throws SQLException {
    List<Insurance> insurances = new ArrayList<>();
    String sql = "SELECT * FROM Insurance";
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        Insurance insurance =
            new Insurance(
                rs.getString("insuranceID"),
                rs.getString("company"),
                rs.getString("address"),
                rs.getString("phone"));
        insurances.add(insurance);
      }
    }
    return insurances;
  }

  // Retrieve insurance by ID
  public Insurance getInsuranceById(String insuranceId) throws SQLException {
    String sql = "SELECT * FROM Insurance WHERE insuranceID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, insuranceId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return new Insurance(
              rs.getString("insuranceID"),
              rs.getString("company"),
              rs.getString("address"),
              rs.getString("phone"));
        }
      }
    }
    return null;
  }

  // Update an insurance
  public void updateInsurance(Insurance insurance) throws SQLException {
    String sql = "UPDATE Insurance SET company = ?, address = ?, phone = ? WHERE insuranceID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, insurance.getCompany());
      stmt.setString(2, insurance.getAddress());
      stmt.setString(3, insurance.getPhone());
      stmt.setString(4, insurance.getInsuranceId());
      stmt.executeUpdate();
    }
  }

  // Delete an insurance
  public void deleteInsurance(String insuranceId) throws SQLException {
    String sql = "DELETE FROM Insurance WHERE insuranceID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, insuranceId);
      stmt.executeUpdate();
    }
  }
}
