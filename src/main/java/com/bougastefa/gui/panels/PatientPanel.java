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

    // Top panel with three sections
    JPanel topPanel = new JPanel(new BorderLayout());

    // Left section: Advanced Filter and Clear Filters buttons
    JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton advancedFilterButton = new JButton("Advanced Filter");
    JButton clearFiltersButton = new JButton("Clear Filters");
    leftButtonPanel.add(advancedFilterButton);
    leftButtonPanel.add(clearFiltersButton);

    // Center section: Add, Edit and Delete buttons
    JPanel centerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton addButton = new JButton("Add Patient");
    JButton editButton = new JButton("Edit Patient");
    JButton deleteButton = new JButton("Delete Patient");
    centerButtonPanel.add(addButton);
    centerButtonPanel.add(editButton);
    centerButtonPanel.add(deleteButton);

    // Right section: Refresh button
    JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton refreshButton = new JButton("Refresh");
    rightButtonPanel.add(refreshButton);

    topPanel.add(leftButtonPanel, BorderLayout.WEST);
    topPanel.add(centerButtonPanel, BorderLayout.CENTER);
    topPanel.add(rightButtonPanel, BorderLayout.EAST);
    add(topPanel, BorderLayout.NORTH);

    // Setup table for Patients
    String[] columnNames = {
      "Patient ID", "First Name", "Surname", "Postcode", "Address", "Email", "Phone"
    };
    tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };

    patientTable = new JTable(tableModel);
    patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(patientTable);
    add(scrollPane, BorderLayout.CENTER);

    // Listeners for top buttons
    refreshButton.addActionListener(e -> loadPatients());
    advancedFilterButton.addActionListener(e -> showAdvancedFilterDialog());
    clearFiltersButton.addActionListener(e -> loadPatients());

    addButton.addActionListener(e -> showPatientDialog(null));
    editButton.addActionListener(
        e -> {
          Patient selectedPatient = getSelectedPatient();
          if (selectedPatient != null) {
            showPatientDialog(selectedPatient);
          } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit");
          }
        });
    deleteButton.addActionListener(e -> deleteSelectedPatient());

    // Initial load of patients
    loadPatients();
  }

  private void loadPatients() {
    try {
      List<Patient> patients = patientService.getAllPatients();
      populateTable(patients);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading patients: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void populateTable(List<Patient> patients) {
    tableModel.setRowCount(0);
    for (Patient patient : patients) {
      tableModel.addRow(
          new Object[] {
            patient.getPatientId(),
            patient.getFirstName(),
            patient.getSurname(),
            patient.getPostcode(),
            patient.getAddress(),
            patient.getEmail(),
            patient.getPhone()
          });
    }
  }

  private Patient getSelectedPatient() {
    int row = patientTable.getSelectedRow();
    if (row != -1) {
      String patientId = (String) tableModel.getValueAt(row, 0);
      String firstName = (String) tableModel.getValueAt(row, 1);
      String surname = (String) tableModel.getValueAt(row, 2);
      String postcode = (String) tableModel.getValueAt(row, 3);
      String address = (String) tableModel.getValueAt(row, 4);
      String email = (String) tableModel.getValueAt(row, 5);
      String phone = (String) tableModel.getValueAt(row, 6);

      return new Patient(patientId, firstName, surname, postcode, address, email, phone);
    }
    return null;
  }

  private void showPatientDialog(Patient existingPatient) {
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingPatient == null ? "Add Patient" : "Edit Patient",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering patient details
    JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField idField = new JTextField(20);
    JTextField firstNameField = new JTextField(20);
    JTextField surnameField = new JTextField(20);
    JTextField postcodeField = new JTextField(20);
    JTextField addressField = new JTextField(20);
    JTextField emailField = new JTextField(20);
    JTextField phoneField = new JTextField(20);

    if (existingPatient != null) {
      idField.setText(existingPatient.getPatientId());
      idField.setEditable(false);
      firstNameField.setText(existingPatient.getFirstName());
      surnameField.setText(existingPatient.getSurname());
      postcodeField.setText(existingPatient.getPostcode());
      addressField.setText(existingPatient.getAddress());
      emailField.setText(existingPatient.getEmail());
      phoneField.setText(existingPatient.getPhone());
    }

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
    formPanel.add(new JLabel("Email:"));
    formPanel.add(emailField);
    formPanel.add(new JLabel("Phone:"));
    formPanel.add(phoneField);

    // Button panel for save and cancel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            Patient patient =
                new Patient(
                    idField.getText().trim(),
                    firstNameField.getText().trim(),
                    surnameField.getText().trim(),
                    postcodeField.getText().trim(),
                    addressField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim());

            if (existingPatient == null) {
              patientService.addPatient(patient);
              JOptionPane.showMessageDialog(this, "Patient added successfully");
            } else {
              patientService.updatePatient(patient);
              JOptionPane.showMessageDialog(this, "Patient updated successfully");
            }
            loadPatients();
            dialog.dispose();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void deleteSelectedPatient() {
    int row = patientTable.getSelectedRow();
    if (row != -1) {
      String patientId = (String) tableModel.getValueAt(row, 0);
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this patient?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          patientService.deletePatient(patientId);
          loadPatients();
          JOptionPane.showMessageDialog(this, "Patient deleted successfully");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
              this,
              "Error deleting patient: " + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a patient to delete");
    }
  }

  private void showAdvancedFilterDialog() {
    JDialog filterDialog =
        new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter", true);
    filterDialog.setLayout(new BorderLayout(10, 10));

    JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField filterIdField = new JTextField(20);
    JTextField filterFirstNameField = new JTextField(20);
    JTextField filterSurnameField = new JTextField(20);
    JTextField filterPostcodeField = new JTextField(20);
    JTextField filterAddressField = new JTextField(20);
    JTextField filterEmailField = new JTextField(20);
    JTextField filterPhoneField = new JTextField(20);

    formPanel.add(new JLabel("Patient ID contains:"));
    formPanel.add(filterIdField);
    formPanel.add(new JLabel("First Name contains:"));
    formPanel.add(filterFirstNameField);
    formPanel.add(new JLabel("Surname contains:"));
    formPanel.add(filterSurnameField);
    formPanel.add(new JLabel("Postcode contains:"));
    formPanel.add(filterPostcodeField);
    formPanel.add(new JLabel("Address contains:"));
    formPanel.add(filterAddressField);
    formPanel.add(new JLabel("Email contains:"));
    formPanel.add(filterEmailField);
    formPanel.add(new JLabel("Phone contains:"));
    formPanel.add(filterPhoneField);

    JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton filterButton = new JButton("Filter");
    JButton cancelButton = new JButton("Cancel");
    filterButtonPanel.add(filterButton);
    filterButtonPanel.add(cancelButton);

    filterButton.addActionListener(
        e -> {
          try {
            List<Patient> patients = patientService.getAllPatients();

            String idFilter = filterIdField.getText().trim();
            String firstNameFilter = filterFirstNameField.getText().trim();
            String surnameFilter = filterSurnameField.getText().trim();
            String postcodeFilter = filterPostcodeField.getText().trim();
            String addressFilter = filterAddressField.getText().trim();
            String emailFilter = filterEmailField.getText().trim();
            String phoneFilter = filterPhoneField.getText().trim();

            patients =
                patients.stream()
                    .filter(
                        p ->
                            idFilter.isEmpty()
                                || p.getPatientId().toLowerCase().contains(idFilter.toLowerCase()))
                    .filter(
                        p ->
                            firstNameFilter.isEmpty()
                                || p.getFirstName()
                                    .toLowerCase()
                                    .contains(firstNameFilter.toLowerCase()))
                    .filter(
                        p ->
                            surnameFilter.isEmpty()
                                || p.getSurname()
                                    .toLowerCase()
                                    .contains(surnameFilter.toLowerCase()))
                    .filter(
                        p ->
                            postcodeFilter.isEmpty()
                                || p.getPostcode()
                                    .toLowerCase()
                                    .contains(postcodeFilter.toLowerCase()))
                    .filter(
                        p ->
                            addressFilter.isEmpty()
                                || p.getAddress()
                                    .toLowerCase()
                                    .contains(addressFilter.toLowerCase()))
                    .filter(
                        p ->
                            emailFilter.isEmpty()
                                || p.getEmail().toLowerCase().contains(emailFilter.toLowerCase()))
                    .filter(
                        p ->
                            phoneFilter.isEmpty()
                                || p.getPhone().toLowerCase().contains(phoneFilter.toLowerCase()))
                    .toList();

            populateTable(patients);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error filtering patients: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
          filterDialog.dispose();
        });

    cancelButton.addActionListener(e -> filterDialog.dispose());

    filterDialog.add(formPanel, BorderLayout.CENTER);
    filterDialog.add(filterButtonPanel, BorderLayout.SOUTH);
    filterDialog.pack();
    filterDialog.setLocationRelativeTo(this);
    filterDialog.setVisible(true);
  }
}
