package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Doctor;
import com.bougastefa.models.Specialist;
import com.bougastefa.services.DoctorService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

/**
 * Panel for managing Doctor entities in the application.
 * This panel extends BasePanel to provide specialized functionality for doctor management,
 * including adding, editing, deleting, and filtering doctors, with special handling for
 * Specialist doctors which are a subclass of Doctor with additional specialization information.
 */
public class DoctorPanel extends BasePanel<Doctor> {
  /** Service object that handles business logic and data operations for doctors */
  private final DoctorService doctorService;

  /**
   * Constructs a new DoctorPanel.
   * Initializes the panel with the "Doctor" title and loads initial doctor data.
   */
  public DoctorPanel() {
    super("Doctor");
    doctorService = new DoctorService();
    loadData();
  }

  /**
   * {@inheritDoc}
   * Defines the column names for the doctor table, including a special column for specialization.
   */
  @Override
  protected String[] getColumnNames() {
    return new String[] {
      "Doctor ID", "First Name", "Surname", "Address", "Email", "Hospital", "Specialization"
    };
  }

  /**
   * {@inheritDoc}
   * Loads all doctors from the service and populates the table with the data.
   */
  @Override
  protected void loadData() {
    try {
      List<Doctor> doctors = doctorService.getAllDoctors();
      populateTable(doctors);
    } catch (Exception ex) {
      showError("Error loading doctors", ex);
    }
  }

  /**
   * Populates the table with doctor data.
   * Handles the special case of Specialist doctors by including their specialization
   * in the appropriate column, while regular doctors have an empty specialization field.
   * 
   * @param doctors The list of doctors to display in the table
   */
  private void populateTable(List<Doctor> doctors) {
    tableModel.setRowCount(0);
    for (Doctor doctor : doctors) {
      // Determine specialization value based on doctor type
      String specialization = "";
      if (doctor instanceof Specialist) {
        specialization = ((Specialist) doctor).getSpecialization();
      }

      // Add all doctor data to the table as a new row
      tableModel.addRow(
          new Object[] {
            doctor.getDoctorId(),
            doctor.getFirstName(),
            doctor.getSurname(),
            doctor.getAddress(),
            doctor.getEmail(),
            doctor.getHospital(),
            specialization
          });
    }
  }

  /**
   * {@inheritDoc}
   * Retrieves the currently selected doctor from the table.
   * Maps the selected row to a Doctor object by using the doctorId to look up the full object.
   * 
   * @return The selected Doctor object, or null if no row is selected or an error occurs
   */
  @Override
  protected Doctor getSelectedItem() {
    int row = dataTable.getSelectedRow();
    if (row != -1) {
      try {
        String doctorId = (String) tableModel.getValueAt(row, 0);
        Doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
          showError("Could not find the selected doctor", null);
        }
        return doctor;
      } catch (Exception ex) {
        showError("Error retrieving doctor details", ex);
      }
    } else {
      showInfo("Please select a doctor first");
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * Shows a dialog for adding a new doctor.
   * Calls showDoctorDialog with null to indicate a new doctor is being created.
   */
  @Override
  protected void showAddDialog() {
    showDoctorDialog(null);
  }

  /**
   * {@inheritDoc}
   * Shows a dialog for editing an existing doctor.
   * Calls showDoctorDialog with the doctor object to pre-populate the form.
   * 
   * @param doctor The doctor to edit
   */
  @Override
  protected void showEditDialog(Doctor doctor) {
    showDoctorDialog(doctor);
  }

  /**
   * Creates and displays a form dialog for adding or editing a doctor.
   * Handles both regular Doctor and Specialist objects, with conditional UI logic
   * for the specialization field which only applies to Specialist doctors.
   * 
   * @param existingDoctor The doctor to edit, or null if creating a new doctor
   */
  private void showDoctorDialog(Doctor existingDoctor) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
            existingDoctor == null ? "Add Doctor" : "Edit Doctor");

    // Add form fields with initial values if editing
    String idValue = existingDoctor != null ? existingDoctor.getDoctorId() : "";
    String firstNameValue = existingDoctor != null ? existingDoctor.getFirstName() : "";
    String surnameValue = existingDoctor != null ? existingDoctor.getSurname() : "";
    String addressValue = existingDoctor != null ? existingDoctor.getAddress() : "";
    String emailValue = existingDoctor != null ? existingDoctor.getEmail() : "";
    String hospitalValue = existingDoctor != null ? existingDoctor.getHospital() : "";
    
    // Check if this doctor is a specialist and get its specialization if it is
    boolean isSpecialist = existingDoctor instanceof Specialist;
    String specializationValue = isSpecialist ? ((Specialist) existingDoctor).getSpecialization() : "";

    // Add all form fields to the dialog
    builder.addTextField("Doctor ID", "doctorId", idValue);
    builder.addTextField("First Name", "firstName", firstNameValue);
    builder.addTextField("Surname", "surname", surnameValue);
    builder.addTextField("Address", "address", addressValue);
    builder.addTextField("Email", "email", emailValue);
    builder.addTextField("Hospital", "hospital", hospitalValue);
    builder.addCheckBox("Is Specialist", "isSpecialist", isSpecialist);
    builder.addTextField("Specialization", "specialization", specializationValue);

    // Define save action
    builder.onSave(
        formData -> {
          try {
            // Extract form data
            String doctorId = (String) formData.get("doctorId");
            String firstName = (String) formData.get("firstName");
            String surname = (String) formData.get("surname");
            String address = (String) formData.get("address");
            String email = (String) formData.get("email");
            String hospital = (String) formData.get("hospital");
            boolean isSpecialistSelected = (Boolean) formData.get("isSpecialist");
            String specialization = (String) formData.get("specialization");

            // Validate doctor ID
            if (doctorId.isEmpty()) {
              showError("Doctor ID cannot be empty", null);
              return;
            }

            // Create doctor object based on whether it's a specialist
            Doctor doctor;
            if (isSpecialistSelected) {
              if (specialization.isEmpty()) {
                showError("Specialization cannot be empty for specialists", null);
                return;
              }
              doctor = new Specialist(doctorId, firstName, surname, address, email, hospital, specialization);
            } else {
              doctor = new Doctor(doctorId, firstName, surname, address, email, hospital);
            }

            // Add or update doctor based on whether we're editing or creating
            if (existingDoctor == null) {
              doctorService.addDoctor(doctor);
              showInfo("Doctor added successfully");
            } else {
              doctorService.updateDoctor(doctor);
              showInfo("Doctor updated successfully");
            }
            loadData(); // Refresh table after operation
          } catch (Exception ex) {
            showError("Error", ex);
          }
        });

    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable ID field if editing (since ID is the primary key and shouldn't change)
    if (existingDoctor != null) {
      JComponent idField = dialog.getField("doctorId");
      if (idField instanceof JTextField) {
        ((JTextField) idField).setEditable(false);
      }
    }

    // Add listener to enable/disable specialization field based on checkbox
    JCheckBox specialistCheckbox = (JCheckBox) dialog.getField("isSpecialist");
    JTextField specializationField = (JTextField) dialog.getField("specialization");
    
    // Initial state - specialization field enabled only if checkbox is selected
    specializationField.setEnabled(specialistCheckbox.isSelected());
    // Dynamic state - enable/disable field whenever checkbox state changes
    specialistCheckbox.addActionListener(e -> 
        specializationField.setEnabled(specialistCheckbox.isSelected()));

    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc}
   * Shows a dialog for advanced filtering of doctors.
   * Creates a filter form with fields corresponding to all doctor properties.
   */
  @Override
  protected void showAdvancedFilterDialog() {
    // Create filter dialog with the relevant fields
    FormDialog.Builder builder = createFilterDialog(
        "Advanced Filter",
        "doctorId",
        "firstName",
        "surname",
        "address",
        "email",
        "hospital",
        "specialization"
    );

    // Define filter action
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc}
   * Applies filter criteria to the list of doctors and updates the table.
   * Handles standard string fields through the parent class helper method,
   * with special handling for the specialization field which requires type checking
   * to identify Specialist doctors.
   * 
   * @param formData Map of field names to filter values from the filter dialog
   */
  @Override
  protected void applyFilters(Map<String, Object> formData) {
    try {
      List<Doctor> doctors = doctorService.getAllDoctors();
      
      // Define filter configurations for standard fields
      Map<String, Function<Doctor, String>> filterMappings =
          Map.of(
              "doctorId", Doctor::getDoctorId,
              "firstName", Doctor::getFirstName,
              "surname", Doctor::getSurname,
              "address", Doctor::getAddress,
              "email", Doctor::getEmail,
              "hospital", Doctor::getHospital);

      // Apply standard filters for non-empty fields
      FilterResult<Doctor> result = applyStandardFilters(doctors, formData, filterMappings);

      // Special handling for specialization since it's in a subclass
      String specializationFilter = (String) formData.get("specialization");
      if (specializationFilter != null && !specializationFilter.isEmpty()) {
        // Filter doctors to only include specialists with matching specialization
        List<Doctor> filteredBySpecialization =
            result.getResults().stream()
                .filter(
                    doctor -> {
                      if (doctor instanceof Specialist) {
                        String specialization = ((Specialist) doctor).getSpecialization();
                        return specialization != null
                            && specialization
                                .toLowerCase()
                                .contains(specializationFilter.toLowerCase());
                      }
                      return false;
                    })
                .toList();
        result = new FilterResult<>(filteredBySpecialization);
      }

      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering doctors", ex);
    }
  }

  /**
   * {@inheritDoc}
   * Deletes a doctor from the system after confirming with the user.
   * 
   * @param doctor The doctor to delete
   * @throws Exception If deletion fails
   */
  @Override
  protected void deleteItem(Doctor doctor) throws Exception {
    doctorService.deleteDoctor(doctor.getDoctorId());
  }
}
