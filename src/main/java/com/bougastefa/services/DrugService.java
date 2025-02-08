package com.bougastefa.services;

import com.bougastefa.database.DrugDAO;
import com.bougastefa.models.Drug;
import java.sql.SQLException;
import java.util.List;

public class DrugService {
    private DrugDAO drugDAO = new DrugDAO();

    // Add a new drug
    public void addDrug(Drug drug) {
        try {
            drugDAO.addDrug(drug);
            System.out.println("Drug added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding drug: " + e.getMessage());
        }
    }

    // Retrieve all drugs
    public List<Drug> getAllDrugs() {
        try {
            return drugDAO.getAllDrugs();
        } catch (SQLException e) {
            System.err.println("Error fetching drugs: " + e.getMessage());
            return List.of(); // Return an empty list on error
        }
    }

    // Retrieve a drug by ID
    public Drug getDrugById(String drugId) {
        try {
            return drugDAO.getDrugById(drugId);
        } catch (SQLException e) {
            System.err.println("Error fetching drug: " + e.getMessage());
            return null;
        }
    }

    // Update a drug
    public void updateDrug(Drug drug) {
        try {
            drugDAO.updateDrug(drug);
            System.out.println("Drug updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating drug: " + e.getMessage());
        }
    }

    // Delete a drug
    public void deleteDrug(String drugId) {
        try {
            drugDAO.deleteDrug(drugId);
            System.out.println("Drug deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting drug: " + e.getMessage());
        }
    }
}
