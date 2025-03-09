package com.bougastefa.services;

import com.bougastefa.database.VisitDAO;
import com.bougastefa.models.Visit;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisitService {
  private VisitDAO visitDAO = new VisitDAO();
  private static final Logger logger = LoggerFactory.getLogger(VisitService.class);

  // Add a new visit
  public void addVisit(Visit visit) {
    if (visit == null) {
      throw new IllegalArgumentException("Visit cannot be null");
    }
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

  // Retrieve all visits
  public List<Visit> getAllVisits() {
    try {
      return visitDAO.getAllVisits();
    } catch (SQLException e) {
      logger.error("Error fetching visits", e);
      return List.of(); // Return an empty list on error
    }
  }

  // Retrieve a visit by patient ID, doctor ID, and date
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

  // Update a visit
  public void updateVisit(Visit visit) {
    if (visit == null) {
      throw new IllegalArgumentException("Visit cannot be null");
    }
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

  // Delete a visit
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

  // Find primary doctor
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
