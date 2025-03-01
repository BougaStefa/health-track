package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterDialog;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FilterableField;
import com.bougastefa.models.Visit;
import com.bougastefa.services.VisitService;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingVisit == null ? "Add Visit" : "Edit Visit",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering visit details
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    JTextField dateField = new JTextField(20);
    JTextField doctorIdField = new JTextField(20);
    JTextField patientIdField = new JTextField(20);
    JTextField symptomsField = new JTextField(20);
    JTextField diagnosisField = new JTextField(20);

    if (existingVisit != null) {
      dateField.setText(existingVisit.getDateOfVisit().format(dateFormatter));
      // Make the composite primary key fields uneditable if editing
      dateField.setEditable(false);
      doctorIdField.setText(existingVisit.getDoctorId());
      doctorIdField.setEditable(false);
      patientIdField.setText(existingVisit.getPatientId());
      patientIdField.setEditable(false);
      symptomsField.setText(existingVisit.getSymptoms());
      diagnosisField.setText(existingVisit.getDiagnosis());
    }

    addFormField(formPanel, "Date (yyyy-MM-dd):", dateField, gbc, 0);
    addFormField(formPanel, "Doctor ID:", doctorIdField, gbc, 1);
    addFormField(formPanel, "Patient ID:", patientIdField, gbc, 2);
    addFormField(formPanel, "Symptoms:", symptomsField, gbc, 3);
    addFormField(formPanel, "Diagnosis:", diagnosisField, gbc, 4);

    // Button panel for save and cancel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            // Validate date format
            String dateStr = dateField.getText().trim();
            if (dateStr.isEmpty()) {
              JOptionPane.showMessageDialog(
                  dialog, "Date cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
              return;
            }

            LocalDate date;
            try {
              date = LocalDate.parse(dateStr, dateFormatter);
            } catch (DateTimeParseException ex) {
              JOptionPane.showMessageDialog(
                  dialog,
                  "Invalid date format. Please use yyyy-MM-dd",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            // Validate doctor and patient IDs
            String doctorId = doctorIdField.getText().trim();
            String patientId = patientIdField.getText().trim();
            if (doctorId.isEmpty() || patientId.isEmpty()) {
              JOptionPane.showMessageDialog(
                  dialog,
                  "Doctor ID and Patient ID cannot be empty",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            Visit visit =
                new Visit(
                    date, symptomsField.getText(), diagnosisField.getText(), doctorId, patientId);

            if (existingVisit == null) {
              visitService.addVisit(visit);
              JOptionPane.showMessageDialog(dialog, "Visit added successfully");
            } else {
              visitService.updateVisit(visit);
              JOptionPane.showMessageDialog(dialog, "Visit updated successfully");
            }
            loadVisits();
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
    panel.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    panel.add(field, gbc);
  }

  private void showAdvancedFilterDialog() {
    List<FilterableField> fields =
        Arrays.asList(
            new FilterableField("Date", "dateOfVisit"),
            new FilterableField("Doctor ID", "doctorId"),
            new FilterableField("Patient ID", "patientId"),
            new FilterableField("Symptoms", "symptoms"),
            new FilterableField("Diagnosis", "diagnosis"));

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
      List<Visit> visits = visitService.getAllVisits();
      FilterResult<Visit> result = new FilterResult<>(visits);

      if (filters.containsKey("dateOfVisit")) {
        result =
            result.filter(
                filters.get("dateOfVisit"), visit -> visit.getDateOfVisit().format(dateFormatter));
      }
      if (filters.containsKey("doctorId")) {
        result = result.filter(filters.get("doctorId"), Visit::getDoctorId);
      }
      if (filters.containsKey("patientId")) {
        result = result.filter(filters.get("patientId"), Visit::getPatientId);
      }
      if (filters.containsKey("symptoms")) {
        result = result.filter(filters.get("symptoms"), Visit::getSymptoms);
      }
      if (filters.containsKey("diagnosis")) {
        result = result.filter(filters.get("diagnosis"), Visit::getDiagnosis);
      }

      populateTable(result.getResults());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error filtering visits: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
