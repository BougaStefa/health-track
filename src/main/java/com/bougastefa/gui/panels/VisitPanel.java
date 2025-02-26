package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.models.Visit;
import com.bougastefa.services.VisitService;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VisitPanel extends JPanel {
  private VisitService visitService;
  private DefaultTableModel tableModel;
  private JTable visitTable;
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public VisitPanel() {
    visitService = new VisitService();
    setLayout(new BorderLayout());

    ButtonPanel buttonPanel = new ButtonPanel("Visit");
    buttonPanel.setAddButtonListener(e -> showVisitDialog(null));
    buttonPanel.setEditButtonListener(
        e -> {
          Visit selectedVisit = getSelectedVisit();
          if (selectedVisit != null) {
            showVisitDialog(selectedVisit);
          } else {
            JOptionPane.showMessageDialog(this, "Please select a visit to edit");
          }
        });
    buttonPanel.setDeleteButtonListener(e -> deleteSelectedVisit());
    buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
    buttonPanel.setRefreshButtonListener(e -> loadVisits());

    add(buttonPanel, BorderLayout.NORTH);

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
    JScrollPane scrollPane = new JScrollPane(visitTable);
    add(scrollPane, BorderLayout.CENTER);

    // Initial load of visits
    loadVisits();
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
      LocalDate dateOfVisit =
          LocalDate.parse((String) tableModel.getValueAt(row, 0), dateFormatter);
      String doctorId = (String) tableModel.getValueAt(row, 1);
      String patientId = (String) tableModel.getValueAt(row, 2);
      String symptoms = (String) tableModel.getValueAt(row, 3);
      String diagnosis = (String) tableModel.getValueAt(row, 4);

      return new Visit(dateOfVisit, symptoms, diagnosis, doctorId, patientId);
    }
    return null;
  }

  private void showVisitDialog(Visit existingVisit) {
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingVisit == null ? "Add Visit" : "Edit Visit",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering visit details
    JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField dateField = new JTextField(20);
    JTextField doctorIdField = new JTextField(20);
    JTextField patientIdField = new JTextField(20);
    JTextField symptomsField = new JTextField(20);
    JTextField diagnosisField = new JTextField(20);

    if (existingVisit != null) {
      dateField.setText(existingVisit.getDateOfVisit().format(dateFormatter));
      doctorIdField.setText(existingVisit.getDoctorId());
      patientIdField.setText(existingVisit.getPatientId());
      symptomsField.setText(existingVisit.getSymptoms());
      diagnosisField.setText(existingVisit.getDiagnosis());

      // For composite key, can't edit fields.
      dateField.setEditable(false);
      doctorIdField.setEditable(false);
      patientIdField.setEditable(false);
    }

    formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
    formPanel.add(dateField);
    formPanel.add(new JLabel("Doctor ID:"));
    formPanel.add(doctorIdField);
    formPanel.add(new JLabel("Patient ID:"));
    formPanel.add(patientIdField);
    formPanel.add(new JLabel("Symptoms:"));
    formPanel.add(symptomsField);
    formPanel.add(new JLabel("Diagnosis:"));
    formPanel.add(diagnosisField);

    // Button panel for save and cancel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            String dateText = dateField.getText().trim();
            String doctorId = doctorIdField.getText().trim();
            String patientId = patientIdField.getText().trim();
            String symptoms = symptomsField.getText().trim();
            String diagnosis = diagnosisField.getText().trim();

            // Handle date
            LocalDate date;
            try {
              date = LocalDate.parse(dateText, dateFormatter);
            } catch (DateTimeParseException dtpe) {
              JOptionPane.showMessageDialog(
                  this,
                  "Please enter the date in YYYY-MM-DD format",
                  "Invalid Date Format",
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
            dialog.dispose();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void deleteSelectedVisit() {
    int row = visitTable.getSelectedRow();
    if (row != -1) {
      LocalDate dateOfVisit =
          LocalDate.parse((String) tableModel.getValueAt(row, 0), dateFormatter);
      String doctorId = (String) tableModel.getValueAt(row, 1);
      String patientId = (String) tableModel.getValueAt(row, 2);

      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this visit?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          visitService.deleteVisit(patientId, doctorId, dateOfVisit);
          loadVisits();
          JOptionPane.showMessageDialog(this, "Visit deleted successfully");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
              this, "Error deleting visit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a visit to delete");
    }
  }

  private void showAdvancedFilterDialog() {
    JDialog filterDialog =
        new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter", true);
    filterDialog.setLayout(new BorderLayout(10, 10));

    JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField filterDateField = new JTextField(20);
    JTextField filterDoctorIdField = new JTextField(20);
    JTextField filterPatientIdField = new JTextField(20);
    JTextField filterSymptomsField = new JTextField(20);
    JTextField filterDiagnosisField = new JTextField(20);

    formPanel.add(new JLabel("Date contains:"));
    formPanel.add(filterDateField);
    formPanel.add(new JLabel("Doctor ID contains:"));
    formPanel.add(filterDoctorIdField);
    formPanel.add(new JLabel("Patient ID contains:"));
    formPanel.add(filterPatientIdField);
    formPanel.add(new JLabel("Symptoms contain:"));
    formPanel.add(filterSymptomsField);
    formPanel.add(new JLabel("Diagnosis contains:"));
    formPanel.add(filterDiagnosisField);

    JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton filterButton = new JButton("Filter");
    JButton cancelButton = new JButton("Cancel");
    filterButtonPanel.add(filterButton);
    filterButtonPanel.add(cancelButton);

    filterButton.addActionListener(
        e -> {
          try {
            List<Visit> visits = visitService.getAllVisits();

            String dateFilter = filterDateField.getText().trim();
            String doctorIdFilter = filterDoctorIdField.getText().trim();
            String patientIdFilter = filterPatientIdField.getText().trim();
            String symptomsFilter = filterSymptomsField.getText().trim();
            String diagnosisFilter = filterDiagnosisField.getText().trim();

            visits =
                visits.stream()
                    .filter(
                        v ->
                            dateFilter.isEmpty()
                                || v.getDateOfVisit().format(dateFormatter).contains(dateFilter))
                    .filter(
                        v ->
                            doctorIdFilter.isEmpty()
                                || v.getDoctorId()
                                    .toLowerCase()
                                    .contains(doctorIdFilter.toLowerCase()))
                    .filter(
                        v ->
                            patientIdFilter.isEmpty()
                                || v.getPatientId()
                                    .toLowerCase()
                                    .contains(patientIdFilter.toLowerCase()))
                    .filter(
                        v ->
                            symptomsFilter.isEmpty()
                                || v.getSymptoms()
                                    .toLowerCase()
                                    .contains(symptomsFilter.toLowerCase()))
                    .filter(
                        v ->
                            diagnosisFilter.isEmpty()
                                || v.getDiagnosis()
                                    .toLowerCase()
                                    .contains(diagnosisFilter.toLowerCase()))
                    .toList();

            populateTable(visits);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error filtering visits: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
          filterDialog.dispose();
        });

    cancelButton.addActionListener(e -> filterDialog.dispose());

    filterDialog.add(formPanel, BorderLayout.CENTER);
    filterDialog.add(filterButtonPanel, BorderLayout.SOUTH);
    filterDialog.pack();
    filterDialog.setLocationRelativeTo(this);
    filterDialog.setVisible(true);
  }
}
