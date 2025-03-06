package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Doctor;
import com.bougastefa.models.Specialist;
import com.bougastefa.services.DoctorService;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DoctorPanel extends JPanel {
  private final DoctorService doctorService;
  private DefaultTableModel tableModel;
  private JTable doctorTable;

  public DoctorPanel() {
    doctorService = new DoctorService();
    setLayout(new BorderLayout());

    // Add button panel
    ButtonPanel buttonPanel = new ButtonPanel("Doctor");
    buttonPanel.setAddButtonListener(e -> showDoctorDialog(null));
    buttonPanel.setEditButtonListener(e -> editSelectedDoctor());
    buttonPanel.setDeleteButtonListener(e -> deleteSelectedDoctor());
    buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
    buttonPanel.setRefreshButtonListener(e -> loadDoctors());

    add(buttonPanel, BorderLayout.NORTH);

    // Setup table for Doctors
    setupDoctorTable();
    JScrollPane scrollPane = new JScrollPane(doctorTable);
    add(scrollPane, BorderLayout.CENTER);

    // Initial load of doctors
    loadDoctors();
  }

  private void setupDoctorTable() {
    String[] columnNames = {
      "Doctor ID", "First Name", "Surname", "Address", "Email", "Hospital", "Specialization"
    };
    tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };
    doctorTable = new JTable(tableModel);
    doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    doctorTable.getTableHeader().setReorderingAllowed(false);
    doctorTable.setFillsViewportHeight(true);
  }

  private void loadDoctors() {
    try {
      List<Doctor> doctors = doctorService.getAllDoctors();
      populateTable(doctors);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading doctors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  private Doctor getSelectedDoctor() {
    int row = doctorTable.getSelectedRow();
    if (row != -1) {
      try {
        String doctorId = (String) tableModel.getValueAt(row, 0);
        Doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
          JOptionPane.showMessageDialog(
              this, "Could not find the selected doctor", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return doctor;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "Error retrieving doctor details: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(
          this, "Please select a doctor first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }
    return null;
  }

  private void editSelectedDoctor() {
    Doctor doctor = getSelectedDoctor();
    if (doctor != null) {
      showDoctorDialog(doctor);
    }
  }

  private void deleteSelectedDoctor() {
    Doctor doctor = getSelectedDoctor();
    if (doctor != null) {
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this doctor?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          doctorService.deleteDoctor(doctor.getDoctorId());
          loadDoctors();
          JOptionPane.showMessageDialog(this, "Doctor deleted successfully");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
              this,
              "Error deleting doctor: " + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

   private void showDoctorDialog(Doctor existingDoctor) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            (Frame) SwingUtilities.getWindowAncestor(this),
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
              JOptionPane.showMessageDialog(
                  this, "Doctor ID cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
              return;
            }

            // Create doctor object
            Doctor doctor;
            if (isSpecialistSelected) {
              if (specialization.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Specialization cannot be empty for specialists",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
              }
              doctor = new Specialist(doctorId, firstName, surname, address, email, hospital, specialization);
            } else {
              doctor = new Doctor(doctorId, firstName, surname, address, email, hospital);
            }

            // Add or update doctor
            if (existingDoctor == null) {
              doctorService.addDoctor(doctor);
              JOptionPane.showMessageDialog(this, "Doctor added successfully");
            } else {
              doctorService.updateDoctor(doctor);
              JOptionPane.showMessageDialog(this, "Doctor updated successfully");
            }
            loadDoctors();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  private void showAdvancedFilterDialog() {
    // Create a FormDialog.Builder for the filter dialog
    FormDialog.Builder builder =
        new FormDialog.Builder((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter");

    // Add filter fields
    builder.addTextField("Doctor ID", "doctorId");
    builder.addTextField("First Name", "firstName");
    builder.addTextField("Surname", "surname");
    builder.addTextField("Address", "address");
    builder.addTextField("Email", "email");
    builder.addTextField("Hospital", "hospital");
    builder.addTextField("Specialization", "specialization");

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
      List<Doctor> doctors = doctorService.getAllDoctors();
      FilterResult<Doctor> result = new FilterResult<>(doctors);

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
      for (Map.Entry<String, Function<Doctor, String>> entry : filterMappings.entrySet()) {
        String fieldName = entry.getKey();
        Function<Doctor, String> getter = entry.getValue();

        String filterValue = (String) formData.get(fieldName);
        if (filterValue != null && !filterValue.isEmpty()) {
          result = result.filter(filterValue, getter);
        }
      }

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
      JOptionPane.showMessageDialog(
          this, "Error filtering doctors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
