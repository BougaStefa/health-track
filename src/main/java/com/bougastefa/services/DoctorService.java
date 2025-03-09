package com.bougastefa.services;

import com.bougastefa.database.DoctorDAO;
import com.bougastefa.models.Doctor;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that handles business logic for Doctor entities.
 * This class acts as an intermediary between the controller layer and the data access layer,
 * providing a higher-level API for doctor-related operations while handling exceptions,
 * validation, and logging.
 */
public class DoctorService {
  private DoctorDAO doctorDAO = new DoctorDAO();
  private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

  /**
   * Adds a new doctor to the system after performing validation checks.
   * Validates that the doctor object is not null and that the doctor ID doesn't already exist.
   *
   * @param doctor The Doctor object to be added
   * @throws IllegalArgumentException If the doctor is null or the doctor ID already exists
   * @throws ServiceException If a database error occurs while adding the doctor
   */
  public void addDoctor(Doctor doctor) {
    if (doctor == null) {
      throw new IllegalArgumentException("Doctor cannot be null");
    }
    Doctor existingDoctor = getDoctorById(doctor.getDoctorId());
    if (existingDoctor != null) {
      throw new IllegalArgumentException("Doctor ID already exists: " + doctor.getDoctorId());
    }
    try {
      doctorDAO.addDoctor(doctor);
      logger.info("Doctor added successfully: {}", doctor.getDoctorId());
    } catch (SQLException e) {
      logger.error("Error adding doctor: {}", doctor.getDoctorId(), e);
      throw new ServiceException("Failed to add doctor", e);
    }
  }

  /**
   * Retrieves all doctors from the database.
   * Returns an empty list instead of throwing exceptions if a database error occurs,
   * providing more robust behavior for UI components.
   *
   * @return A List containing all doctors, or an empty list if an error occurs
   */
  public List<Doctor> getAllDoctors() {
    try {
      return doctorDAO.getAllDoctors();
    } catch (SQLException e) {
      logger.error("Error fetching doctors", e);
      return List.of();
    }
  }

  /**
   * Retrieves a specific doctor by their ID.
   * Validates that the provided ID is not null or empty before querying the database.
   *
   * @param doctorId The unique identifier of the doctor to retrieve
   * @return The Doctor object if found, or null if the doctor doesn't exist or an error occurs
   * @throws IllegalArgumentException If the doctorId is null or empty
   */
  public Doctor getDoctorById(String doctorId) {
    if (doctorId == null || doctorId.isEmpty()) {
      throw new IllegalArgumentException("Doctor ID cannot be empty");
    }
    try {
      return doctorDAO.getDoctorById(doctorId);
    } catch (SQLException e) {
      logger.error("Error fetching doctor: {}", doctorId, e);
      return null;
    }
  }

  /**
   * Updates an existing doctor's information in the database.
   * Validates that the doctor object is not null before proceeding with the update.
   *
   * @param doctor The Doctor object containing updated information
   * @throws IllegalArgumentException If the doctor is null
   * @throws ServiceException If a database error occurs while updating the doctor
   */
  public void updateDoctor(Doctor doctor) {
    if (doctor == null) {
      throw new IllegalArgumentException("Doctor cannot be null");
    }
    try {
      doctorDAO.updateDoctor(doctor);
      logger.info("Doctor updated successfully: {}", doctor.getDoctorId());
    } catch (SQLException e) {
      logger.error("Error updating doctor: {}", doctor.getDoctorId(), e);
      throw new ServiceException("Failed to update doctor", e);
    }
  }

  /**
   * Deletes a doctor from the database by their ID.
   * Validates that the provided ID is not null or empty before attempting deletion.
   * Note: This operation may fail if the doctor is referenced by other records
   * in the database (e.g., visits, prescriptions).
   *
   * @param doctorId The unique identifier of the doctor to delete
   * @throws IllegalArgumentException If the doctorId is null or empty
   * @throws ServiceException If a database error occurs while deleting the doctor
   */
  public void deleteDoctor(String doctorId) {
    if (doctorId == null || doctorId.isEmpty()) {
      throw new IllegalArgumentException("Doctor ID cannot be empty");
    }
    try {
      doctorDAO.deleteDoctor(doctorId);
      logger.info("Doctor deleted successfully: {}", doctorId);
    } catch (SQLException e) {
      logger.error("Error deleting doctor: {}", doctorId, e);
      throw new ServiceException("Failed to delete doctor", e);
    }
  }
}
