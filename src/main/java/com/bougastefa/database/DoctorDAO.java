package com.bougastefa.database;

import com.bougastefa.models.Doctor;
import com.bougastefa.models.Specialist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for Doctor entities.
 * This class provides methods for CRUD (Create, Read, Update, Delete) operations
 * on Doctor records in the database, handling both regular doctors and specialists.
 */
public class DoctorDAO {
  /**
   * Inserts a new doctor record into the database.
   * Handles both regular doctors and specialists by using different SQL statements
   * based on the doctor type.
   *
   * @param doctor The doctor object to be added to the database
   * @throws SQLException If a database access error occurs
   */
  public void addDoctor(Doctor doctor) throws SQLException {
    String sql;
    // Check if the doctor is a specialist, create the appropriate SQL query
    if (doctor instanceof Specialist) {
      sql =
          "INSERT INTO Doctor (doctorID, firstname, surname, address, email, hospital,"
              + " specialization) VALUES (?, ?, ?, ?, ?, ?, ?)";
    } else {
      sql =
          "INSERT INTO Doctor (doctorID, firstname, surname, address, email, hospital) VALUES (?,"
              + " ?, ?, ?, ?, ?)";
    }

    // Connection and statement are automatically closed after the try block
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, doctor.getDoctorId());
      stmt.setString(2, doctor.getFirstName());
      stmt.setString(3, doctor.getSurname());
      stmt.setString(4, doctor.getAddress());
      stmt.setString(5, doctor.getEmail());
      stmt.setString(6, doctor.getHospital());

      // If the doctor is a specialist, set the specialization
      if (doctor instanceof Specialist) {
        Specialist specialist = (Specialist) doctor;
        stmt.setString(7, specialist.getSpecialization());
      }

      stmt.executeUpdate();
    }
  }

  /**
   * Retrieves all doctor records from the database.
   * Creates appropriate Doctor or Specialist objects based on whether
   * the specialization field is set.
   *
   * @return A list containing all doctors in the database
   * @throws SQLException If a database access error occurs
   */
  public List<Doctor> getAllDoctors() throws SQLException {
    List<Doctor> doctors = new ArrayList<>();
    String sql = "SELECT * FROM Doctor";
    // Connection, statement and result set are automatically closed after the try block
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      // Iterate over the result set and create a doctor object for each row
      while (rs.next()) {
        String doctorId = rs.getString("doctorID");
        String firstname = rs.getString("firstname");
        String surname = rs.getString("surname");
        String address = rs.getString("address");
        String email = rs.getString("email");
        String hospital = rs.getString("hospital");
        String specialization = rs.getString("specialization");

        Doctor doctor;
        // Check if the doctor is a specialist and create the appropriate object
        if (specialization != null) {
          doctor =
              new Specialist(
                  doctorId, firstname, surname, address, email, hospital, specialization);
        } else {
          doctor = new Doctor(doctorId, firstname, surname, address, email, hospital);
        }
        doctors.add(doctor);
      }
    }
    return doctors;
  }

  /**
   * Retrieves a doctor record from the database by its ID.
   * Creates either a Doctor or Specialist object based on the database record.
   *
   * @param doctorId The unique identifier of the doctor to retrieve
   * @return The doctor object if found, null otherwise
   * @throws SQLException If a database access error occurs
   */
  public Doctor getDoctorById(String doctorId) throws SQLException {
    String sql = "SELECT * FROM Doctor WHERE doctorID = ?";
    // Connection, statement and result set are automatically closed after the try block
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, doctorId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          String specialization = rs.getString("specialization");
          // Check if the doctor is a specialist and create the appropriate object
          if (specialization != null) {
            return new Specialist(
                rs.getString("doctorID"),
                rs.getString("firstname"),
                rs.getString("surname"),
                rs.getString("address"),
                rs.getString("email"),
                rs.getString("hospital"),
                specialization);
          } else {
            return new Doctor(
                rs.getString("doctorID"),
                rs.getString("firstname"),
                rs.getString("surname"),
                rs.getString("address"),
                rs.getString("email"),
                rs.getString("hospital"));
          }
        }
      }
    }
    return null;
  }

  /**
   * Updates an existing doctor record in the database.
   * Uses different SQL statements for regular doctors and specialists.
   *
   * @param doctor The doctor object with updated information
   * @throws SQLException If a database access error occurs
   */
  public void updateDoctor(Doctor doctor) throws SQLException {
    String sql;
    // Check if the doctor is a specialist, create the appropriate SQL query
    if (doctor instanceof Specialist) {
      sql =
          "UPDATE Doctor SET firstname = ?, surname = ?, address = ?, email = ?, hospital = ?,"
              + " specialization = ? WHERE doctorID = ?";
    } else {
      sql =
          "UPDATE Doctor SET firstname = ?, surname = ?, address = ?, email = ?, hospital = ? WHERE"
              + " doctorID = ?";
    }

    // Connection and statement are automatically closed after the try block
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, doctor.getFirstName());
      stmt.setString(2, doctor.getSurname());
      stmt.setString(3, doctor.getAddress());
      stmt.setString(4, doctor.getEmail());
      stmt.setString(5, doctor.getHospital());

      // If the doctor is a specialist, set the specialization
      if (doctor instanceof Specialist) {
        Specialist specialist = (Specialist) doctor;
        stmt.setString(6, specialist.getSpecialization());
        stmt.setString(7, doctor.getDoctorId());
      } else {
        stmt.setString(6, doctor.getDoctorId());
      }

      stmt.executeUpdate();
    }
  }

  /**
   * Deletes a doctor record from the database based on its ID.
   *
   * @param doctorId The unique identifier of the doctor to delete
   * @throws SQLException If a database access error occurs
   */
  public void deleteDoctor(String doctorId) throws SQLException {
    String sql = "DELETE FROM Doctor WHERE doctorID = ?";
    // Connection and statement are automatically closed after the try block
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, doctorId);
      stmt.executeUpdate();
    }
  }
}
