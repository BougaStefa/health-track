package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterDialog;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FilterableField;
import com.bougastefa.models.Doctor;
import com.bougastefa.models.Specialist;
import com.bougastefa.services.DoctorService;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingDoctor == null ? "Add Doctor" : "Edit Doctor",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering doctor details
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    JTextField idField = new JTextField(20);
    JTextField firstNameField = new JTextField(20);
    JTextField surnameField = new JTextField(20);
    JTextField addressField = new JTextField(20);
    JTextField emailField = new JTextField(20);
    JTextField hospitalField = new JTextField(20);
    JTextField specializationField = new JTextField(20);
    JCheckBox isSpecialistCheckBox = new JCheckBox("Is Specialist");

    if (existingDoctor != null) {
      idField.setText(existingDoctor.getDoctorId());
      idField.setEditable(false);
      firstNameField.setText(existingDoctor.getFirstName());
      surnameField.setText(existingDoctor.getSurname());
      addressField.setText(existingDoctor.getAddress());
      emailField.setText(existingDoctor.getEmail());
      hospitalField.setText(existingDoctor.getHospital());

      if (existingDoctor instanceof Specialist) {
        isSpecialistCheckBox.setSelected(true);
        specializationField.setText(((Specialist) existingDoctor).getSpecialization());
      } else {
        specializationField.setEnabled(false);
      }
    } else {
      specializationField.setEnabled(false);
    }

    // Add listener to enable/disable the specialization field based on checkbox
    isSpecialistCheckBox.addActionListener(
        e -> specializationField.setEnabled(isSpecialistCheckBox.isSelected()));

    addFormField(formPanel, "Doctor ID:", idField, gbc, 0);
    addFormField(formPanel, "First Name:", firstNameField, gbc, 1);
    addFormField(formPanel, "Surname:", surnameField, gbc, 2);
    addFormField(formPanel, "Address:", addressField, gbc, 3);
    addFormField(formPanel, "Email:", emailField, gbc, 4);
    addFormField(formPanel, "Hospital:", hospitalField, gbc, 5);
    addFormField(formPanel, "", isSpecialistCheckBox, gbc, 6);
    addFormField(formPanel, "Specialization:", specializationField, gbc, 7);

    // Button panel for save and cancel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            String doctorId = idField.getText().trim();
            if (doctorId.isEmpty()) {
              JOptionPane.showMessageDialog(
                  dialog,
                  "Doctor ID cannot be empty",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            String firstName = firstNameField.getText().trim();
            String surname = surnameField.getText().trim();
            String address = addressField.getText().trim();
            String email = emailField.getText().trim();
            String hospital = hospitalField.getText().trim();

            Doctor doctor;
            if (isSpecialistCheckBox.isSelected()) {
              String specialization = specializationField.getText().trim();
              if (specialization.isEmpty()) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Specialization cannot be empty for specialists",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
              }
              doctor =
                  new Specialist(
                      doctorId, firstName, surname, address, email, hospital, specialization);
            } else {
              doctor = new Doctor(doctorId, firstName, surname, address, email, hospital);
            }

            if (existingDoctor == null) {
              doctorService.addDoctor(doctor);
              JOptionPane.showMessageDialog(dialog, "Doctor added successfully");
            } else {
              doctorService.updateDoctor(doctor);
              JOptionPane.showMessageDialog(dialog, "Doctor updated successfully");
            }
            loadDoctors();
            dialog.dispose();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    if (!label.isEmpty()) {
      panel.add(new JLabel(label), gbc);
    } else {
      panel.add(new JLabel(), gbc);
    }

    gbc.gridx = 1;
    gbc.weightx = 1;
    panel.add(field, gbc);
  }

  private void showAdvancedFilterDialog() {
    List<FilterableField> fields =
        Arrays.asList(
            new FilterableField("Doctor ID", "doctorId"),
            new FilterableField("First Name", "firstName"),
            new FilterableField("Surname", "surname"),
            new FilterableField("Address", "address"),
            new FilterableField("Email", "email"),
            new FilterableField("Hospital", "hospital"),
            new FilterableField("Specialization", "specialization"));

    FilterDialog dialog =
        new FilterDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Advanced Filter",
            fields,
            this::applyFilters);

    dialog.setVisible(true);
  }

  private void applyFilters(Map<String, String> filters) {
    try {
      List<Doctor> doctors = doctorService.getAllDoctors();
      FilterResult<Doctor> result = new FilterResult<>(doctors);

      if (filters.containsKey("doctorId")) {
        result = result.filter(filters.get("doctorId"), Doctor::getDoctorId);
      }
      if (filters.containsKey("firstName")) {
        result = result.filter(filters.get("firstName"), Doctor::getFirstName);
      }
      if (filters.containsKey("surname")) {
        result = result.filter(filters.get("surname"), Doctor::getSurname);
      }
      if (filters.containsKey("address")) {
        result = result.filter(filters.get("address"), Doctor::getAddress);
      }
      if (filters.containsKey("email")) {
        result = result.filter(filters.get("email"), Doctor::getEmail);
      }
      if (filters.containsKey("hospital")) {
        result = result.filter(filters.get("hospital"), Doctor::getHospital);
      }
      if (filters.containsKey("specialization")) {
        String specializationFilter = filters.get("specialization");
        // Special handling for specialization since it's only on Specialist subclass
        List<Doctor> filteredDoctors =
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
        result = new FilterResult<>(filteredDoctors);
      }

      populateTable(result.getResults());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error filtering doctors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
