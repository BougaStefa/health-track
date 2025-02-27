package com.bougastefa.services;

import com.bougastefa.database.DoctorDAO;
import com.bougastefa.models.Doctor;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoctorService {
  private DoctorDAO doctorDAO = new DoctorDAO();
  private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

  // Add a new doctor
  public void addDoctor(Doctor doctor) {
    if (doctor == null) {
      throw new IllegalArgumentException("Doctor cannot be null");
    }
    try {
      doctorDAO.addDoctor(doctor);
      logger.info("Doctor added successfully: {}", doctor.getDoctorId());
    } catch (SQLException e) {
      logger.error("Error adding doctor: {}", doctor.getDoctorId(), e);
      throw new ServiceException("Failed to add doctor", e);
    }
  }

  // Retrieve all doctors
  public List<Doctor> getAllDoctors() {
    try {
      return doctorDAO.getAllDoctors();
    } catch (SQLException e) {
      logger.error("Error fetching doctors", e);
      return List.of(); // Return an empty list on error
    }
  }

  // Retrieve a doctor by ID
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

  // Update a doctor
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

  // Delete a doctor
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
