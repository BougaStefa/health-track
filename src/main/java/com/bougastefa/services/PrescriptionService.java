package com.bougastefa.services;

import com.bougastefa.database.PrescriptionDAO;
import com.bougastefa.models.Prescription;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrescriptionService {
  private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
  private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

  // Add a new prescription
  public void addPrescription(Prescription prescription) {
    if (prescription == null) {
      throw new IllegalArgumentException("Prescription cannot be null");
    }
    Prescription existingPrescription = getPrescriptionById(prescription.getPrescriptionId());
    if (existingPrescription != null) {
      throw new IllegalArgumentException("Prescription ID already exists: " + prescription.getPrescriptionId());
    }
    try {
      prescriptionDAO.addPrescription(prescription);
      logger.info("Prescription added successfully: {}", prescription.getPrescriptionId());
    } catch (SQLException e) {
      logger.error("Error adding prescription: {}", prescription.getPrescriptionId(), e);
      throw new ServiceException("Failed to add prescription", e);
    }
  }

  // Retrieve all prescriptions
  public List<Prescription> getAllPrescriptions() {
    try {
      List<Prescription> prescriptions = prescriptionDAO.getAllPrescriptions();
      if (prescriptions == null) {
        return Collections.emptyList();
      }
      return Collections.unmodifiableList(prescriptions);
    } catch (SQLException e) {
      logger.error("Error fetching prescriptions", e);
      return Collections.emptyList();
    }
  }

  // Retrieve a prescription by ID
  public Prescription getPrescriptionById(String prescriptionId) {
    if (prescriptionId == null || prescriptionId.isEmpty()) {
      throw new IllegalArgumentException("Prescription ID cannot be empty");
    }
    try {
      return prescriptionDAO.getPrescriptionById(prescriptionId);
    } catch (SQLException e) {
      logger.error("Error fetching prescription: {}", prescriptionId, e);
      return null;
    }
  }

  // Update a prescription
  public void updatePrescription(Prescription prescription) {
    if (prescription == null) {
      throw new IllegalArgumentException("Prescription cannot be null");
    }
    try {
      prescriptionDAO.updatePrescription(prescription);
      logger.info("Prescription updated successfully: {}", prescription.getPrescriptionId());
    } catch (SQLException e) {
      logger.error("Error updating prescription: {}", prescription.getPrescriptionId(), e);
      throw new ServiceException("Failed to update prescription", e);
    }
  }

  // Delete a prescription
  public void deletePrescription(String prescriptionId) {
    if (prescriptionId == null || prescriptionId.isEmpty()) {
      throw new IllegalArgumentException("Prescription ID cannot be empty");
    }
    try {
      prescriptionDAO.deletePrescription(prescriptionId);
      logger.info("Prescription deleted successfully: {}", prescriptionId);
    } catch (SQLException e) {
      logger.error("Error deleting prescription: {}", prescriptionId, e);
      throw new ServiceException("Failed to delete prescription", e);
    }
  }
}
