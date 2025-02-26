package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.models.Doctor;
import com.bougastefa.models.Patient;
import com.bougastefa.services.DoctorService;
import com.bougastefa.services.PatientService;
import com.bougastefa.services.VisitService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PatientPanel extends JPanel {
  private final PatientService patientService = new PatientService();
  private final DoctorService doctorService = new DoctorService();
  private final VisitService visitService = new VisitService();
  private DefaultTableModel tableModel;
  private JTable patientTable;

  public PatientPanel() {
    setLayout(new BorderLayout());

    ButtonPanel buttonPanel = new ButtonPanel("Patient");
    buttonPanel.setAddButtonListener(e -> showPatientDialog(null));
    buttonPanel.setEditButtonListener(
        e -> {
          Patient selectedPatient = getSelectedPatient();
          if (selectedPatient != null) {
            showPatientDialog(selectedPatient);
          } else {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit");
          }
        });
    buttonPanel.setDeleteButtonListener(e -> deleteSelectedPatient());
    buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
    buttonPanel.setRefreshButtonListener(e -> loadPatients());

    // Add custom button for Primary Doctor functionality
    buttonPanel.addCustomButton("Primary Doctor", e -> showPrimaryDoctorDetails());

    add(buttonPanel, BorderLayout.NORTH);

    // Setup table for Patients
    String[] columnNames = {
      "Patient ID", "First Name", "Surname", "Postcode", "Address", "Phone", "Email"
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
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

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

    addFormField(formPanel, "Patient ID:", idField, gbc, 0);
    addFormField(formPanel, "First Name:", firstNameField, gbc, 1);
    addFormField(formPanel, "Surname:", surnameField, gbc, 2);
    addFormField(formPanel, "Postcode:", postcodeField, gbc, 3);
    addFormField(formPanel, "Address:", addressField, gbc, 4);
    addFormField(formPanel, "Email:", emailField, gbc, 5);
    addFormField(formPanel, "Phone:", phoneField, gbc, 6);

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
                    idField.getText(),
                    firstNameField.getText(),
                    surnameField.getText(),
                    postcodeField.getText(),
                    addressField.getText(),
                    emailField.getText(),
                    phoneField.getText());
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

  private void addFormField(
      JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    panel.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    panel.add(field, gbc);
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

  private void showPrimaryDoctorDetails() {
    Patient selectedPatient = getSelectedPatient();
    if (selectedPatient == null) {
      JOptionPane.showMessageDialog(this, "Please select a patient first");
      return;
    }

    String primaryDoctorId = visitService.getPrimaryDoctorId(selectedPatient.getPatientId());
    if (primaryDoctorId == null) {
      JOptionPane.showMessageDialog(
          this,
          "No primary doctor found. The patient has no recorded visits.",
          "No Primary Doctor",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    Doctor primaryDoctor = doctorService.getDoctorById(primaryDoctorId);
    if (primaryDoctor == null) {
      JOptionPane.showMessageDialog(
          this, "Error: Could not find doctor details", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Create and show the doctor details dialog
    JDialog dialog =
        new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Primary Doctor Details", true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Create panel for doctor details
    JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Add doctor details
    detailsPanel.add(new JLabel("Doctor ID:"));
    detailsPanel.add(new JLabel(primaryDoctor.getDoctorId()));

    detailsPanel.add(new JLabel("First Name:"));
    detailsPanel.add(new JLabel(primaryDoctor.getFirstName()));

    detailsPanel.add(new JLabel("Surname:"));
    detailsPanel.add(new JLabel(primaryDoctor.getSurname()));

    detailsPanel.add(new JLabel("Address:"));
    detailsPanel.add(new JLabel(primaryDoctor.getAddress()));

    detailsPanel.add(new JLabel("Email:"));
    detailsPanel.add(new JLabel(primaryDoctor.getEmail()));

    detailsPanel.add(new JLabel("Hospital:"));
    detailsPanel.add(new JLabel(primaryDoctor.getHospital()));

    // Add close button
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dialog.dispose());
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(closeButton);

    dialog.add(detailsPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void showAdvancedFilterDialog() {
    JDialog filterDialog =
        new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter", true);
    filterDialog.setLayout(new BorderLayout(10, 10));

    // Form panel for filter criteria
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
    formPanel.add(new JLabel("Phone contains:"));
    formPanel.add(filterEmailField);
    formPanel.add(new JLabel("Email contains:"));
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

            // Apply filters
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
            filterDialog.dispose();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error applying filters: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    cancelButton.addActionListener(e -> filterDialog.dispose());

    filterDialog.add(formPanel, BorderLayout.CENTER);
    filterDialog.add(filterButtonPanel, BorderLayout.SOUTH);
    filterDialog.pack();
    filterDialog.setLocationRelativeTo(this);
    filterDialog.setVisible(true);
  }
}
