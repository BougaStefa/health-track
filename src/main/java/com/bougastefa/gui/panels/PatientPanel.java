package com.bougastefa.gui.panels;

import com.bougastefa.models.Patient;
import com.bougastefa.services.PatientService;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class PatientPanel extends JPanel {

  private PatientService patientService;
  private DefaultListModel<Patient> patientListModel;

  public PatientPanel() {
    patientService = new PatientService();
    patientListModel = new DefaultListModel<>();

    setLayout(new BorderLayout());

    // List of patients
    JList<Patient> patientList = new JList<>(patientListModel);
    JScrollPane scrollPane = new JScrollPane(patientList);
    add(scrollPane, BorderLayout.CENTER);

    // Form to add new patient
    JPanel formPanel = new JPanel(new GridLayout(8, 2));
    JTextField idField = new JTextField();
    JTextField firstNameField = new JTextField();
    JTextField surnameField = new JTextField();
    JTextField postcodeField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField emailField = new JTextField();
    JButton addButton = new JButton("Add Patient");

    formPanel.add(new JLabel("Patient ID:"));
    formPanel.add(idField);
    formPanel.add(new JLabel("First Name:"));
    formPanel.add(firstNameField);
    formPanel.add(new JLabel("Surname:"));
    formPanel.add(surnameField);
    formPanel.add(new JLabel("Postcode:"));
    formPanel.add(postcodeField);
    formPanel.add(new JLabel("Address:"));
    formPanel.add(addressField);
    formPanel.add(new JLabel("Phone:"));
    formPanel.add(phoneField);
    formPanel.add(new JLabel("Email:"));
    formPanel.add(emailField);
    formPanel.add(new JLabel());
    formPanel.add(addButton);

    add(formPanel, BorderLayout.SOUTH);

    addButton.addActionListener(
        e -> {
          String id = idField.getText();
          String firstName = firstNameField.getText();
          String surname = surnameField.getText();
          String postcode = postcodeField.getText();
          String address = addressField.getText();
          String phone = phoneField.getText();
          String email = emailField.getText();

          Patient patient = new Patient(id, firstName, surname, postcode, address, phone, email);
          try {
            patientService.addPatient(patient);
            patientListModel.addElement(patient);
            JOptionPane.showMessageDialog(this, "Patient added successfully!");
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error adding patient: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    loadPatients();
  }

  private void loadPatients() {
    try {
      List<Patient> patients = patientService.getAllPatients();
      patientListModel.clear();
      for (Patient patient : patients) {
        patientListModel.addElement(patient);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading patients: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
