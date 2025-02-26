package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.models.Prescription;
import com.bougastefa.services.PrescriptionService;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PrescriptionPanel extends JPanel {
  private PrescriptionService prescriptionService;
  private DefaultTableModel tableModel;
  private JTable prescriptionTable;
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public PrescriptionPanel() {
    prescriptionService = new PrescriptionService();
    setLayout(new BorderLayout());

    // Replace custom button panels with ButtonPanel component
    ButtonPanel buttonPanel = new ButtonPanel("Prescription");
    buttonPanel.setAddButtonListener(e -> showPrescriptionDialog(null));
    buttonPanel.setEditButtonListener(
        e -> {
          Prescription selectedPrescription = getSelectedPrescription();
          if (selectedPrescription != null) {
            showPrescriptionDialog(selectedPrescription);
          } else {
            JOptionPane.showMessageDialog(this, "Please select a prescription to edit");
          }
        });
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
      String prescriptionId = (String) tableModel.getValueAt(row, 0);
      LocalDate date = LocalDate.parse((String) tableModel.getValueAt(row, 1), dateFormatter);
      String drugId = (String) tableModel.getValueAt(row, 2);
      String doctorId = (String) tableModel.getValueAt(row, 3);
      String patientId = (String) tableModel.getValueAt(row, 4);
      int dosage = (int) tableModel.getValueAt(row, 5);
      int duration = (int) tableModel.getValueAt(row, 6);
      String comment = (String) tableModel.getValueAt(row, 7);

      return new Prescription(
          prescriptionId, date, dosage, duration, comment, drugId, doctorId, patientId);
    }
    return null;
  }

  private void showPrescriptionDialog(Prescription existingPrescription) {
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingPrescription == null ? "Add Prescription" : "Edit Prescription",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering prescription details
    JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField idField = new JTextField(20);
    JTextField dateField = new JTextField(20);
    JTextField drugIdField = new JTextField(20);
    JTextField doctorIdField = new JTextField(20);
    JTextField patientIdField = new JTextField(20);
    JTextField dosageField = new JTextField(20);
    JTextField durationField = new JTextField(20);
    JTextField commentField = new JTextField(20);

    if (existingPrescription != null) {
      idField.setText(existingPrescription.getPrescriptionId());
      idField.setEditable(false);
      dateField.setText(existingPrescription.getDateOfPrescribe().format(dateFormatter));
      drugIdField.setText(existingPrescription.getDrugId());
      doctorIdField.setText(existingPrescription.getDoctorId());
      patientIdField.setText(existingPrescription.getPatientId());
      dosageField.setText(String.valueOf(existingPrescription.getDosage()));
      durationField.setText(String.valueOf(existingPrescription.getDuration()));
      commentField.setText(existingPrescription.getComment());
    }

    formPanel.add(new JLabel("Prescription ID:"));
    formPanel.add(idField);
    formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
    formPanel.add(dateField);
    formPanel.add(new JLabel("Drug ID:"));
    formPanel.add(drugIdField);
    formPanel.add(new JLabel("Doctor ID:"));
    formPanel.add(doctorIdField);
    formPanel.add(new JLabel("Patient ID:"));
    formPanel.add(patientIdField);
    formPanel.add(new JLabel("Dosage:"));
    formPanel.add(dosageField);
    formPanel.add(new JLabel("Duration (days):"));
    formPanel.add(durationField);
    formPanel.add(new JLabel("Comment:"));
    formPanel.add(commentField);

    // Button panel for save and cancel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            // Enforce PK constraint
            String id = idField.getText().trim();
            if (id.isEmpty()) {
              JOptionPane.showMessageDialog(
                  this,
                  "Prescription ID cannot be empty",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }
            String dateText = dateField.getText().trim();
            String drugId = drugIdField.getText().trim();
            String doctorId = doctorIdField.getText().trim();
            String patientId = patientIdField.getText().trim();
            String dosageText = dosageField.getText().trim();
            String durationText = durationField.getText().trim();
            String comment = commentField.getText().trim();

            // Handle empty fields
            LocalDate date =
                dateText.isEmpty() ? LocalDate.now() : LocalDate.parse(dateText, dateFormatter);
            int dosage = dosageText.isEmpty() ? 0 : Integer.parseInt(dosageText);
            int duration = durationText.isEmpty() ? 0 : Integer.parseInt(durationText);

            Prescription prescription =
                new Prescription(id, date, dosage, duration, comment, drugId, doctorId, patientId);

            if (existingPrescription == null) {
              prescriptionService.addPrescription(prescription);
              JOptionPane.showMessageDialog(this, "Prescription added successfully");
            } else {
              prescriptionService.updatePrescription(prescription);
              JOptionPane.showMessageDialog(this, "Prescription updated successfully");
            }
            loadPrescriptions();
            dialog.dispose();
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
    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void deleteSelectedPrescription() {
    int row = prescriptionTable.getSelectedRow();
    if (row != -1) {
      String prescriptionId = (String) tableModel.getValueAt(row, 0);
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this prescription?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          prescriptionService.deletePrescription(prescriptionId);
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
    } else {
      JOptionPane.showMessageDialog(this, "Please select a prescription to delete");
    }
  }

  private void showAdvancedFilterDialog() {
    JDialog filterDialog =
        new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter", true);
    filterDialog.setLayout(new BorderLayout(10, 10));

    JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField filterIdField = new JTextField(20);
    JTextField filterDateField = new JTextField(20);
    JTextField filterDrugIdField = new JTextField(20);
    JTextField filterDoctorIdField = new JTextField(20);
    JTextField filterPatientIdField = new JTextField(20);
    JTextField filterDosageField = new JTextField(20);
    JTextField filterDurationField = new JTextField(20);
    JTextField filterCommentField = new JTextField(20);

    formPanel.add(new JLabel("Prescription ID contains:"));
    formPanel.add(filterIdField);
    formPanel.add(new JLabel("Date contains:"));
    formPanel.add(filterDateField);
    formPanel.add(new JLabel("Drug ID contains:"));
    formPanel.add(filterDrugIdField);
    formPanel.add(new JLabel("Doctor ID contains:"));
    formPanel.add(filterDoctorIdField);
    formPanel.add(new JLabel("Patient ID contains:"));
    formPanel.add(filterPatientIdField);
    formPanel.add(new JLabel("Dosage contains:"));
    formPanel.add(filterDosageField);
    formPanel.add(new JLabel("Duration contains:"));
    formPanel.add(filterDurationField);
    formPanel.add(new JLabel("Comment contains:"));
    formPanel.add(filterCommentField);

    JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton filterButton = new JButton("Filter");
    JButton cancelButton = new JButton("Cancel");
    filterButtonPanel.add(filterButton);
    filterButtonPanel.add(cancelButton);

    filterButton.addActionListener(
        e -> {
          try {
            List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();

            String idFilter = filterIdField.getText().trim();
            String dateFilter = filterDateField.getText().trim();
            String drugIdFilter = filterDrugIdField.getText().trim();
            String doctorIdFilter = filterDoctorIdField.getText().trim();
            String patientIdFilter = filterPatientIdField.getText().trim();
            String dosageFilter = filterDosageField.getText().trim();
            String durationFilter = filterDurationField.getText().trim();
            String commentFilter = filterCommentField.getText().trim();

            prescriptions =
                prescriptions.stream()
                    .filter(
                        p ->
                            idFilter.isEmpty()
                                || p.getPrescriptionId()
                                    .toLowerCase()
                                    .contains(idFilter.toLowerCase()))
                    .filter(
                        p ->
                            dateFilter.isEmpty()
                                || p.getDateOfPrescribe()
                                    .format(dateFormatter)
                                    .contains(dateFilter))
                    .filter(
                        p ->
                            drugIdFilter.isEmpty()
                                || p.getDrugId().toLowerCase().contains(drugIdFilter.toLowerCase()))
                    .filter(
                        p ->
                            doctorIdFilter.isEmpty()
                                || p.getDoctorId()
                                    .toLowerCase()
                                    .contains(doctorIdFilter.toLowerCase()))
                    .filter(
                        p ->
                            patientIdFilter.isEmpty()
                                || p.getPatientId()
                                    .toLowerCase()
                                    .contains(patientIdFilter.toLowerCase()))
                    .filter(
                        p ->
                            dosageFilter.isEmpty()
                                || String.valueOf(p.getDosage()).contains(dosageFilter))
                    .filter(
                        p ->
                            durationFilter.isEmpty()
                                || String.valueOf(p.getDuration()).contains(durationFilter))
                    .filter(
                        p ->
                            commentFilter.isEmpty()
                                || p.getComment()
                                    .toLowerCase()
                                    .contains(commentFilter.toLowerCase()))
                    .toList();

            populateTable(prescriptions);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error filtering prescriptions: " + ex.getMessage(),
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
