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

public class DoctorPanel extends BasePanel<Doctor> {
  private final DoctorService doctorService;

  public DoctorPanel() {
    super("Doctor");
    doctorService = new DoctorService();
    loadData();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] {
      "Doctor ID", "First Name", "Surname", "Address", "Email", "Hospital", "Specialization"
    };
  }

  @Override
  protected void loadData() {
    try {
      List<Doctor> doctors = doctorService.getAllDoctors();
      populateTable(doctors);
    } catch (Exception ex) {
      showError("Error loading doctors", ex);
    }
  }

  private void populateTable(List<Doctor> doctors) {
    tableModel.setRowCount(0);
    for (Doctor doctor : doctors) {
      String specialization = "";
      if (doctor instanceof Specialist) {
        specialization = ((Specialist) doctor).getSpecialization();
      }

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

  @Override
  protected void showAddDialog() {
    showDoctorDialog(null);
  }

  @Override
  protected void showEditDialog(Doctor doctor) {
    showDoctorDialog(doctor);
  }

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
    
    boolean isSpecialist = existingDoctor instanceof Specialist;
    String specializationValue = isSpecialist ? ((Specialist) existingDoctor).getSpecialization() : "";

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

            // Create doctor object
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

            // Add or update doctor
            if (existingDoctor == null) {
              doctorService.addDoctor(doctor);
              showInfo("Doctor added successfully");
            } else {
              doctorService.updateDoctor(doctor);
              showInfo("Doctor updated successfully");
            }
            loadData();
          } catch (Exception ex) {
            showError("Error", ex);
          }
        });

    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable ID field if editing
    if (existingDoctor != null) {
      JComponent idField = dialog.getField("doctorId");
      if (idField instanceof JTextField) {
        ((JTextField) idField).setEditable(false);
      }
    }

    // Add listener to enable/disable specialization field based on checkbox
    JCheckBox specialistCheckbox = (JCheckBox) dialog.getField("isSpecialist");
    JTextField specializationField = (JTextField) dialog.getField("specialization");
    
    specializationField.setEnabled(specialistCheckbox.isSelected());
    specialistCheckbox.addActionListener(e -> 
        specializationField.setEnabled(specialistCheckbox.isSelected()));

    dialog.setVisible(true);
  }

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

  @Override
  protected void deleteItem(Doctor doctor) throws Exception {
    doctorService.deleteDoctor(doctor.getDoctorId());
  }
}
