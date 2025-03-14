package com.bougastefa.services;

import com.bougastefa.database.VisitDAO;
import com.bougastefa.models.Visit;
import com.bougastefa.utils.FieldLengthConstants;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages the business logic for Visit entities.
 * This class serves as an intermediary between the controller layer and the data access layer,
 * handling validation, exception management, and logging for all visit-related operations.
 * Visit records represent patient-doctor consultations and are identified by a composite key
 * consisting of patientId, doctorId, and dateOfVisit.
 */
public class VisitService {
  private VisitDAO visitDAO = new VisitDAO();
  private static final Logger logger = LoggerFactory.getLogger(VisitService.class);
/**
 * Validates that the visit fields don't exceed database column length limits.
 *
 * @param visit The visit to validate
 * @throws IllegalArgumentException If any field exceeds its maximum length
 */
private void validateFieldLengths(Visit visit) {
    if (visit.getDoctorId() != null && 
        visit.getDoctorId().length() > FieldLengthConstants.VISIT_DOCTOR_ID_MAX_LENGTH) {
        throw new IllegalArgumentException(
            "Doctor ID exceeds maximum length of " + 
            FieldLengthConstants.VISIT_DOCTOR_ID_MAX_LENGTH + " characters");
    }
    
    if (visit.getPatientId() != null && 
        visit.getPatientId().length() > FieldLengthConstants.VISIT_PATIENT_ID_MAX_LENGTH) {
        throw new IllegalArgumentException(
            "Patient ID exceeds maximum length of " + 
            FieldLengthConstants.VISIT_PATIENT_ID_MAX_LENGTH + " characters");
    }
    
    if (visit.getDiagnosis() != null && 
        visit.getDiagnosis().length() > FieldLengthConstants.VISIT_DIAGNOSIS_MAX_LENGTH) {
        throw new IllegalArgumentException(
            "Diagnosis exceeds maximum length of " + 
            FieldLengthConstants.VISIT_DIAGNOSIS_MAX_LENGTH + " characters");
    }
    
    if (visit.getSymptoms() != null && 
        visit.getSymptoms().length() > FieldLengthConstants.VISIT_SYMPTOMS_MAX_LENGTH) {
        throw new IllegalArgumentException(
            "Symptoms exceeds maximum length of " + 
            FieldLengthConstants.VISIT_SYMPTOMS_MAX_LENGTH + " characters");
    }
}

  /**
   * Adds a new visit to the system after performing validation checks.
   * Verifies that the visit object is not null and that a visit with the same
   * composite key (patientId, doctorId, dateOfVisit) doesn't already exist.
   *
   * @param visit The Visit object to be added
   * @throws IllegalArgumentException If the visit is null or if a visit with the same composite key already exists
   * @throws ServiceException If a database error occurs while adding the visit
   */
  public void addVisit(Visit visit) {
    if (visit == null) {
      throw new IllegalArgumentException("Visit cannot be null");
    }

    validateFieldLengths(visit);

    Visit existingVisit = getVisit(visit.getPatientId(), visit.getDoctorId(), visit.getDateOfVisit());
    if (existingVisit != null) {
      throw new IllegalArgumentException(
          "Visit already exists for patient: "
              + visit.getPatientId()
              + ", doctor: "
              + visit.getDoctorId()
              + ", date: "
              + visit.getDateOfVisit());
    }
    try {
      visitDAO.addVisit(visit);
      logger.info(
          "Visit added successfully for patient: {}, doctor: {}, date: {}",
          visit.getPatientId(),
          visit.getDoctorId(),
          visit.getDateOfVisit());
    } catch (SQLException e) {
      logger.error("Error adding visit", e);
      throw new ServiceException("Failed to add visit", e);
    }
  }

  /**
   * Retrieves all visits from the database.
   * Returns an empty list instead of throwing exceptions if a database error occurs,
   * providing graceful degradation for UI components that depend on this data.
   *
   * @return A List containing all visits, or an empty list if an error occurs
   */
  public List<Visit> getAllVisits() {
    try {
      return visitDAO.getAllVisits();
    } catch (SQLException e) {
      logger.error("Error fetching visits", e);
      return List.of(); // Return an empty list on error
    }
  }

  /**
   * Retrieves a specific visit by its composite key components.
   * Validates that all components of the composite key (patientId, doctorId, dateOfVisit)
   * are not null or empty before querying the database.
   *
   * @param patientId The ID of the patient involved in the visit
   * @param doctorId The ID of the doctor conducting the visit
   * @param dateOfVisit The date when the visit occurred
   * @return The Visit object if found, or null if no matching visit exists or an error occurs
   * @throws IllegalArgumentException If any of the key components are null or empty
   */
  public Visit getVisit(String patientId, String doctorId, LocalDate dateOfVisit) {
    if (patientId == null || patientId.isEmpty()) {
      throw new IllegalArgumentException("Patient ID cannot be empty");
    }
    if (doctorId == null || doctorId.isEmpty()) {
      throw new IllegalArgumentException("Doctor ID cannot be empty");
    }
    if (dateOfVisit == null) {
      throw new IllegalArgumentException("Visit date cannot be null");
    }

    try {
      return visitDAO.getVisit(patientId, doctorId, dateOfVisit);
    } catch (SQLException e) {
      logger.error(
          "Error fetching visit for patient: {}, doctor: {}, date: {}",
          patientId,
          doctorId,
          dateOfVisit,
          e);
      return null;
    }
  }

  /**
   * Updates an existing visit's information in the database.
   * Validates that the visit object is not null before proceeding with the update.
   * The composite key (patientId, doctorId, dateOfVisit) is used to identify which 
   * visit to update, typically allowing changes to symptoms and diagnosis.
   *
   * @param visit The Visit object containing updated information
   * @throws IllegalArgumentException If the visit is null
   * @throws ServiceException If a database error occurs while updating the visit
   */
  public void updateVisit(Visit visit) {
    if (visit == null) {
      throw new IllegalArgumentException("Visit cannot be null");
    }

    validateFieldLengths(visit);

    try {
      visitDAO.updateVisit(visit);
      logger.info(
          "Visit updated successfully for patient: {}, doctor: {}, date: {}",
          visit.getPatientId(),
          visit.getDoctorId(),
          visit.getDateOfVisit());
    } catch (SQLException e) {
      logger.error("Error updating visit", e);
      throw new ServiceException("Failed to update visit", e);
    }
  }

  /**
   * Deletes a visit from the database using its composite key components.
   * Validates that all components of the composite key (patientId, doctorId, dateOfVisit)
   * are not null or empty before attempting deletion.
   *
   * @param patientId The ID of the patient involved in the visit
   * @param doctorId The ID of the doctor conducting the visit
   * @param dateOfVisit The date when the visit occurred
   * @throws IllegalArgumentException If any of the key components are null or empty
   * @throws ServiceException If a database error occurs while deleting the visit
   */
  public void deleteVisit(String patientId, String doctorId, LocalDate dateOfVisit) {
    if (patientId == null || patientId.isEmpty()) {
      throw new IllegalArgumentException("Patient ID cannot be empty");
    }
    if (doctorId == null || doctorId.isEmpty()) {
      throw new IllegalArgumentException("Doctor ID cannot be empty");
    }
    if (dateOfVisit == null) {
      throw new IllegalArgumentException("Visit date cannot be null");
    }

    try {
      visitDAO.deleteVisit(patientId, doctorId, dateOfVisit);
      logger.info(
          "Visit deleted successfully for patient: {}, doctor: {}, date: {}",
          patientId,
          doctorId,
          dateOfVisit);
    } catch (SQLException e) {
      logger.error(
          "Error deleting visit for patient: {}, doctor: {}, date: {}",
          patientId,
          doctorId,
          dateOfVisit,
          e);
      throw new ServiceException("Failed to delete visit", e);
    }
  }

  /**
   * Determines which doctor has seen a particular patient most frequently,
   * making them the patient's "primary doctor".
   * Validates that the patient ID is not null or empty before querying the database.
   *
   * @param patientId The ID of the patient to find the primary doctor for
   * @return The ID of the doctor who has conducted the most visits with this patient,
   *         or null if the patient has no recorded visits or an error occurs
   * @throws IllegalArgumentException If the patientId is null or empty
   */
  public String getPrimaryDoctorId(String patientId) {
    if (patientId == null || patientId.isEmpty()) {
      throw new IllegalArgumentException("Patient ID cannot be empty");
    }
    try {
      return visitDAO.getPrimaryDoctorId(patientId);
    } catch (SQLException e) {
      logger.error("Error fetching primary doctor for patient: {}", patientId, e);
      return null;
    }
  }
}
