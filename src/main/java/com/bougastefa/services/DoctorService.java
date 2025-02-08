package com.bougastefa.services;

import com.bougastefa.database.DoctorDAO;
import com.bougastefa.models.Doctor;
import java.sql.SQLException;
import java.util.List;

public class DoctorService {
  private DoctorDAO doctorDAO = new DoctorDAO();

  // Add a new doctor
  public void addDoctor(Doctor doctor) {
    try {
      doctorDAO.addDoctor(doctor);
      System.out.println("Doctor added successfully!");
    } catch (SQLException e) {
      System.err.println("Error adding doctor: " + e.getMessage());
    }
  }

  // Retrieve all doctors
  public List<Doctor> getAllDoctors() {
    try {
      return doctorDAO.getAllDoctors();
    } catch (SQLException e) {
      System.err.println("Error fetching doctors: " + e.getMessage());
      return List.of(); // Return an empty list on error
    }
  }

  // Retrieve a doctor by ID
  public Doctor getDoctorById(String doctorId) {
    try {
      return doctorDAO.getDoctorById(doctorId);
    } catch (SQLException e) {
      System.err.println("Error fetching doctor: " + e.getMessage());
      return null;
    }
  }

  // Update a doctor
  public void updateDoctor(Doctor doctor) {
    try {
      doctorDAO.updateDoctor(doctor);
      System.out.println("Doctor updated successfully!");
    } catch (SQLException e) {
      System.err.println("Error updating doctor: " + e.getMessage());
    }
  }

  // Delete a doctor
  public void deleteDoctor(String doctorId) {
    try {
      doctorDAO.deleteDoctor(doctorId);
      System.out.println("Doctor deleted successfully!");
    } catch (SQLException e) {
      System.err.println("Error deleting doctor: " + e.getMessage());
    }
  }
}
