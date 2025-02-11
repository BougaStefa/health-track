package com.bougastefa.gui.panels;

import com.bougastefa.models.Patient;
import com.bougastefa.services.PatientService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PatientPanel extends JPanel {

  private PatientService patientService;
  private DefaultTableModel tableModel;
  private JTable patientTable;

  public PatientPanel() {
    patientService = new PatientService();
    setLayout(new BorderLayout());

    // Create table with columns
    String[] columnNames = {"ID", "First Name", "Surname", "Postcode", "Address", "Phone", "Email"};
    tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };

    patientTable = new JTable(tableModel);
    patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    patientTable.getTableHeader().setReorderingAllowed(false);

    // Set column widths
    patientTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    patientTable.getColumnModel().getColumn(1).setPreferredWidth(100);
    patientTable.getColumnModel().getColumn(2).setPreferredWidth(100);
    patientTable.getColumnModel().getColumn(3).setPreferredWidth(80);
    patientTable.getColumnModel().getColumn(4).setPreferredWidth(150);
    patientTable.getColumnModel().getColumn(5).setPreferredWidth(100);
    patientTable.getColumnModel().getColumn(6).setPreferredWidth(150);

    // Add table to scroll pane
    JScrollPane scrollPane = new JScrollPane(patientTable);
    add(scrollPane, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel();
    JButton addButton = new JButton("Add Patient");
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    JButton refreshButton = new JButton("Refresh");

    buttonPanel.add(addButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(refreshButton);

    add(buttonPanel, BorderLayout.NORTH);

    // Add listeners
    addButton.addActionListener(e -> showPatientDialog(null));
    editButton.addActionListener(
        e -> {
          int row = patientTable.getSelectedRow();
          if (row != -1) {
            showPatientDialog(getSelectedPatient());
          } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit");
          }
        });
    deleteButton.addActionListener(e -> deleteSelectedPatient());
    refreshButton.addActionListener(e -> loadPatients());

    // Initial load
    loadPatients();
  }

  private void showPatientDialog(Patient existingPatient) {
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingPatient == null ? "Add Patient" : "Edit Patient",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Create form panel
    JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField idField = new JTextField(20);
    JTextField firstNameField = new JTextField(20);
    JTextField surnameField = new JTextField(20);
    JTextField postcodeField = new JTextField(20);
    JTextField addressField = new JTextField(20);
    JTextField phoneField = new JTextField(20);
    JTextField emailField = new JTextField(20);

    // If editing, populate fields
    if (existingPatient != null) {
      idField.setText(existingPatient.getPatientId());
      idField.setEditable(false);
      firstNameField.setText(existingPatient.getFirstName());
      surnameField.setText(existingPatient.getSurname());
      postcodeField.setText(existingPatient.getPostcode());
      addressField.setText(existingPatient.getAddress());
      phoneField.setText(existingPatient.getPhone());
      emailField.setText(existingPatient.getEmail());
    }

    formPanel.add(new JLabel("ID:"));
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

    // Button panel
    JPanel buttonPanel = new JPanel();
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

    saveButton.addActionListener(
        e -> {
          try {
            Patient patient =
                new Patient(
                    idField.getText(),
                    firstNameField.getText(),
                    surnameField.getText(),
                    postcodeField.getText(),
                    addressField.getText(),
                    phoneField.getText(),
                    emailField.getText());

            if (existingPatient == null) {
              patientService.addPatient(patient);
            } else {
              patientService.updatePatient(patient);
            }

            loadPatients();
            dialog.dispose();
            JOptionPane.showMessageDialog(
                this,
                existingPatient == null
                    ? "Patient added successfully"
                    : "Patient updated successfully");
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private Patient getSelectedPatient() {
    int row = patientTable.getSelectedRow();
    if (row != -1) {
      return new Patient(
          (String) tableModel.getValueAt(row, 0),
          (String) tableModel.getValueAt(row, 1),
          (String) tableModel.getValueAt(row, 2),
          (String) tableModel.getValueAt(row, 3),
          (String) tableModel.getValueAt(row, 4),
          (String) tableModel.getValueAt(row, 5),
          (String) tableModel.getValueAt(row, 6));
    }
    return null;
  }

  private void deleteSelectedPatient() {
    int row = patientTable.getSelectedRow();
    if (row != -1) {
      String id = (String) tableModel.getValueAt(row, 0);
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this patient?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);

      if (result == JOptionPane.YES_OPTION) {
        try {
          patientService.deletePatient(id);
          loadPatients();
          JOptionPane.showMessageDialog(this, "Patient deleted successfully");
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
              this,
              "Error deleting patient: " + e.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a patient to delete");
    }
  }

  private void loadPatients() {
    try {
      List<Patient> patients = patientService.getAllPatients();
      tableModel.setRowCount(0);
      for (Patient patient : patients) {
        tableModel.addRow(
            new Object[] {
              patient.getPatientId(),
              patient.getFirstName(),
              patient.getSurname(),
              patient.getPostcode(),
              patient.getAddress(),
              patient.getPhone(),
              patient.getEmail()
            });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          this, "Error loading patients: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
