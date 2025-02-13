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

    // Create table with columns
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

    // Set column widths
    visitTable.getColumnModel().getColumn(0).setPreferredWidth(100);
    visitTable.getColumnModel().getColumn(1).setPreferredWidth(200);
    visitTable.getColumnModel().getColumn(2).setPreferredWidth(200);
    visitTable.getColumnModel().getColumn(3).setPreferredWidth(80);
    visitTable.getColumnModel().getColumn(4).setPreferredWidth(80);

    // Add table to scroll pane
    JScrollPane scrollPane = new JScrollPane(visitTable);
    add(scrollPane, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel();
    JButton addButton = new JButton("Add Visit");
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    JButton refreshButton = new JButton("Refresh");

    buttonPanel.add(addButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(refreshButton);

    add(buttonPanel, BorderLayout.NORTH);

    // Add listeners
    addButton.addActionListener(e -> showVisitDialog(null));
    editButton.addActionListener(
        e -> {
          int row = visitTable.getSelectedRow();
          if (row != -1) {
            showVisitDialog(getSelectedVisit());
          } else {
            JOptionPane.showMessageDialog(this, "Please select a visit to edit");
          }
        });
    deleteButton.addActionListener(e -> deleteSelectedVisit());
    refreshButton.addActionListener(e -> loadVisits());

    // Initial load
    loadVisits();
  }

  private void showVisitDialog(Visit existingVisit) {
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingVisit == null ? "Add Visit" : "Edit Visit",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Create form panel
    JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField dateField = new JTextField(20);
    JTextField symptomsField = new JTextField(20);
    JTextField diagnosisField = new JTextField(20);
    JTextField doctorIdField = new JTextField(20);
    JTextField patientIdField = new JTextField(20);

    // If editing, populate fields
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

    // Button panel
    JPanel buttonPanel = new JPanel();
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

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
            } else {
              visitService.updateVisit(visit);
            }

            loadVisits();
            dialog.dispose();
            JOptionPane.showMessageDialog(
                this,
                existingVisit == null ? "Visit added successfully" : "Visit updated successfully");
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

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
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
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
              this, "Error deleting visit: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    } else {
      JOptionPane.showMessageDialog(this, "Please select a visit to delete");
    }
  }

  private void loadVisits() {
    try {
      List<Visit> visits = visitService.getAllVisits();
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
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          this, "Error loading visits: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
