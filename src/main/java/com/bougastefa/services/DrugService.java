package com.bougastefa.services;

import com.bougastefa.database.DrugDAO;
import com.bougastefa.models.Drug;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrugService {
  private DrugDAO drugDAO = new DrugDAO();
  private static final Logger logger = LoggerFactory.getLogger(DrugService.class);

  // Add a new drug
  public void addDrug(Drug drug) {
    if (drug == null) {
      throw new IllegalArgumentException("Drug cannot be null");
    }
    try {
      drugDAO.addDrug(drug);
      logger.info("Drug added successfully: {}", drug.getDrugId());
    } catch (SQLException e) {
      logger.error("Error adding drug: {}", drug.getDrugId(), e);
      throw new ServiceException("Failed to add drug", e);
    }
  }

  // Retrieve all drugs
  public List<Drug> getAllDrugs() {
    try {
      return drugDAO.getAllDrugs();
    } catch (SQLException e) {
      logger.error("Error fetching drugs", e);
      return List.of(); // Return an empty list on error
    }
  }

  // Retrieve a drug by ID
  public Drug getDrugById(String drugId) {
    if (drugId == null || drugId.isEmpty()) {
      throw new IllegalArgumentException("Drug ID cannot be empty");
    }
    try {
      return drugDAO.getDrugById(drugId);
    } catch (SQLException e) {
      logger.error("Error fetching drug: {}", drugId, e);
      return null;
    }
  }

  // Retrieve drugs by name
  public List<Drug> getDrugsByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Drug name cannot be null");
    }
    try {
      return drugDAO.getDrugsByName(name);
    } catch (SQLException e) {
      logger.error("Error fetching drugs by name: {}", name, e);
      return List.of();
    }
  }

  // Retrieve drugs by side effects
  public List<Drug> getDrugsBySideEffects(String sideEffects) {
    if (sideEffects == null) {
      throw new IllegalArgumentException("Side effects cannot be null");
    }
    try {
      return drugDAO.getDrugsBySideEffects(sideEffects);
    } catch (SQLException e) {
      logger.error("Error fetching drugs by side effects: {}", sideEffects, e);
      return List.of();
    }
  }

  // Retrieve drugs by benefits
  public List<Drug> getDrugsByBenefits(String benefits) {
    if (benefits == null) {
      throw new IllegalArgumentException("Benefits cannot be null");
    }
    try {
      return drugDAO.getDrugsByBenefits(benefits);
    } catch (SQLException e) {
      logger.error("Error fetching drugs by benefits: {}", benefits, e);
      return List.of();
    }
  }

  // Update a drug
  public void updateDrug(Drug drug) {
    if (drug == null) {
      throw new IllegalArgumentException("Drug cannot be null");
    }
    try {
      drugDAO.updateDrug(drug);
      logger.info("Drug updated successfully: {}", drug.getDrugId());
    } catch (SQLException e) {
      logger.error("Error updating drug: {}", drug.getDrugId(), e);
      throw new ServiceException("Failed to update drug", e);
    }
  }

  // Delete a drug
  public void deleteDrug(String drugId) {
    if (drugId == null || drugId.isEmpty()) {
      throw new IllegalArgumentException("Drug ID cannot be empty");
    }
    try {
      drugDAO.deleteDrug(drugId);
      logger.info("Drug deleted successfully: {}", drugId);
    } catch (SQLException e) {
      logger.error("Error deleting drug: {}", drugId, e);
      throw new ServiceException("Failed to delete drug", e);
    }
  }
}
