package com.bougastefa.services;

import com.bougastefa.database.PatientDAO;
import com.bougastefa.models.Patient;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientService {
  private PatientDAO patientDAO = new PatientDAO();
  private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

  // Add a new patient
  public void addPatient(Patient patient) {
    if (patient == null) {
      throw new IllegalArgumentException("Patient cannot be null");
    }
    Patient existingPatient = getPatientById(patient.getPatientId());
    if(existingPatient != null){
      throw new IllegalArgumentException("Patient with ID " + patient.getPatientId() + " already exists");
    }
    try {
      patientDAO.addPatient(patient);
      logger.info("Patient added successfully: {}", patient.getPatientId());
    } catch (SQLException e) {
      logger.error("Error adding patient: {}", patient.getPatientId(), e);
      throw new ServiceException("Failed to add patient", e);
    }
  }

  // Retrieve all patients
  public List<Patient> getAllPatients() {
    try {
      return patientDAO.getAllPatients();
    } catch (SQLException e) {
      logger.error("Error fetching patients", e);
      return List.of(); // Return an empty list on error
    }
  }

  // Retrieve a patient by ID
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

  // Update a patient
  public void updatePatient(Patient patient) {
    if (patient == null) {
      throw new IllegalArgumentException("Patient cannot be null");
    }
    try {
      patientDAO.updatePatient(patient);
      logger.info("Patient updated successfully: {}", patient.getPatientId());
    } catch (SQLException e) {
      logger.error("Error updating patient: {}", patient.getPatientId(), e);
      throw new ServiceException("Failed to update patient", e);
    }
  }

  // Delete a patient
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
