package com.bougastefa.gui.panels;

import com.bougastefa.models.Doctor;
import com.bougastefa.services.DoctorService;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class DoctorPanel extends JPanel {

  private DoctorService doctorService;
  private DefaultListModel<Doctor> doctorListModel;

  public DoctorPanel() {
    doctorService = new DoctorService();
    doctorListModel = new DefaultListModel<>();

    setLayout(new BorderLayout());

    // List of doctors
    JList<Doctor> doctorList = new JList<>(doctorListModel);
    JScrollPane scrollPane = new JScrollPane(doctorList);
    add(scrollPane, BorderLayout.CENTER);

    // Form to add new doctor
    JPanel formPanel = new JPanel(new GridLayout(7, 2));
    JTextField idField = new JTextField();
    JTextField firstNameField = new JTextField();
    JTextField surnameField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField hospitalField = new JTextField();
    JButton addButton = new JButton("Add Doctor");

    formPanel.add(new JLabel("Doctor ID:"));
    formPanel.add(idField);
    formPanel.add(new JLabel("First Name:"));
    formPanel.add(firstNameField);
    formPanel.add(new JLabel("Surname:"));
    formPanel.add(surnameField);
    formPanel.add(new JLabel("Address:"));
    formPanel.add(addressField);
    formPanel.add(new JLabel("Email:"));
    formPanel.add(emailField);
    formPanel.add(new JLabel("Hospital:"));
    formPanel.add(hospitalField);
    formPanel.add(new JLabel());
    formPanel.add(addButton);

    add(formPanel, BorderLayout.SOUTH);

    addButton.addActionListener(
        e -> {
          String id = idField.getText();
          String firstName = firstNameField.getText();
          String surname = surnameField.getText();
          String address = addressField.getText();
          String email = emailField.getText();
          String hospital = hospitalField.getText();

          Doctor doctor = new Doctor(id, firstName, surname, address, email, hospital);
          try {
            doctorService.addDoctor(doctor);
            doctorListModel.addElement(doctor);
            JOptionPane.showMessageDialog(this, "Doctor added successfully!");
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error adding doctor: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    loadDoctors();
  }

  private void loadDoctors() {
    try {
      List<Doctor> doctors = doctorService.getAllDoctors();
      doctorListModel.clear();
      for (Doctor doctor : doctors) {
        doctorListModel.addElement(doctor);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading doctors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
