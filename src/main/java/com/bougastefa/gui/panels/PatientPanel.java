package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Doctor;
import com.bougastefa.models.InsuredPatient;
import com.bougastefa.models.Patient;
import com.bougastefa.models.Specialist;
import com.bougastefa.services.DoctorService;
import com.bougastefa.services.PatientService;
import com.bougastefa.services.VisitService;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    buttonPanel.setEditButtonListener(e -> editSelectedPatient());
    buttonPanel.setDeleteButtonListener(e -> deleteSelectedPatient());
    buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
    buttonPanel.setRefreshButtonListener(e -> loadPatients());

    // Add custom button for Primary Doctor functionality
    buttonPanel.addCustomButton("Primary Doctor", e -> showPrimaryDoctorDetails());

    add(buttonPanel, BorderLayout.NORTH);

    // Setup table for Patients
    setupPatientTable();
    JScrollPane scrollPane = new JScrollPane(patientTable);
    add(scrollPane, BorderLayout.CENTER);

    // Initial load of patients
    loadPatients();
  }

  private void setupPatientTable() {
    String[] columnNames = {
      "Patient ID", "First Name", "Surname", "Postcode", "Address", "Phone", "Email", "Insurance ID"
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
    patientTable.getTableHeader().setReorderingAllowed(false);
    patientTable.setFillsViewportHeight(true);
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
      String insuranceId = "";
      if (patient instanceof InsuredPatient) {
        insuranceId = ((InsuredPatient) patient).getInsuranceId();
      }
      tableModel.addRow(
          new Object[] {
            patient.getPatientId(),
            patient.getFirstName(),
            patient.getSurname(),
            patient.getPostcode(),
            patient.getAddress(),
            patient.getPhone(),
            patient.getEmail(),
            insuranceId
          });
    }
  }

  private Patient getSelectedPatient() {
    int row = patientTable.getSelectedRow();
    if (row != -1) {
      try {
        String patientId = (String) tableModel.getValueAt(row, 0);
        Patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
          JOptionPane.showMessageDialog(
              this, "Could not find the selected patient", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return patient;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "Error retrieving patient details: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(
          this, "Please select a patient first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }
    return null;
  }

  private void editSelectedPatient() {
    Patient patient = getSelectedPatient();
    if (patient != null) {
      showPatientDialog(patient);
    }
  }

  private void showPatientDialog(Patient existingPatient) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingPatient == null ? "Add Patient" : "Edit Patient");

    // Add form fields with initial values if editing
    String idValue = existingPatient != null ? existingPatient.getPatientId() : "";
    String firstNameValue = existingPatient != null ? existingPatient.getFirstName() : "";
    String surnameValue = existingPatient != null ? existingPatient.getSurname() : "";
    String postcodeValue = existingPatient != null ? existingPatient.getPostcode() : "";
    String addressValue = existingPatient != null ? existingPatient.getAddress() : "";
    String phoneValue = existingPatient != null ? existingPatient.getPhone() : "";
    String emailValue = existingPatient != null ? existingPatient.getEmail() : "";

    boolean isInsured = existingPatient instanceof InsuredPatient;
    String insuranceIdValue = isInsured ? ((InsuredPatient) existingPatient).getInsuranceId() : "";

    builder.addTextField("Patient ID", "patientId", idValue);
    builder.addTextField("First Name", "firstName", firstNameValue);
    builder.addTextField("Surname", "surname", surnameValue);
    builder.addTextField("Postcode", "postcode", postcodeValue);
    builder.addTextField("Address", "address", addressValue);
    builder.addTextField("Phone", "phone", phoneValue);
    builder.addTextField("Email", "email", emailValue);
    builder.addCheckBox("Is Insured", "isInsured", isInsured);
    builder.addTextField("Insurance ID", "insuranceId", insuranceIdValue);

    // Define save action
    builder.onSave(
        formData -> {
          try {
            // Extract form data
            String patientId = (String) formData.get("patientId");
            String firstName = (String) formData.get("firstName");
            String surname = (String) formData.get("surname");
            String postcode = (String) formData.get("postcode");
            String address = (String) formData.get("address");
            String phone = (String) formData.get("phone");
            String email = (String) formData.get("email");
            boolean isInsuredSelected = (Boolean) formData.get("isInsured");
            String insuranceId = (String) formData.get("insuranceId");

            // Validate patient ID
            if (patientId.isEmpty()) {
              JOptionPane.showMessageDialog(
                  this,
                  "Patient ID cannot be empty",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            // Create patient object
            Patient patient;
            if (isInsuredSelected) {
              if (insuranceId.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Insurance ID cannot be empty for insured patients",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
              }
              patient =
                  new InsuredPatient(
                      patientId, firstName, surname, postcode, address, phone, email, insuranceId);
            } else {
              patient = new Patient(patientId, firstName, surname, postcode, address, phone, email);
            }

            // Add or update patient
            if (existingPatient == null) {
              patientService.addPatient(patient);
              JOptionPane.showMessageDialog(this, "Patient added successfully");
            } else {
              patientService.updatePatient(patient);
              JOptionPane.showMessageDialog(this, "Patient updated successfully");
            }
            loadPatients();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable ID field if editing
    if (existingPatient != null) {
      JComponent idField = dialog.getField("patientId");
      if (idField instanceof JTextField) {
        ((JTextField) idField).setEditable(false);
      }
    }

    // Add listener to enable/disable insurance ID field based on checkbox
    JCheckBox insuredCheckbox = (JCheckBox) dialog.getField("isInsured");
    JTextField insuranceIdField = (JTextField) dialog.getField("insuranceId");

    insuranceIdField.setEnabled(insuredCheckbox.isSelected());
    insuredCheckbox.addActionListener(
        e -> insuranceIdField.setEnabled(insuredCheckbox.isSelected()));

    dialog.setVisible(true);
  }

  private void deleteSelectedPatient() {
    Patient patient = getSelectedPatient();
    if (patient != null) {
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this patient?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          patientService.deletePatient(patient.getPatientId());
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
    FormDialog.Builder builder =
        new FormDialog.Builder(
            (Frame) SwingUtilities.getWindowAncestor(this), "Primary Doctor Details");

    // Add read-only fields for doctor details
    builder.addTextField("Doctor ID", "doctorId", primaryDoctor.getDoctorId());
    builder.addTextField("First Name", "firstName", primaryDoctor.getFirstName());
    builder.addTextField("Surname", "surname", primaryDoctor.getSurname());
    builder.addTextField("Address", "address", primaryDoctor.getAddress());
    builder.addTextField("Email", "email", primaryDoctor.getEmail());
    builder.addTextField("Hospital", "hospital", primaryDoctor.getHospital());

    // Show specialization if the doctor is a specialist
    if (primaryDoctor instanceof Specialist) {
      builder.addTextField(
          "Specialization", "specialization", ((Specialist) primaryDoctor).getSpecialization());
    }

    // Just use an empty save action since we're only viewing data
    builder.onSave(formData -> {});

    FormDialog dialog = builder.build();

    // Make all fields read-only
    for (String fieldName :
        List.of(
            "doctorId", "firstName", "surname", "address", "email", "hospital", "specialization")) {
      JComponent field = dialog.getField(fieldName);
      if (field instanceof JTextField) {
        ((JTextField) field).setEditable(false);
      }
    }

    dialog.setSaveButtonText("Close");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  private void showAdvancedFilterDialog() {
    // Create a FormDialog.Builder for the filter dialog
    FormDialog.Builder builder =
        new FormDialog.Builder((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter");

    // Add filter fields
    builder.addTextField("Patient ID", "patientId");
    builder.addTextField("First Name", "firstName");
    builder.addTextField("Surname", "surname");
    builder.addTextField("Postcode", "postcode");
    builder.addTextField("Address", "address");
    builder.addTextField("Phone", "phone");
    builder.addTextField("Email", "email");
    builder.addTextField("Insurance ID", "insuranceId");

    // Define filter action
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  private void applyFilters(Map<String, Object> formData) {
    try {
      List<Patient> patients = patientService.getAllPatients();
      FilterResult<Patient> result = new FilterResult<>(patients);

      // Define filter configurations for each field
      Map<String, Function<Patient, String>> filterMappings =
          Map.of(
              "patientId", Patient::getPatientId,
              "firstName", Patient::getFirstName,
              "surname", Patient::getSurname,
              "postcode", Patient::getPostcode,
              "address", Patient::getAddress,
              "phone", Patient::getPhone,
              "email", Patient::getEmail);

      // Apply filters for non-empty fields
      for (Map.Entry<String, Function<Patient, String>> entry : filterMappings.entrySet()) {
        String fieldName = entry.getKey();
        Function<Patient, String> getter = entry.getValue();

        String filterValue = (String) formData.get(fieldName);
        if (filterValue != null && !filterValue.isEmpty()) {
          result = result.filter(filterValue, getter);
        }
      }

      // Special handling for insurance ID since it's in a subclass
      String insuranceIdFilter = (String) formData.get("insuranceId");
      if (insuranceIdFilter != null && !insuranceIdFilter.isEmpty()) {
        List<Patient> filteredByInsuranceId =
            result.getResults().stream()
                .filter(
                    patient -> {
                      if (patient instanceof InsuredPatient) {
                        String insuranceId = ((InsuredPatient) patient).getInsuranceId();
                        return insuranceId != null
                            && insuranceId.toLowerCase().contains(insuranceIdFilter.toLowerCase());
                      }
                      return false;
                    })
                .toList();
        result = new FilterResult<>(filteredByInsuranceId);
      }

      populateTable(result.getResults());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error filtering patients: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
