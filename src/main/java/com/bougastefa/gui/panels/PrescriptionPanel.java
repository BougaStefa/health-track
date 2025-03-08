package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Prescription;
import com.bougastefa.services.PrescriptionService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

public class PrescriptionPanel extends BasePanel<Prescription> {
  private final PrescriptionService prescriptionService;
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public PrescriptionPanel() {
    super("Prescription");
    prescriptionService = new PrescriptionService();
    loadData();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] {
      "Prescription ID",
      "Date",
      "Drug ID",
      "Doctor ID",
      "Patient ID",
      "Dosage",
      "Duration",
      "Comment"
    };
  }

  @Override
  protected void loadData() {
    try {
      List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
      populateTable(prescriptions);
    } catch (Exception ex) {
      showError("Error loading prescriptions", ex);
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

  @Override
  protected Prescription getSelectedItem() {
    int row = dataTable.getSelectedRow();
    if (row != -1) {
      try {
        String prescriptionId = (String) tableModel.getValueAt(row, 0);
        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
        if (prescription == null) {
          showError("Could not find the selected prescription", null);
        }
        return prescription;
      } catch (Exception ex) {
        showError("Error retrieving prescription details", ex);
      }
    } else {
      showInfo("Please select a prescription first");
    }
    return null;
  }

  @Override
  protected void showAddDialog() {
    showPrescriptionDialog(null);
  }

  @Override
  protected void showEditDialog(Prescription prescription) {
    showPrescriptionDialog(prescription);
  }

  private void showPrescriptionDialog(Prescription existingPrescription) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
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
              showError("Prescription ID cannot be empty", null);
              return;
            }

            // Parse date and numeric values
            LocalDate date;
            try {
              date = dateText.isEmpty() ? LocalDate.now() : LocalDate.parse(dateText, dateFormatter);
            } catch (DateTimeParseException dtpe) {
              showError("Please enter the date in YYYY-MM-DD format or leave it empty for today's date", dtpe);
              return;
            }
            
            int dosage, duration;
            try {
              dosage = dosageText.isEmpty() ? 0 : Integer.parseInt(dosageText);
              duration = durationText.isEmpty() ? 0 : Integer.parseInt(durationText);
            } catch (NumberFormatException nfe) {
              showError("Please enter valid numbers for dosage and duration or leave them empty", nfe);
              return;
            }

            // Create prescription object
            Prescription prescription =
                new Prescription(id, date, dosage, duration, comment, drugId, doctorId, patientId);

            // Add or update prescription
            if (existingPrescription == null) {
              prescriptionService.addPrescription(prescription);
              showInfo("Prescription added successfully");
            } else {
              prescriptionService.updatePrescription(prescription);
              showInfo("Prescription updated successfully");
            }

            // Refresh display
            loadData();
          } catch (Exception ex) {
            showError("Error", ex);
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

  @Override
  protected void showAdvancedFilterDialog() {
    // Create a filter dialog with the relevant fields
    FormDialog.Builder builder = createFilterDialog(
        "Advanced Filter",
        "prescriptionId",
        "date",
        "drugId",
        "doctorId",
        "patientId",
        "dosage",
        "duration",
        "comment"
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
      List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
      
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

      // Apply standard filters
      FilterResult<Prescription> result = applyStandardFilters(prescriptions, formData, filterMappings);

      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering prescriptions", ex);
    }
  }

  @Override
  protected void deleteItem(Prescription prescription) throws Exception {
    prescriptionService.deletePrescription(prescription.getPrescriptionId());
  }
}
