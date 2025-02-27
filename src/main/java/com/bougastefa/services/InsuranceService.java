package com.bougastefa.services;

import com.bougastefa.database.InsuranceDAO;
import com.bougastefa.models.Insurance;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsuranceService {
  private InsuranceDAO insuranceDAO = new InsuranceDAO();
  private static final Logger logger = LoggerFactory.getLogger(InsuranceService.class);

  // Add a new insurance
  public void addInsurance(Insurance insurance) {
    if (insurance == null) {
      throw new IllegalArgumentException("Insurance cannot be null");
    }
    try {
      insuranceDAO.addInsurance(insurance);
      logger.info("Insurance added successfully: {}", insurance.getInsuranceId());
    } catch (SQLException e) {
      logger.error("Error adding insurance: {}", insurance.getInsuranceId(), e);
      throw new ServiceException("Failed to add insurance", e);
    }
  }

  // Retrieve all insurances
  public List<Insurance> getAllInsurances() {
    try {
      return insuranceDAO.getAllInsurances();
    } catch (SQLException e) {
      logger.error("Error fetching insurances", e);
      return List.of(); // Return an empty list on error
    }
  }

  // Retrieve an insurance by ID
  public Insurance getInsuranceById(String insuranceId) {
    if (insuranceId == null || insuranceId.isEmpty()) {
      throw new IllegalArgumentException("Insurance ID cannot be empty");
    }
    try {
      return insuranceDAO.getInsuranceById(insuranceId);
    } catch (SQLException e) {
      logger.error("Error fetching insurance: {}", insuranceId, e);
      return null;
    }
  }

  // Update an insurance
  public void updateInsurance(Insurance insurance) {
    if (insurance == null) {
      throw new IllegalArgumentException("Insurance cannot be null");
    }
    try {
      insuranceDAO.updateInsurance(insurance);
      logger.info("Insurance updated successfully: {}", insurance.getInsuranceId());
    } catch (SQLException e) {
      logger.error("Error updating insurance: {}", insurance.getInsuranceId(), e);
      throw new ServiceException("Failed to update insurance", e);
    }
  }

  // Delete an insurance
  public void deleteInsurance(String insuranceId) {
    if (insuranceId == null || insuranceId.isEmpty()) {
      throw new IllegalArgumentException("Insurance ID cannot be empty");
    }
    try {
      insuranceDAO.deleteInsurance(insuranceId);
      logger.info("Insurance deleted successfully: {}", insuranceId);
    } catch (SQLException e) {
      logger.error("Error deleting insurance: {}", insuranceId, e);
      throw new ServiceException("Failed to delete insurance", e);
    }
  }
}
