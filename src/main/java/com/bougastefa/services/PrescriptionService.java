package com.bougastefa.services;

import com.bougastefa.database.PrescriptionDAO;
import com.bougastefa.models.Prescription;
import com.bougastefa.utils.FieldLengthConstants;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages the business logic for Prescription entities. This class acts as an
 * intermediary between the controller layer and the data access layer, providing validation, error
 * handling, and logging for all prescription-related operations. It ensures data integrity and
 * consistent behavior when interacting with prescription records.
 */
public class PrescriptionService {
  private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
  private static final Logger logger = LoggerFactory.getLogger(PrescriptionService.class);

  /**
   * Validates that the prescription fields don't exceed database column length limits.
   *
   * @param prescription The prescription to validate
   * @throws IllegalArgumentException If any field exceeds its maximum length
   */
  private void validateFieldLengths(Prescription prescription) {
    if (prescription.getPrescriptionId() != null
        && prescription.getPrescriptionId().length()
            > FieldLengthConstants.PRESCRIPTION_ID_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Prescription ID exceeds maximum length of "
              + FieldLengthConstants.PRESCRIPTION_ID_MAX_LENGTH
              + " characters");
    }

    if (prescription.getDrugId() != null
        && prescription.getDrugId().length()
            > FieldLengthConstants.PRESCRIPTION_DRUG_ID_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Drug ID exceeds maximum length of "
              + FieldLengthConstants.PRESCRIPTION_DRUG_ID_MAX_LENGTH
              + " characters");
    }

    if (prescription.getDoctorId() != null
        && prescription.getDoctorId().length()
            > FieldLengthConstants.PRESCRIPTION_DOCTOR_ID_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Doctor ID exceeds maximum length of "
              + FieldLengthConstants.PRESCRIPTION_DOCTOR_ID_MAX_LENGTH
              + " characters");
    }

    if (prescription.getPatientId() != null
        && prescription.getPatientId().length()
            > FieldLengthConstants.PRESCRIPTION_PATIENT_ID_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Patient ID exceeds maximum length of "
              + FieldLengthConstants.PRESCRIPTION_PATIENT_ID_MAX_LENGTH
              + " characters");
    }

    if (prescription.getComment() != null
        && prescription.getComment().length()
            > FieldLengthConstants.PRESCRIPTION_COMMENT_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Comment exceeds maximum length of "
              + FieldLengthConstants.PRESCRIPTION_COMMENT_MAX_LENGTH
              + " characters");
    }
  }

  /**
   * Adds a new prescription to the system after performing validation checks. Validates that the
   * prescription object is not null and that a prescription with the same ID doesn't already exist
   * in the database.
   *
   * @param prescription The Prescription object to be added
   * @throws IllegalArgumentException If the prescription is null or if a prescription with the same
   *     ID already exists
   * @throws ServiceException If a database error occurs while adding the prescription
   */
  public void addPrescription(Prescription prescription) {
    if (prescription == null) {
      throw new IllegalArgumentException("Prescription cannot be null");
    }

    validateFieldLengths(prescription);

    Prescription existingPrescription = getPrescriptionById(prescription.getPrescriptionId());
    if (existingPrescription != null) {
      throw new IllegalArgumentException(
          "Prescription ID already exists: " + prescription.getPrescriptionId());
    }
    try {
      prescriptionDAO.addPrescription(prescription);
      logger.info("Prescription added successfully: {}", prescription.getPrescriptionId());
    } catch (SQLException e) {
      logger.error("Error adding prescription: {}", prescription.getPrescriptionId(), e);
      throw new ServiceException("Failed to add prescription", e);
    }
  }

  /**
   * Retrieves all prescriptions from the database. Returns an unmodifiable list to prevent clients
   * from modifying the returned data, ensuring data integrity. Returns an empty list if an error
   * occurs or if no prescriptions exist.
   *
   * @return An unmodifiable List containing all prescriptions, or an empty list if none found or an
   *     error occurs
   */
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

  /**
   * Retrieves a specific prescription by its ID. Validates that the provided ID is not null or
   * empty before querying the database.
   *
   * @param prescriptionId The unique identifier of the prescription to retrieve
   * @return The Prescription object if found, or null if the prescription doesn't exist or an error
   *     occurs
   * @throws IllegalArgumentException If the prescriptionId is null or empty
   */
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

  /**
   * Updates an existing prescription's information in the database. Validates that the prescription
   * object is not null before proceeding with the update. This method assumes the prescription
   * already exists in the database.
   *
   * @param prescription The Prescription object containing updated information
   * @throws IllegalArgumentException If the prescription is null
   * @throws ServiceException If a database error occurs while updating the prescription
   */
  public void updatePrescription(Prescription prescription) {
    if (prescription == null) {
      throw new IllegalArgumentException("Prescription cannot be null");
    }

    validateFieldLengths(prescription);

    try {
      prescriptionDAO.updatePrescription(prescription);
      logger.info("Prescription updated successfully: {}", prescription.getPrescriptionId());
    } catch (SQLException e) {
      logger.error("Error updating prescription: {}", prescription.getPrescriptionId(), e);
      throw new ServiceException("Failed to update prescription", e);
    }
  }

  /**
   * Deletes a prescription from the database by its ID. Validates that the provided ID is not null
   * or empty before attempting deletion. This operation permanently removes the prescription record
   * from the system.
   *
   * @param prescriptionId The unique identifier of the prescription to delete
   * @throws IllegalArgumentException If the prescriptionId is null or empty
   * @throws ServiceException If a database error occurs while deleting the prescription
   */
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
