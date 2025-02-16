package com.bougastefa.gui.panels;

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
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

  public VisitPanel() {
    visitService = new VisitService();
    setLayout(new BorderLayout());

    // Top panel with three sections:
    // Left: Advanced Filter and Clear Filters buttons.
    // Center: Add, Edit, Delete buttons.
    // Right: Refresh button.
    JPanel topPanel = new JPanel(new BorderLayout());

    // Left section: Advanced Filter and Clear Filters buttons
    JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton advancedFilterButton = new JButton("Advanced Filter");
    JButton clearFiltersButton = new JButton("Clear Filters");
    leftButtonPanel.add(advancedFilterButton);
    leftButtonPanel.add(clearFiltersButton);

    // Center section: Add, Edit and Delete buttons
    JPanel centerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton addButton = new JButton("Add Visit");
    JButton editButton = new JButton("Edit Visit");
    JButton deleteButton = new JButton("Delete Visit");
    centerButtonPanel.add(addButton);
    centerButtonPanel.add(editButton);
    centerButtonPanel.add(deleteButton);

    // Right section: Refresh button
    JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton refreshButton = new JButton("Refresh");
    rightButtonPanel.add(refreshButton);

    topPanel.add(leftButtonPanel, BorderLayout.WEST);
    topPanel.add(centerButtonPanel, BorderLayout.CENTER);
    topPanel.add(rightButtonPanel, BorderLayout.EAST);
    add(topPanel, BorderLayout.NORTH);

    // Setup table for Visits
    String[] columnNames = {"Date", "Symptoms", "Diagnosis", "Doctor ID", "Patient ID"};
    tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };

    visitTable = new JTable(tableModel);
    visitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    visitTable.getTableHeader().setReorderingAllowed(false);
    JScrollPane scrollPane = new JScrollPane(visitTable);
    add(scrollPane, BorderLayout.CENTER);

    // Listeners for top buttons
    refreshButton.addActionListener(e -> loadVisits());
    advancedFilterButton.addActionListener(e -> showAdvancedFilterDialog());
    clearFiltersButton.addActionListener(e -> loadVisits());

    addButton.addActionListener(e -> showVisitDialog(null));
    editButton.addActionListener(
        e -> {
          Visit selectedVisit = getSelectedVisit();
          if (selectedVisit != null) {
            showVisitDialog(selectedVisit);
          } else {
            JOptionPane.showMessageDialog(this, "Please select a visit to edit");
          }
        });
    deleteButton.addActionListener(e -> deleteSelectedVisit());

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
            visit.getSymptoms(),
            visit.getDiagnosis(),
            visit.getDoctorId(),
            visit.getPatientId()
          });
    }
  }

  private Visit getSelectedVisit() {
    int row = visitTable.getSelectedRow();
    if (row != -1) {
      return new Visit(
          LocalDate.parse((String) tableModel.getValueAt(row, 0), dateFormatter),
          (String) tableModel.getValueAt(row, 1),
          (String) tableModel.getValueAt(row, 2),
          (String) tableModel.getValueAt(row, 3),
          (String) tableModel.getValueAt(row, 4));
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
    JTextField symptomsField = new JTextField(20);
    JTextField diagnosisField = new JTextField(20);
    JTextField doctorIdField = new JTextField(20);
    JTextField patientIdField = new JTextField(20);

    if (existingVisit != null) {
      dateField.setText(existingVisit.getDateOfVisit().format(dateFormatter));
      symptomsField.setText(existingVisit.getSymptoms());
      diagnosisField.setText(existingVisit.getDiagnosis());
      doctorIdField.setText(existingVisit.getDoctorId());
      patientIdField.setText(existingVisit.getPatientId());
    }

    formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
    formPanel.add(dateField);
    formPanel.add(new JLabel("Symptoms:"));
    formPanel.add(symptomsField);
    formPanel.add(new JLabel("Diagnosis:"));
    formPanel.add(diagnosisField);
    formPanel.add(new JLabel("Doctor ID:"));
    formPanel.add(doctorIdField);
    formPanel.add(new JLabel("Patient ID:"));
    formPanel.add(patientIdField);

    // Button panel for save and cancel
    JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    dialogButtonPanel.add(saveButton);
    dialogButtonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            LocalDate visitDate = LocalDate.parse(dateField.getText(), dateFormatter);
            Visit visit =
                new Visit(
                    visitDate,
                    symptomsField.getText(),
                    diagnosisField.getText(),
                    doctorIdField.getText(),
                    patientIdField.getText());
            if (existingVisit == null) {
              visitService.addVisit(visit);
              JOptionPane.showMessageDialog(this, "Visit added successfully");
            } else {
              visitService.updateVisit(visit);
              JOptionPane.showMessageDialog(this, "Visit updated successfully");
            }
            loadVisits();
            dialog.dispose();
          } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid date format. Please use YYYY-MM-DD format.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(dialogButtonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void deleteSelectedVisit() {
    int row = visitTable.getSelectedRow();
    if (row != -1) {
      String dateStr = (String) tableModel.getValueAt(row, 0);
      String patientId = (String) tableModel.getValueAt(row, 4);
      String doctorId = (String) tableModel.getValueAt(row, 3);
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this visit?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          LocalDate visitDate = LocalDate.parse(dateStr, dateFormatter);
          visitService.deleteVisit(patientId, doctorId, visitDate);
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

  // Popup dialog for advanced filtering
  private void showAdvancedFilterDialog() {
    JDialog filterDialog =
        new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter", true);
    filterDialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering advanced filter criteria
    JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField filterDateField = new JTextField(20);
    JTextField filterSymptomsField = new JTextField(20);
    JTextField filterDiagnosisField = new JTextField(20);
    JTextField filterDoctorIdField = new JTextField(20);
    JTextField filterPatientIdField = new JTextField(20);

    formPanel.add(new JLabel("Date contains:"));
    formPanel.add(filterDateField);
    formPanel.add(new JLabel("Symptoms contains:"));
    formPanel.add(filterSymptomsField);
    formPanel.add(new JLabel("Diagnosis contains:"));
    formPanel.add(filterDiagnosisField);
    formPanel.add(new JLabel("Doctor ID contains:"));
    formPanel.add(filterDoctorIdField);
    formPanel.add(new JLabel("Patient ID contains:"));
    formPanel.add(filterPatientIdField);

    // Button panel for filter actions (only Filter and Cancel buttons remain here)
    JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton filterButton = new JButton("Filter");
    JButton cancelButton = new JButton("Cancel");
    filterButtonPanel.add(filterButton);
    filterButtonPanel.add(cancelButton);

    // Action for Filter button: apply filters based on criteria
    filterButton.addActionListener(
        e -> {
          try {
            List<Visit> visits = visitService.getAllVisits();

            String dateFilter = filterDateField.getText().trim();
            String symptomsFilter = filterSymptomsField.getText().trim();
            String diagnosisFilter = filterDiagnosisField.getText().trim();
            String doctorIdFilter = filterDoctorIdField.getText().trim();
            String patientIdFilter = filterPatientIdField.getText().trim();

            if (!dateFilter.isEmpty()) {
              visits =
                  visits.stream()
                      .filter(
                          visit ->
                              visit
                                  .getDateOfVisit()
                                  .format(dateFormatter)
                                  .toLowerCase()
                                  .contains(dateFilter.toLowerCase()))
                      .toList();
            }
            if (!symptomsFilter.isEmpty()) {
              visits =
                  visits.stream()
                      .filter(
                          visit ->
                              visit
                                  .getSymptoms()
                                  .toLowerCase()
                                  .contains(symptomsFilter.toLowerCase()))
                      .toList();
            }
            if (!diagnosisFilter.isEmpty()) {
              visits =
                  visits.stream()
                      .filter(
                          visit ->
                              visit
                                  .getDiagnosis()
                                  .toLowerCase()
                                  .contains(diagnosisFilter.toLowerCase()))
                      .toList();
            }
            if (!doctorIdFilter.isEmpty()) {
              visits =
                  visits.stream()
                      .filter(
                          visit ->
                              visit
                                  .getDoctorId()
                                  .toLowerCase()
                                  .contains(doctorIdFilter.toLowerCase()))
                      .toList();
            }
            if (!patientIdFilter.isEmpty()) {
              visits =
                  visits.stream()
                      .filter(
                          visit ->
                              visit
                                  .getPatientId()
                                  .toLowerCase()
                                  .contains(patientIdFilter.toLowerCase()))
                      .toList();
            }
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
