package com.bougastefa.services;

import com.bougastefa.database.PatientDAO;
import com.bougastefa.models.Patient;
import java.sql.SQLException;
import java.util.List;

public class PatientService {
  private PatientDAO patientDAO = new PatientDAO();

  // Add a new patient
  public void addPatient(Patient patient) {
    try {
      patientDAO.addPatient(patient);
      System.out.println("Patient added successfully!");
    } catch (SQLException e) {
      System.err.println("Error adding patient: " + e.getMessage());
    }
  }

  // Retrieve all patients
  public List<Patient> getAllPatients() {
    try {
      return patientDAO.getAllPatients();
    } catch (SQLException e) {
      System.err.println("Error fetching patients: " + e.getMessage());
      return List.of(); // Return an empty list on error
    }
  }

  // Retrieve a patient by ID
  public Patient getPatientById(String patientId) {
    try {
      return patientDAO.getPatientById(patientId);
    } catch (SQLException e) {
      System.err.println("Error fetching patient: " + e.getMessage());
      return null;
    }
  }

  // Update a patient
  public void updatePatient(Patient patient) {
    try {
      patientDAO.updatePatient(patient);
      System.out.println("Patient updated successfully!");
    } catch (SQLException e) {
      System.err.println("Error updating patient: " + e.getMessage());
    }
  }

  // Delete a patient
  public void deletePatient(String patientId) {
    try {
      patientDAO.deletePatient(patientId);
      System.out.println("Patient deleted successfully!");
    } catch (SQLException e) {
      System.err.println("Error deleting patient: " + e.getMessage());
    }
  }
}
