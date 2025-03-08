package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Visit;
import com.bougastefa.services.VisitService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

public class VisitPanel extends BasePanel<Visit> {
  private final VisitService visitService;
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public VisitPanel() {
    super("Visit");
    visitService = new VisitService();
    loadData();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] {"Date of Visit", "Doctor ID", "Patient ID", "Symptoms", "Diagnosis"};
  }

  @Override
  protected void loadData() {
    try {
      List<Visit> visits = visitService.getAllVisits();
      populateTable(visits);
    } catch (Exception ex) {
      showError("Error loading visits", ex);
    }
  }

  private void populateTable(List<Visit> visits) {
    tableModel.setRowCount(0);
    for (Visit visit : visits) {
      tableModel.addRow(
          new Object[] {
            visit.getDateOfVisit().format(dateFormatter),
            visit.getDoctorId(),
            visit.getPatientId(),
            visit.getSymptoms(),
            visit.getDiagnosis()
          });
    }
  }

  @Override
  protected Visit getSelectedItem() {
    int row = dataTable.getSelectedRow();
    if (row != -1) {
      try {
        String dateStr = (String) tableModel.getValueAt(row, 0);
        LocalDate dateOfVisit = LocalDate.parse(dateStr, dateFormatter);
        String doctorId = (String) tableModel.getValueAt(row, 1);
        String patientId = (String) tableModel.getValueAt(row, 2);
        String symptoms = (String) tableModel.getValueAt(row, 3);
        String diagnosis = (String) tableModel.getValueAt(row, 4);

        // Create a visit object from the table values
        return new Visit(dateOfVisit, symptoms, diagnosis, doctorId, patientId);
      } catch (Exception ex) {
        showError("Error retrieving visit details", ex);
      }
    } else {
      showInfo("Please select a visit first");
    }
    return null;
  }

  @Override
  protected void showAddDialog() {
    showVisitDialog(null);
  }

  @Override
  protected void showEditDialog(Visit visit) {
    showVisitDialog(visit);
  }

  private void showVisitDialog(Visit existingVisit) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
            existingVisit == null ? "Add Visit" : "Edit Visit");

    // Add form fields with initial values if editing
    String dateValue =
        existingVisit != null
            ? existingVisit.getDateOfVisit().format(dateFormatter)
            : LocalDate.now().format(dateFormatter);
    String doctorIdValue = existingVisit != null ? existingVisit.getDoctorId() : "";
    String patientIdValue = existingVisit != null ? existingVisit.getPatientId() : "";
    String symptomsValue = existingVisit != null ? existingVisit.getSymptoms() : "";
    String diagnosisValue = existingVisit != null ? existingVisit.getDiagnosis() : "";

    builder.addTextField("Date (yyyy-MM-dd)", "dateOfVisit", dateValue);
    builder.addTextField("Doctor ID", "doctorId", doctorIdValue);
    builder.addTextField("Patient ID", "patientId", patientIdValue);
    builder.addTextField("Symptoms", "symptoms", symptomsValue);
    builder.addTextField("Diagnosis", "diagnosis", diagnosisValue);

    // Define save action
    builder.onSave(
        formData -> {
          try {
            // Extract form data
            String dateStr = (String) formData.get("dateOfVisit");
            String doctorId = (String) formData.get("doctorId");
            String patientId = (String) formData.get("patientId");
            String symptoms = (String) formData.get("symptoms");
            String diagnosis = (String) formData.get("diagnosis");

            // Validate date format
            if (dateStr.isEmpty()) {
              showError("Date cannot be empty", null);
              return;
            }

            LocalDate date;
            try {
              date = LocalDate.parse(dateStr, dateFormatter);
            } catch (DateTimeParseException ex) {
              showError("Invalid date format. Please use yyyy-MM-dd", ex);
              return;
            }

            // Validate doctor and patient IDs
            if (doctorId.isEmpty() || patientId.isEmpty()) {
              showError("Doctor ID and Patient ID cannot be empty", null);
              return;
            }

            Visit visit = new Visit(date, symptoms, diagnosis, doctorId, patientId);

            if (existingVisit == null) {
              visitService.addVisit(visit);
              showInfo("Visit added successfully");
            } else {
              visitService.updateVisit(visit);
              showInfo("Visit updated successfully");
            }
            loadData();
          } catch (Exception ex) {
            showError("Error", ex);
          }
        });

    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable composite primary key fields if editing
    if (existingVisit != null) {
      JComponent dateField = dialog.getField("dateOfVisit");
      JComponent doctorIdField = dialog.getField("doctorId");
      JComponent patientIdField = dialog.getField("patientId");

      if (dateField instanceof JTextField) {
        ((JTextField) dateField).setEditable(false);
      }

      if (doctorIdField instanceof JTextField) {
        ((JTextField) doctorIdField).setEditable(false);
      }

      if (patientIdField instanceof JTextField) {
        ((JTextField) patientIdField).setEditable(false);
      }
    }

    dialog.setVisible(true);
  }

  @Override
  protected void showAdvancedFilterDialog() {
    // Create filter dialog with the relevant fields
    FormDialog.Builder builder = createFilterDialog(
        "Advanced Filter", "dateOfVisit", "doctorId", "patientId", "symptoms", "diagnosis");

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
      List<Visit> visits = visitService.getAllVisits();
      
      // Define filter configurations for each field
      Map<String, Function<Visit, String>> filterMappings =
          Map.of(
              "dateOfVisit", visit -> visit.getDateOfVisit().format(dateFormatter),
              "doctorId", Visit::getDoctorId,
              "patientId", Visit::getPatientId,
              "symptoms", Visit::getSymptoms,
              "diagnosis", Visit::getDiagnosis);

      // Apply standard filters
      FilterResult<Visit> result = applyStandardFilters(visits, formData, filterMappings);

      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering visits", ex);
    }
  }

  @Override
  protected void deleteItem(Visit visit) throws Exception {
    visitService.deleteVisit(visit.getPatientId(), visit.getDoctorId(), visit.getDateOfVisit());
  }
}
