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
import com.bougastefa.utils.FieldLengthConstants;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

/**
 * Panel for managing Patient entities in the application. This panel extends BasePanel to provide
 * specialized functionality for patient management, including adding, editing, deleting, and
 * filtering patients. It handles both regular Patient objects and InsuredPatient subclasses, with
 * special UI components for insurance-related fields. Additionally, it provides functionality to
 * display information about a patient's primary doctor (the doctor they have visited most often).
 */
public class PatientPanel extends BasePanel<Patient> {
  /** Service object that handles business logic and data operations for patients */
  private final PatientService patientService;

  /** Service object used for retrieving doctor information */
  private final DoctorService doctorService;

  /** Service object used to determine a patient's primary doctor based on visit history */
  private final VisitService visitService;

  /**
   * Constructs a new PatientPanel. Initializes the panel with the "Patient" title, sets up the
   * required services, adds a custom button for viewing the patient's primary doctor details, and
   * loads initial patient data.
   */
  public PatientPanel() {
    super("Patient");
    patientService = new PatientService();
    doctorService = new DoctorService();
    visitService = new VisitService();

    // Add custom button for Primary Doctor functionality
    addCustomButton("Primary Doctor", e -> showPrimaryDoctorDetails());

    loadData();
  }

  /**
   * {@inheritDoc} Defines the column names for the patient table, including a special column for
   * insurance ID.
   */
  @Override
  protected String[] getColumnNames() {
    return new String[] {
      "Patient ID", "First Name", "Surname", "Postcode", "Address", "Phone", "Email", "Insurance ID"
    };
  }

  /**
   * {@inheritDoc} Loads all patients from the service and populates the table with the data.
   * Handles any exceptions that may occur during the data loading process.
   */
  @Override
  protected void loadData() {
    try {
      List<Patient> patients = patientService.getAllPatients();
      populateTable(patients);
    } catch (Exception ex) {
      showError("Error loading patients", ex);
    }
  }

  /**
   * Populates the table with patient data. Handles both regular Patient and InsuredPatient objects,
   * showing the insurance ID for insured patients and an empty string for regular patients.
   *
   * @param patients The list of patients to display in the table
   */
  private void populateTable(List<Patient> patients) {
    tableModel.setRowCount(0);
    for (Patient patient : patients) {
      // Check if this patient is insured and get insurance ID if available
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

  /**
   * {@inheritDoc} Retrieves the currently selected patient from the table. Maps the selected row to
   * a Patient object by using the patientId to look up the full object.
   *
   * @return The selected Patient object, or null if no row is selected or an error occurs
   */
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

  /**
   * {@inheritDoc} Shows a dialog for adding a new patient. Calls showPatientDialog with null to
   * indicate a new patient is being created.
   */
  @Override
  protected void showAddDialog() {
    showPatientDialog(null);
  }

  /**
   * {@inheritDoc} Shows a dialog for editing an existing patient. Calls showPatientDialog with the
   * patient object to pre-populate the form.
   *
   * @param patient The patient to edit
   */
  @Override
  protected void showEditDialog(Patient patient) {
    showPatientDialog(patient);
  }

  /**
   * Creates and displays a form dialog for adding or editing a patient. Sets up the form with
   * fields for all patient properties, including conditional insurance-related fields for insured
   * patients. The dialog dynamically enables or disables the insurance ID field based on the "Is
   * Insured" checkbox.
   *
   * @param existingPatient The patient to edit, or null if creating a new patient
   */
  private void showPatientDialog(Patient existingPatient) {
    // Create FormDialog.Builder with appropriate title based on operation type
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(), existingPatient == null ? "Add Patient" : "Edit Patient");
    // Add form fields with initial values if editing and display max length info
    builder.addTextField(
        "Patient ID (max " + FieldLengthConstants.PATIENT_ID_MAX_LENGTH + " chars)",
        "patientId",
        existingPatient != null ? existingPatient.getPatientId() : "");

    builder.addTextField(
        "First Name (max " + FieldLengthConstants.PATIENT_FIRSTNAME_MAX_LENGTH + " chars)",
        "firstName",
        existingPatient != null ? existingPatient.getFirstName() : "");

    builder.addTextField(
        "Surname (max " + FieldLengthConstants.PATIENT_SURNAME_MAX_LENGTH + " chars)",
        "surname",
        existingPatient != null ? existingPatient.getSurname() : "");

    builder.addTextField(
        "Postcode (max " + FieldLengthConstants.PATIENT_POSTCODE_MAX_LENGTH + " chars)",
        "postcode",
        existingPatient != null ? existingPatient.getPostcode() : "");

    builder.addTextField(
        "Address (max " + FieldLengthConstants.PATIENT_ADDRESS_MAX_LENGTH + " chars)",
        "address",
        existingPatient != null ? existingPatient.getAddress() : "");

    builder.addTextField(
        "Email (max " + FieldLengthConstants.PATIENT_EMAIL_MAX_LENGTH + " chars)",
        "email",
        existingPatient != null ? existingPatient.getEmail() : "");

    builder.addTextField(
        "Phone (max " + FieldLengthConstants.PATIENT_PHONE_MAX_LENGTH + " chars)",
        "phone",
        existingPatient != null ? existingPatient.getPhone() : "");

    // Determine if this patient is insured for initial checkbox state and insurance ID value
    boolean isInsured = existingPatient instanceof InsuredPatient;
    String insuranceIdValue = isInsured ? ((InsuredPatient) existingPatient).getInsuranceId() : "";

    builder.addCheckBox("Is Insured", "isInsured", isInsured);
    builder.addTextField(
        "Insurance ID (max " + FieldLengthConstants.INSURANCE_ID_MAX_LENGTH + " chars)",
        "insuranceId",
        insuranceIdValue);

    // Define save action that will be called when form is submitted
    builder.onSave(
        formData -> {
          try {
            // Extract form data from the submitted form
            String patientId = (String) formData.get("patientId");
            String firstName = (String) formData.get("firstName");
            String surname = (String) formData.get("surname");
            String postcode = (String) formData.get("postcode");
            String address = (String) formData.get("address");
            String phone = (String) formData.get("phone");
            String email = (String) formData.get("email");
            boolean isInsuredSelected = (Boolean) formData.get("isInsured");
            String insuranceId = (String) formData.get("insuranceId");

            // Validate required fields
            if (patientId.isEmpty()) {
              showError("Patient ID is a required field", null);
              return;
            }

            // Validate field lengths
            if (patientId.length() > FieldLengthConstants.PATIENT_ID_MAX_LENGTH) {
              showError(
                  "Patient ID exceeds maximum length of "
                      + FieldLengthConstants.PATIENT_ID_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (firstName.length() > FieldLengthConstants.PATIENT_FIRSTNAME_MAX_LENGTH) {
              showError(
                  "First name exceeds maximum length of "
                      + FieldLengthConstants.PATIENT_FIRSTNAME_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (surname.length() > FieldLengthConstants.PATIENT_SURNAME_MAX_LENGTH) {
              showError(
                  "Surname exceeds maximum length of "
                      + FieldLengthConstants.PATIENT_SURNAME_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (postcode != null
                && postcode.length() > FieldLengthConstants.PATIENT_POSTCODE_MAX_LENGTH) {
              showError(
                  "Postcode exceeds maximum length of "
                      + FieldLengthConstants.PATIENT_POSTCODE_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (address != null
                && address.length() > FieldLengthConstants.PATIENT_ADDRESS_MAX_LENGTH) {
              showError(
                  "Address exceeds maximum length of "
                      + FieldLengthConstants.PATIENT_ADDRESS_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (phone != null && phone.length() > FieldLengthConstants.PATIENT_PHONE_MAX_LENGTH) {
              showError(
                  "Phone exceeds maximum length of "
                      + FieldLengthConstants.PATIENT_PHONE_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (email != null && email.length() > FieldLengthConstants.PATIENT_EMAIL_MAX_LENGTH) {
              showError(
                  "Email exceeds maximum length of "
                      + FieldLengthConstants.PATIENT_EMAIL_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            // Create appropriate patient object based on insurance status
            Patient patient;
            if (isInsuredSelected) {
              // For insured patients, validate insurance ID and create InsuredPatient
              if (insuranceId.isEmpty()) {
                showError("Insurance ID cannot be empty for insured patients", null);
                return;
              }

              if (insuranceId.length() > FieldLengthConstants.INSURANCE_ID_MAX_LENGTH) {
                showError(
                    "Insurance ID exceeds maximum length of "
                        + FieldLengthConstants.INSURANCE_ID_MAX_LENGTH
                        + " characters",
                    null);
                return;
              }

              patient =
                  new InsuredPatient(
                      patientId, firstName, surname, postcode, address, phone, email, insuranceId);
            } else {
              // For regular patients, create standard Patient object
              patient = new Patient(patientId, firstName, surname, postcode, address, phone, email);
            }

            // Add or update patient based on whether we're editing or creating
            if (existingPatient == null) {
              patientService.addPatient(patient);
              showInfo("Patient added successfully");
            } else {
              patientService.updatePatient(patient);
              showInfo("Patient updated successfully");
            }

            // Refresh display to show changes
            loadData();

          } catch (IllegalArgumentException e) {
            showError(e.getMessage(), null);
          } catch (Exception ex) {
            showError("Error: " + ex.getMessage(), ex);
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

    // Set initial enabled state based on checkbox
    insuranceIdField.setEnabled(insuredCheckbox.isSelected());
    // Add listener to update enabled state whenever checkbox changes
    insuredCheckbox.addActionListener(
        e -> insuranceIdField.setEnabled(insuredCheckbox.isSelected()));

    dialog.setVisible(true);
  }

  /**
   * Shows a dialog with details about the selected patient's primary doctor. The primary doctor is
   * defined as the doctor the patient has visited most frequently. If the patient has no visits, or
   * if the doctor information cannot be found, appropriate error messages are displayed.
   */
  private void showPrimaryDoctorDetails() {
    // Get the currently selected patient
    Patient selectedPatient = getSelectedItem();
    if (selectedPatient == null) {
      return; // getSelectedItem() already shows a message if nothing is selected
    }

    // Find the primary doctor ID based on visit frequency
    String primaryDoctorId = visitService.getPrimaryDoctorId(selectedPatient.getPatientId());
    if (primaryDoctorId == null) {
      showInfo("No primary doctor found. The patient has no recorded visits.");
      return;
    }

    // Get the full doctor details
    Doctor primaryDoctor = doctorService.getDoctorById(primaryDoctorId);
    if (primaryDoctor == null) {
      showError("Could not find doctor details", null);
      return;
    }

    // Create and show the doctor details dialog
    FormDialog.Builder builder = new FormDialog.Builder(getParentFrame(), "Primary Doctor Details");

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

    // Make all fields read-only since this is just a view
    setupReadOnlyDialog(
        dialog,
        "doctorId",
        "firstName",
        "surname",
        "address",
        "email",
        "hospital",
        "specialization");

    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc} Shows a dialog for advanced filtering of patients. Creates a filter form with
   * fields corresponding to all patient properties, including the insurance ID field for filtering
   * insured patients.
   */
  @Override
  protected void showAdvancedFilterDialog() {
    // Create filter dialog with the relevant fields
    FormDialog.Builder builder =
        createFilterDialog(
            "Advanced Filter",
            "patientId",
            "firstName",
            "surname",
            "postcode",
            "address",
            "phone",
            "email",
            "insuranceId");

    // Define filter action to be called when filter is applied
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc} Applies filter criteria to the list of patients and updates the table. Uses the
   * FilterResult helper class for standard fields, with special handling for the insuranceId field
   * which requires type checking to identify InsuredPatient objects.
   *
   * @param formData Map of field names to filter values from the filter dialog
   */
  @Override
  protected void applyFilters(Map<String, Object> formData) {
    try {
      // Get all patients to start with
      List<Patient> patients = patientService.getAllPatients();

      // Define filter configurations for standard fields
      Map<String, Function<Patient, String>> filterMappings =
          Map.of(
              "patientId", Patient::getPatientId,
              "firstName", Patient::getFirstName,
              "surname", Patient::getSurname,
              "postcode", Patient::getPostcode,
              "address", Patient::getAddress,
              "phone", Patient::getPhone,
              "email", Patient::getEmail);

      // Apply standard filters for each field using the helper method from BasePanel
      FilterResult<Patient> result = applyStandardFilters(patients, formData, filterMappings);

      // Special handling for insurance ID since it's in a subclass
      String insuranceIdFilter = (String) formData.get("insuranceId");
      if (insuranceIdFilter != null && !insuranceIdFilter.isEmpty()) {
        // Filter patients to only include insured patients with matching insurance ID
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

      // Update the table with the filtered results
      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering patients", ex);
    }
  }

  /**
   * {@inheritDoc} Deletes a patient from the system. Note: This operation may fail if there are
   * visits or prescriptions associated with this patient, as there will be foreign key constraints
   * in the database.
   *
   * @param patient The patient to delete
   * @throws Exception If deletion fails
   */
  @Override
  protected void deleteItem(Patient patient) throws Exception {
    patientService.deletePatient(patient.getPatientId());
  }
}
