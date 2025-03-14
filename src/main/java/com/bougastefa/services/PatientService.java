package com.bougastefa.services;

import com.bougastefa.database.PatientDAO;
import com.bougastefa.models.InsuredPatient;
import com.bougastefa.models.Patient;
import com.bougastefa.utils.FieldLengthConstants;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages the business logic for Patient entities. This class acts as an
 * intermediary between the controller layer and the data access layer, providing validation, error
 * handling, and logging for all patient-related operations. It ensures data integrity and proper
 * business rule application before any database interaction.
 */
public class PatientService {
  private PatientDAO patientDAO = new PatientDAO();
  private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

  /**
   * Validates that the patient fields don't exceed database column length limits.
   *
   * @param prescription The patient to validate
   * @throws IllegalArgumentException If any field exceeds its maximum length
   */
  private void validateFieldLengths(Patient patient) {
    if (patient.getPatientId() != null
        && patient.getPatientId().length() > FieldLengthConstants.PATIENT_ID_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Patient ID exceeds maximum length of "
              + FieldLengthConstants.PATIENT_ID_MAX_LENGTH
              + " characters");
    }

    if (patient.getFirstName() != null
        && patient.getFirstName().length() > FieldLengthConstants.PATIENT_FIRSTNAME_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "First name exceeds maximum length of "
              + FieldLengthConstants.PATIENT_FIRSTNAME_MAX_LENGTH
              + " characters");
    }

    if (patient.getSurname() != null
        && patient.getSurname().length() > FieldLengthConstants.PATIENT_SURNAME_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Surname exceeds maximum length of "
              + FieldLengthConstants.PATIENT_SURNAME_MAX_LENGTH
              + " characters");
    }

    if (patient.getPostcode() != null
        && patient.getPostcode().length() > FieldLengthConstants.PATIENT_POSTCODE_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Postcode exceeds maximum length of "
              + FieldLengthConstants.PATIENT_POSTCODE_MAX_LENGTH
              + " characters");
    }

    if (patient.getAddress() != null
        && patient.getAddress().length() > FieldLengthConstants.PATIENT_ADDRESS_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Address exceeds maximum length of "
              + FieldLengthConstants.PATIENT_ADDRESS_MAX_LENGTH
              + " characters");
    }

    if (patient.getEmail() != null
        && patient.getEmail().length() > FieldLengthConstants.PATIENT_EMAIL_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Email exceeds maximum length of "
              + FieldLengthConstants.PATIENT_EMAIL_MAX_LENGTH
              + " characters");
    }

    if (patient.getPhone() != null
        && patient.getPhone().length() > FieldLengthConstants.PATIENT_PHONE_MAX_LENGTH) {
      throw new IllegalArgumentException(
          "Phone exceeds maximum length of "
              + FieldLengthConstants.PATIENT_PHONE_MAX_LENGTH
              + " characters");
    }

    if (patient instanceof InsuredPatient) {
      InsuredPatient insuredPatient = (InsuredPatient) patient;
      if (insuredPatient.getInsuranceId() != null
          && insuredPatient.getInsuranceId().length()
              > FieldLengthConstants.INSURANCE_ID_MAX_LENGTH) {
        throw new IllegalArgumentException(
            "Insurance ID exceeds maximum length of "
                + FieldLengthConstants.INSURANCE_ID_MAX_LENGTH
                + " characters");
      }
    }
  }

  /**
   * Adds a new patient to the system after performing validation checks. Verifies that the patient
   * object is not null and that a patient with the same ID doesn't already exist in the database.
   *
   * @param patient The Patient object to be added
   * @throws IllegalArgumentException If the patient is null or if a patient with the same ID
   *     already exists
   * @throws ServiceException If a database error occurs while adding the patient
   */
  public void addPatient(Patient patient) {
    if (patient == null) {
      throw new IllegalArgumentException("Patient cannot be null");
    }

    validateFieldLengths(patient);

    Patient existingPatient = getPatientById(patient.getPatientId());
    if (existingPatient != null) {
      throw new IllegalArgumentException(
          "Patient with ID " + patient.getPatientId() + " already exists");
    }
    try {
      patientDAO.addPatient(patient);
      logger.info("Patient added successfully: {}", patient.getPatientId());
    } catch (SQLException e) {
      logger.error("Error adding patient: {}", patient.getPatientId(), e);
      throw new ServiceException("Failed to add patient", e);
    }
  }

  /**
   * Retrieves all patients from the database. Returns an empty list instead of throwing exceptions
   * if a database error occurs, providing graceful degradation for UI components that depend on
   * this data.
   *
   * @return A List containing all patients, or an empty list if an error occurs
   */
  public List<Patient> getAllPatients() {
    try {
      return patientDAO.getAllPatients();
    } catch (SQLException e) {
      logger.error("Error fetching patients", e);
      return List.of(); // Return an empty list on error
    }
  }

  /**
   * Retrieves a specific patient by their ID. Validates that the provided ID is not null or empty
   * before querying the database.
   *
   * @param patientId The unique identifier of the patient to retrieve
   * @return The Patient object if found, or null if the patient doesn't exist or an error occurs
   * @throws IllegalArgumentException If the patientId is null or empty
   */
  public Patient getPatientById(String patientId) {
    if (patientId == null || patientId.isEmpty()) {
      throw new IllegalArgumentException("Patient ID cannot be empty");
    }
    try {
      return patientDAO.getPatientById(patientId);
    } catch (SQLException e) {
      logger.error("Error fetching patient: {}", patientId, e);
      return null;
    }
  }

  /**
   * Updates an existing patient's information in the database. Validates that the patient object is
   * not null before proceeding with the update. If the patient doesn't exist, the DAO will handle
   * the appropriate error.
   *
   * @param patient The Patient object containing updated information
   * @throws IllegalArgumentException If the patient is null
   * @throws ServiceException If a database error occurs while updating the patient
   */
  public void updatePatient(Patient patient) {
    if (patient == null) {
      throw new IllegalArgumentException("Patient cannot be null");
    }

    validateFieldLengths(patient);

    try {
      patientDAO.updatePatient(patient);
      logger.info("Patient updated successfully: {}", patient.getPatientId());
    } catch (SQLException e) {
      logger.error("Error updating patient: {}", patient.getPatientId(), e);
      throw new ServiceException("Failed to update patient", e);
    }
  }

  /**
   * Deletes a patient from the database by their ID. Validates that the provided ID is not null or
   * empty before attempting deletion. Note: This operation may fail if the patient is referenced by
   * other records (e.g., visits, prescriptions), which would violate referential integrity
   * constraints.
   *
   * @param patientId The unique identifier of the patient to delete
   * @throws IllegalArgumentException If the patientId is null or empty
   * @throws ServiceException If a database error occurs while deleting the patient
   */
  public void deletePatient(String patientId) {
    if (patientId == null || patientId.isEmpty()) {
      throw new IllegalArgumentException("Patient ID cannot be empty");
    }
    try {
      patientDAO.deletePatient(patientId);
      logger.info("Patient deleted successfully: {}", patientId);
    } catch (SQLException e) {
      logger.error("Error deleting patient: {}", patientId, e);
      throw new ServiceException("Failed to delete patient", e);
    }
  }
}
