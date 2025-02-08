package com.bougastefa.services;

import com.bougastefa.database.VisitDAO;
import com.bougastefa.models.Visit;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class VisitService {
    private VisitDAO visitDAO = new VisitDAO();

    // Add a new visit
    public void addVisit(Visit visit) {
        try {
            visitDAO.addVisit(visit);
            System.out.println("Visit added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding visit: " + e.getMessage());
        }
    }

    // Retrieve all visits
    public List<Visit> getAllVisits() {
        try {
            return visitDAO.getAllVisits();
        } catch (SQLException e) {
            System.err.println("Error fetching visits: " + e.getMessage());
            return List.of(); // Return an empty list on error
        }
    }

    // Retrieve a visit by patient ID, doctor ID, and date
    public Visit getVisit(String patientId, String doctorId, LocalDate dateOfVisit) {
        try {
            return visitDAO.getVisit(patientId, doctorId, dateOfVisit);
        } catch (SQLException e) {
            System.err.println("Error fetching visit: " + e.getMessage());
            return null;
        }
    }

    // Update a visit
    public void updateVisit(Visit visit) {
        try {
            visitDAO.updateVisit(visit);
            System.out.println("Visit updated successfully!");
        } catch (SQLException e) {
            System.err.println("Error updating visit: " + e.getMessage());
        }
    }

    // Delete a visit
    public void deleteVisit(String patientId, String doctorId, LocalDate dateOfVisit) {
        try {
            visitDAO.deleteVisit(patientId, doctorId, dateOfVisit);
            System.out.println("Visit deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting visit: " + e.getMessage());
        }
    }
}
