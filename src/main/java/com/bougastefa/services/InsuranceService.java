package com.bougastefa.services;

import com.bougastefa.database.InsuranceDAO;
import com.bougastefa.models.Insurance;
import java.sql.SQLException;
import java.util.List;

public class InsuranceService {
    private InsuranceDAO insuranceDAO = new InsuranceDAO();

    // Add a new insurance
    public void addInsurance(Insurance insurance) {
        try {
            insuranceDAO.addInsurance(insurance);
            System.out.println("Insurance added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding insurance: " + e.getMessage());
        }
    }

    // Retrieve all insurances
    public List<Insurance> getAllInsurances() {
        try {
            return insuranceDAO.getAllInsurances();
        } catch (SQLException e) {
            System.err.println("Error fetching insurances: " + e.getMessage());
            return List.of(); // Return an empty list on error
        }
    }

    // Retrieve an insurance by ID
    public Insurance getInsuranceById(String insuranceId) {
        try {
            return insuranceDAO.getInsuranceById(insuranceId);
        } catch (SQLException e) {
            System.err.println("Error fetching insurance: " + e.getMessage());
            return null;
        }
    }

    // Update an insurance
    public void updateInsurance(Insurance insurance) {
        try {
            insuranceDAO.updateInsurance(insurance);
            System.out.println("Insurance updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating insurance: " + e.getMessage());
        }
    }

    // Delete an insurance
    public void deleteInsurance(String insuranceId) {
        try {
            insuranceDAO.deleteInsurance(insuranceId);
            System.out.println("Insurance deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting insurance: " + e.getMessage());
        }
    }
}
