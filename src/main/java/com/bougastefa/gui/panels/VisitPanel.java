package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Visit;
import com.bougastefa.services.VisitService;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VisitPanel extends JPanel {
  private final VisitService visitService;
  private DefaultTableModel tableModel;
  private JTable visitTable;
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public VisitPanel() {
    visitService = new VisitService();
    setLayout(new BorderLayout());

    // Add button panel
    ButtonPanel buttonPanel = new ButtonPanel("Visit");
    buttonPanel.setAddButtonListener(e -> showVisitDialog(null));
    buttonPanel.setEditButtonListener(e -> editSelectedVisit());
    buttonPanel.setDeleteButtonListener(e -> deleteSelectedVisit());
    buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
    buttonPanel.setRefreshButtonListener(e -> loadVisits());

    add(buttonPanel, BorderLayout.NORTH);

    // Setup table for Visits
    setupVisitTable();
    JScrollPane scrollPane = new JScrollPane(visitTable);
    add(scrollPane, BorderLayout.CENTER);

    // Initial load of visits
    loadVisits();
  }

  private void setupVisitTable() {
    String[] columnNames = {"Date of Visit", "Doctor ID", "Patient ID", "Symptoms", "Diagnosis"};
    tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };
    visitTable = new JTable(tableModel);
    visitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  private void loadVisits() {
    try {
      List<Visit> visits = visitService.getAllVisits();
      populateTable(visits);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading visits: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  private Visit getSelectedVisit() {
    int row = visitTable.getSelectedRow();
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
        JOptionPane.showMessageDialog(
            this,
            "Error retrieving visit details: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(
          this, "Please select a visit first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }
    return null;
  }

  private void editSelectedVisit() {
    Visit visit = getSelectedVisit();
    if (visit != null) {
      showVisitDialog(visit);
    }
  }

  private void deleteSelectedVisit() {
    Visit visit = getSelectedVisit();
    if (visit != null) {
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this visit?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          visitService.deleteVisit(
              visit.getPatientId(), visit.getDoctorId(), visit.getDateOfVisit());
          loadVisits();
          JOptionPane.showMessageDialog(this, "Visit deleted successfully");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
              this, "Error deleting visit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  private void showVisitDialog(Visit existingVisit) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            (Frame) SwingUtilities.getWindowAncestor(this),
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
              JOptionPane.showMessageDialog(
                  this, "Date cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
              return;
            }

            LocalDate date;
            try {
              date = LocalDate.parse(dateStr, dateFormatter);
            } catch (DateTimeParseException ex) {
              JOptionPane.showMessageDialog(
                  this,
                  "Invalid date format. Please use yyyy-MM-dd",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            // Validate doctor and patient IDs
            if (doctorId.isEmpty() || patientId.isEmpty()) {
              JOptionPane.showMessageDialog(
                  this,
                  "Doctor ID and Patient ID cannot be empty",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            Visit visit = new Visit(date, symptoms, diagnosis, doctorId, patientId);

            if (existingVisit == null) {
              visitService.addVisit(visit);
              JOptionPane.showMessageDialog(this, "Visit added successfully");
            } else {
              visitService.updateVisit(visit);
              JOptionPane.showMessageDialog(this, "Visit updated successfully");
            }
            loadVisits();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  private void showAdvancedFilterDialog() {
    // Create a FormDialog.Builder for the filter dialog
    FormDialog.Builder builder =
        new FormDialog.Builder((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter");

    // Add filter fields
    builder.addTextField("Date", "dateOfVisit");
    builder.addTextField("Doctor ID", "doctorId");
    builder.addTextField("Patient ID", "patientId");
    builder.addTextField("Symptoms", "symptoms");
    builder.addTextField("Diagnosis", "diagnosis");

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
      List<Visit> visits = visitService.getAllVisits();
      FilterResult<Visit> result = new FilterResult<>(visits);

      // Define filter configurations for each field
      Map<String, Function<Visit, String>> filterMappings =
          Map.of(
              "dateOfVisit", visit -> visit.getDateOfVisit().format(dateFormatter),
              "doctorId", Visit::getDoctorId,
              "patientId", Visit::getPatientId,
              "symptoms", Visit::getSymptoms,
              "diagnosis", Visit::getDiagnosis);

      // Apply filters for non-empty fields
      for (Map.Entry<String, Function<Visit, String>> entry : filterMappings.entrySet()) {
        String fieldName = entry.getKey();
        Function<Visit, String> getter = entry.getValue();

        String filterValue = (String) formData.get(fieldName);
        if (filterValue != null && !filterValue.isEmpty()) {
          result = result.filter(filterValue, getter);
        }
      }

      populateTable(result.getResults());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error filtering visits: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
