package com.bougastefa.database;

import com.bougastefa.models.Prescription;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {
    // Insert a new prescription
    public void addPrescription(Prescription prescription) throws SQLException {
        String sql =
            "INSERT INTO Prescription (prescriptionID, dateprescribed, dosage, duration, comment,"
                + " drugID, doctorID, patientID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prescription.getPrescriptionId());
            stmt.setDate(2, Date.valueOf(prescription.getDateOfPrescribe()));
            stmt.setInt(3, prescription.getDosage());
            stmt.setInt(4, prescription.getDuration());
            stmt.setString(5, prescription.getComment());
            stmt.setString(6, prescription.getDrugId());
            stmt.setString(7, prescription.getDoctorId());
            stmt.setString(8, prescription.getPatientId());
            stmt.executeUpdate();
        }
    }

    // Retrieve all prescriptions
    public List<Prescription> getAllPrescriptions() throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription";
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Prescription prescription =
                    new Prescription(
                        rs.getString("prescriptionID"),
                        rs.getDate("dateprescribed").toLocalDate(),
                        rs.getInt("dosage"),
                        rs.getInt("duration"),
                        rs.getString("comment"),
                        rs.getString("drugID"),
                        rs.getString("doctorID"),
                        rs.getString("patientID")
                    );
                prescriptions.add(prescription);
            }
        }
        return prescriptions;
    }

    // Retrieve prescription by ID
    public Prescription getPrescriptionById(String prescriptionId) throws SQLException {
        String sql = "SELECT * FROM Prescription WHERE prescriptionID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prescriptionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Prescription(
                        rs.getString("prescriptionID"),
                        rs.getDate("dateprescribed").toLocalDate(),
                        rs.getInt("dosage"),
                        rs.getInt("duration"),
                        rs.getString("comment"),
                        rs.getString("drugID"), 
                        rs.getString("doctorID"),
                        rs.getString("patientID")
                    );
                }
            }
        }
        return null;
    }

    // Update a prescription
    public void updatePrescription(Prescription prescription) throws SQLException {
        String sql =
            "UPDATE Prescription SET dateprescribed = ?, dosage = ?, duration = ?, comment = ?, drugID"
                + " = ?, doctorID = ?, patientID = ? WHERE prescriptionID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(prescription.getDateOfPrescribe()));
            stmt.setInt(2, prescription.getDosage());
            stmt.setInt(3, prescription.getDuration());
            stmt.setString(4, prescription.getComment());
            stmt.setString(5, prescription.getDrugId());
            stmt.setString(6, prescription.getDoctorId());
            stmt.setString(7, prescription.getPatientId());
            stmt.setString(8, prescription.getPrescriptionId());
            stmt.executeUpdate();
        }
    }

    // Delete a prescription
    public void deletePrescription(String prescriptionId) throws SQLException {
        String sql = "DELETE FROM Prescription WHERE prescriptionID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prescriptionId);
            stmt.executeUpdate();
        }
    }
}
