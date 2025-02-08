package com.bougastefa.services;

import com.bougastefa.database.PrescriptionDAO;
import com.bougastefa.models.Prescription;
import java.sql.SQLException;
import java.util.List;

public class PrescriptionService {
  private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

  // Add a new prescription
  public void addPrescription(Prescription prescription) {
    try {
      prescriptionDAO.addPrescription(prescription);
      System.out.println("Prescription added successfully!");
    } catch (SQLException e) {
      System.err.println("Error adding prescription: " + e.getMessage());
    }
  }

  // Retrieve all prescriptions
  public List<Prescription> getAllPrescriptions() {
    try {
      return prescriptionDAO.getAllPrescriptions();
    } catch (SQLException e) {
      System.err.println("Error fetching prescriptions: " + e.getMessage());
      return List.of(); // Return an empty list on error
    }
  }

  // Retrieve a prescription by ID
  public Prescription getPrescriptionById(String prescriptionId) {
    try {
      return prescriptionDAO.getPrescriptionById(prescriptionId);
    } catch (SQLException e) {
      System.err.println("Error fetching prescription: " + e.getMessage());
      return null;
    }
  }

  // Update a prescription
  public void updatePrescription(Prescription prescription) {
    try {
      prescriptionDAO.updatePrescription(prescription);
      System.out.println("Prescription updated successfully!");
    } catch (SQLException e) {
      System.err.println("Error updating prescription: " + e.getMessage());
    }
  }

  // Delete a prescription
  public void deletePrescription(String prescriptionId) {
    try {
      prescriptionDAO.deletePrescription(prescriptionId);
      System.out.println("Prescription deleted successfully!");
    } catch (SQLException e) {
      System.err.println("Error deleting prescription: " + e.getMessage());
    }
  }
}
