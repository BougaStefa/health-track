package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Prescription;
import com.bougastefa.services.PrescriptionService;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PrescriptionPanel extends JPanel {
  private final PrescriptionService prescriptionService;
  private final DefaultTableModel tableModel;
  private final JTable prescriptionTable;
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public PrescriptionPanel() {
    prescriptionService = new PrescriptionService();
    setLayout(new BorderLayout());

    // Replace custom button panels with ButtonPanel component
    ButtonPanel buttonPanel = new ButtonPanel("Prescription");
    buttonPanel.setAddButtonListener(e -> showPrescriptionDialog(null));
    buttonPanel.setEditButtonListener(e -> editSelectedPrescription());
    buttonPanel.setDeleteButtonListener(e -> deleteSelectedPrescription());
    buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
    buttonPanel.setRefreshButtonListener(e -> loadPrescriptions());

    add(buttonPanel, BorderLayout.NORTH);

    // Setup table for Prescriptions
    String[] columnNames = {
      "Prescription ID",
      "Date",
      "Drug ID",
      "Doctor ID",
      "Patient ID",
      "Dosage",
      "Duration",
      "Comment"
    };
    tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };

    prescriptionTable = new JTable(tableModel);
    prescriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(prescriptionTable);
    add(scrollPane, BorderLayout.CENTER);

    // Initial load of prescriptions
    loadPrescriptions();
  }

  private void loadPrescriptions() {
    try {
      List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
      populateTable(prescriptions);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this,
          "Error loading prescriptions: " + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void populateTable(List<Prescription> prescriptions) {
    tableModel.setRowCount(0);
    for (Prescription prescription : prescriptions) {
      tableModel.addRow(
          new Object[] {
            prescription.getPrescriptionId(),
            prescription.getDateOfPrescribe().format(dateFormatter),
            prescription.getDrugId(),
            prescription.getDoctorId(),
            prescription.getPatientId(),
            prescription.getDosage(),
            prescription.getDuration(),
            prescription.getComment()
          });
    }
  }

  private Prescription getSelectedPrescription() {
    int row = prescriptionTable.getSelectedRow();
    if (row != -1) {
      try {
        String prescriptionId = (String) tableModel.getValueAt(row, 0);
        return prescriptionService.getPrescriptionById(prescriptionId);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "Error retrieving prescription details: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(
          this,
          "Please select a prescription first",
          "No Selection",
          JOptionPane.INFORMATION_MESSAGE);
    }
    return null;
  }

  private void editSelectedPrescription() {
    Prescription prescription = getSelectedPrescription();
    if (prescription != null) {
      showPrescriptionDialog(prescription);
    }
  }

  private void showPrescriptionDialog(Prescription existingPrescription) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingPrescription == null ? "Add Prescription" : "Edit Prescription");

    // Add form fields with initial values if editing
    String idValue = existingPrescription != null ? existingPrescription.getPrescriptionId() : "";
    String dateValue =
        existingPrescription != null
            ? existingPrescription.getDateOfPrescribe().format(dateFormatter)
            : LocalDate.now().format(dateFormatter);
    String drugIdValue = existingPrescription != null ? existingPrescription.getDrugId() : "";
    String doctorIdValue = existingPrescription != null ? existingPrescription.getDoctorId() : "";
    String patientIdValue = existingPrescription != null ? existingPrescription.getPatientId() : "";
    String dosageValue =
        existingPrescription != null ? String.valueOf(existingPrescription.getDosage()) : "";
    String durationValue =
        existingPrescription != null ? String.valueOf(existingPrescription.getDuration()) : "";
    String commentValue = existingPrescription != null ? existingPrescription.getComment() : "";

    builder.addTextField("Prescription ID", "prescriptionId", idValue);
    builder.addTextField("Date (YYYY-MM-DD)", "date", dateValue);
    builder.addTextField("Drug ID", "drugId", drugIdValue);
    builder.addTextField("Doctor ID", "doctorId", doctorIdValue);
    builder.addTextField("Patient ID", "patientId", patientIdValue);
    builder.addTextField("Dosage", "dosage", dosageValue);
    builder.addTextField("Duration (days)", "duration", durationValue);
    builder.addTextField("Comment", "comment", commentValue);

    // Define save action
    builder.onSave(
        formData -> {
          try {
            // Extract form data
            String id = (String) formData.get("prescriptionId");
            String dateText = (String) formData.get("date");
            String drugId = (String) formData.get("drugId");
            String doctorId = (String) formData.get("doctorId");
            String patientId = (String) formData.get("patientId");
            String dosageText = (String) formData.get("dosage");
            String durationText = (String) formData.get("duration");
            String comment = (String) formData.get("comment");

            // Validate prescription ID
            if (id.isEmpty()) {
              JOptionPane.showMessageDialog(
                  this,
                  "Prescription ID cannot be empty",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            // Parse date and numeric values
            LocalDate date =
                dateText.isEmpty() ? LocalDate.now() : LocalDate.parse(dateText, dateFormatter);
            int dosage = dosageText.isEmpty() ? 0 : Integer.parseInt(dosageText);
            int duration = durationText.isEmpty() ? 0 : Integer.parseInt(durationText);

            // Create prescription object
            Prescription prescription =
                new Prescription(id, date, dosage, duration, comment, drugId, doctorId, patientId);

            // Add or update prescription
            if (existingPrescription == null) {
              prescriptionService.addPrescription(prescription);
              JOptionPane.showMessageDialog(this, "Prescription added successfully");
            } else {
              prescriptionService.updatePrescription(prescription);
              JOptionPane.showMessageDialog(this, "Prescription updated successfully");
            }

            // Refresh display
            loadPrescriptions();
          } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter the date in YYYY-MM-DD format or leave it empty for today's date",
                "Invalid Date Format",
                JOptionPane.ERROR_MESSAGE);
          } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter valid numbers for dosage and duration or leave them empty",
                "Invalid Number Format",
                JOptionPane.ERROR_MESSAGE);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable ID field if editing
    if (existingPrescription != null) {
      JComponent idField = dialog.getField("prescriptionId");
      if (idField instanceof JTextField) {
        ((JTextField) idField).setEditable(false);
      }
    }

    dialog.setVisible(true);
  }

  private void deleteSelectedPrescription() {
    Prescription prescription = getSelectedPrescription();
    if (prescription != null) {
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this prescription?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          prescriptionService.deletePrescription(prescription.getPrescriptionId());
          loadPrescriptions();
          JOptionPane.showMessageDialog(this, "Prescription deleted successfully");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
              this,
              "Error deleting prescription: " + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  private void showAdvancedFilterDialog() {
    // Create a FormDialog.Builder for the filter dialog
    FormDialog.Builder builder =
        new FormDialog.Builder((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter");

    // Add filter fields
    builder.addTextField("Prescription ID", "prescriptionId");
    builder.addTextField("Date", "date");
    builder.addTextField("Drug ID", "drugId");
    builder.addTextField("Doctor ID", "doctorId");
    builder.addTextField("Patient ID", "patientId");
    builder.addTextField("Dosage", "dosage");
    builder.addTextField("Duration", "duration");
    builder.addTextField("Comment", "comment");

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
      List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
      FilterResult<Prescription> result = new FilterResult<>(prescriptions);

      // Define filter configurations for each field
      Map<String, Function<Prescription, String>> filterMappings =
          Map.ofEntries(
              Map.entry("prescriptionId", Prescription::getPrescriptionId),
              Map.entry("date", p -> p.getDateOfPrescribe().format(dateFormatter)),
              Map.entry("drugId", Prescription::getDrugId),
              Map.entry("doctorId", Prescription::getDoctorId),
              Map.entry("patientId", Prescription::getPatientId),
              Map.entry("dosage", p -> String.valueOf(p.getDosage())),
              Map.entry("duration", p -> String.valueOf(p.getDuration())),
              Map.entry("comment", Prescription::getComment));

      // Apply filters for non-empty fields
      for (Map.Entry<String, Function<Prescription, String>> entry : filterMappings.entrySet()) {
        String fieldName = entry.getKey();
        Function<Prescription, String> getter = entry.getValue();

        String filterValue = (String) formData.get(fieldName);
        if (filterValue != null && !filterValue.isEmpty()) {
          result = result.filter(filterValue, getter);
        }
      }

      populateTable(result.getResults());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this,
          "Error filtering prescriptions: " + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
