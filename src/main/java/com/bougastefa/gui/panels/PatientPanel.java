package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Doctor;
import com.bougastefa.models.InsuredPatient;
import com.bougastefa.models.Patient;
import com.bougastefa.models.Specialist;
import com.bougastefa.services.DoctorService;
import com.bougastefa.services.PatientService;
import com.bougastefa.services.VisitService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

public class PatientPanel extends BasePanel<Patient> {
  private final PatientService patientService;
  private final DoctorService doctorService;
  private final VisitService visitService;

  public PatientPanel() {
    super("Patient");
    patientService = new PatientService();
    doctorService = new DoctorService();
    visitService = new VisitService();
    
    // Add custom button for Primary Doctor functionality
    addCustomButton("Primary Doctor", e -> showPrimaryDoctorDetails());
    
    loadData();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] {
      "Patient ID", "First Name", "Surname", "Postcode", "Address", "Phone", "Email", "Insurance ID"
    };
  }

  @Override
  protected void loadData() {
    try {
      List<Patient> patients = patientService.getAllPatients();
      populateTable(patients);
    } catch (Exception ex) {
      showError("Error loading patients", ex);
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

  @Override
  protected Patient getSelectedItem() {
    int row = dataTable.getSelectedRow();
    if (row != -1) {
      try {
        String patientId = (String) tableModel.getValueAt(row, 0);
        Patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
          showError("Could not find the selected patient", null);
        }
        return patient;
      } catch (Exception ex) {
        showError("Error retrieving patient details", ex);
      }
    } else {
      showInfo("Please select a patient first");
    }
    return null;
  }

  @Override
  protected void showAddDialog() {
    showPatientDialog(null);
  }

  @Override
  protected void showEditDialog(Patient patient) {
    showPatientDialog(patient);
  }

  private void showPatientDialog(Patient existingPatient) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
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
              showError("Patient ID cannot be empty", null);
              return;
            }

            // Create patient object
            Patient patient;
            if (isInsuredSelected) {
              if (insuranceId.isEmpty()) {
                showError("Insurance ID cannot be empty for insured patients", null);
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
              showInfo("Patient added successfully");
            } else {
              patientService.updatePatient(patient);
              showInfo("Patient updated successfully");
            }
            loadData();
          } catch (Exception ex) {
            showError("Error", ex);
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

  private void showPrimaryDoctorDetails() {
    Patient selectedPatient = getSelectedItem();
    if (selectedPatient == null) {
      return; // getSelectedItem() already shows a message
    }

    String primaryDoctorId = visitService.getPrimaryDoctorId(selectedPatient.getPatientId());
    if (primaryDoctorId == null) {
      showInfo("No primary doctor found. The patient has no recorded visits.");
      return;
    }

    Doctor primaryDoctor = doctorService.getDoctorById(primaryDoctorId);
    if (primaryDoctor == null) {
      showError("Could not find doctor details", null);
      return;
    }

    // Create and show the doctor details dialog
    FormDialog.Builder builder =
        new FormDialog.Builder(getParentFrame(), "Primary Doctor Details");

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
    setupReadOnlyDialog(dialog, 
        "doctorId", "firstName", "surname", "address", "email", "hospital", "specialization");

    dialog.setVisible(true);
  }

  @Override
  protected void showAdvancedFilterDialog() {
    // Create a FormDialog.Builder for the filter dialog
    FormDialog.Builder builder = createFilterDialog(
        "Advanced Filter",
        "patientId",
        "firstName",
        "surname",
        "postcode",
        "address",
        "phone",
        "email",
        "insuranceId"
    );

    // Define filter action
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  @Override
  protected void applyFilters(Map<String, Object> formData) {
    try {
      List<Patient> patients = patientService.getAllPatients();
      
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

      // Apply standard filters
      FilterResult<Patient> result = applyStandardFilters(patients, formData, filterMappings);

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
      showError("Error filtering patients", ex);
    }
  }

  @Override
  protected void deleteItem(Patient patient) throws Exception {
    patientService.deletePatient(patient.getPatientId());
  }
}
