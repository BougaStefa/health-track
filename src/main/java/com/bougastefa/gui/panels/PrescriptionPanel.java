package com.bougastefa.gui.panels;

import com.bougastefa.models.Prescription;
import com.bougastefa.services.PrescriptionService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PrescriptionPanel extends JPanel {

    private PrescriptionService prescriptionService;
    private DefaultTableModel tableModel;
    private JTable prescriptionTable;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public PrescriptionPanel() {
        prescriptionService = new PrescriptionService();
        setLayout(new BorderLayout());

        // Create table with columns
        String[] columnNames = {"ID", "Date", "Dosage", "Duration", "Comment", "Drug ID", "Doctor ID", "Patient ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        prescriptionTable = new JTable(tableModel);
        prescriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        prescriptionTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        prescriptionTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        prescriptionTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        prescriptionTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        prescriptionTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        prescriptionTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        prescriptionTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        prescriptionTable.getColumnModel().getColumn(7).setPreferredWidth(70);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(prescriptionTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Prescription");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Add listeners
        addButton.addActionListener(e -> showPrescriptionDialog(null));
        editButton.addActionListener(e -> {
            int row = prescriptionTable.getSelectedRow();
            if (row != -1) {
                showPrescriptionDialog(getSelectedPrescription());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a prescription to edit");
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedPrescription());
        refreshButton.addActionListener(e -> loadPrescriptions());

        // Initial load
        loadPrescriptions();
    }

    private void showPrescriptionDialog(Prescription existingPrescription) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   existingPrescription == null ? "Add Prescription" : "Edit Prescription", 
                                   true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField(20);
        JTextField dateField = new JTextField(20);
        JTextField dosageField = new JTextField(20);
        JTextField durationField = new JTextField(20);
        JTextField commentField = new JTextField(20);
        JTextField drugIdField = new JTextField(20);
        JTextField doctorIdField = new JTextField(20);
        JTextField patientIdField = new JTextField(20);

        // If editing, populate fields
        if (existingPrescription != null) {
            idField.setText(existingPrescription.getPrescriptionId());
            idField.setEditable(false);
            dateField.setText(existingPrescription.getDateOfPrescribe().format(dateFormatter));
            dosageField.setText(String.valueOf(existingPrescription.getDosage()));
            durationField.setText(String.valueOf(existingPrescription.getDuration()));
            commentField.setText(existingPrescription.getComment());
            drugIdField.setText(existingPrescription.getDrugId());
            doctorIdField.setText(existingPrescription.getDoctorId());
            patientIdField.setText(existingPrescription.getPatientId());
        }

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Dosage:"));
        formPanel.add(dosageField);
        formPanel.add(new JLabel("Duration (days):"));
        formPanel.add(durationField);
        formPanel.add(new JLabel("Comment:"));
        formPanel.add(commentField);
        formPanel.add(new JLabel("Drug ID:"));
        formPanel.add(drugIdField);
        formPanel.add(new JLabel("Doctor ID:"));
        formPanel.add(doctorIdField);
        formPanel.add(new JLabel("Patient ID:"));
        formPanel.add(patientIdField);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                LocalDate prescribedDate = LocalDate.parse(dateField.getText(), dateFormatter);
                int dosage = Integer.parseInt(dosageField.getText());
                int duration = Integer.parseInt(durationField.getText());
                
                Prescription prescription = new Prescription(
                    idField.getText(),
                    prescribedDate,
                    dosage,
                    duration,
                    commentField.getText(),
                    drugIdField.getText(),
                    doctorIdField.getText(),
                    patientIdField.getText()
                );
                
                if (existingPrescription == null) {
                    prescriptionService.addPrescription(prescription);
                } else {
                    prescriptionService.updatePrescription(prescription);
                }
                
                loadPrescriptions();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    existingPrescription == null ? "Prescription added successfully" : "Prescription updated successfully");
            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD format.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this,
                    "Invalid number format for dosage or duration. Please enter valid numbers.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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

    private Prescription getSelectedPrescription() {
        int row = prescriptionTable.getSelectedRow();
        if (row != -1) {
            return new Prescription(
                (String) tableModel.getValueAt(row, 0),
                LocalDate.parse((String) tableModel.getValueAt(row, 1), dateFormatter),
                Integer.parseInt(tableModel.getValueAt(row, 2).toString()),
                Integer.parseInt(tableModel.getValueAt(row, 3).toString()),
                (String) tableModel.getValueAt(row, 4),
                (String) tableModel.getValueAt(row, 5),
                (String) tableModel.getValueAt(row, 6),
                (String) tableModel.getValueAt(row, 7)
            );
        }
        return null;
    }

    private void deleteSelectedPrescription() {
        int row = prescriptionTable.getSelectedRow();
        if (row != -1) {
            String id = (String) tableModel.getValueAt(row, 0);
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this prescription?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    prescriptionService.deletePrescription(id);
                    loadPrescriptions();
                    JOptionPane.showMessageDialog(this, "Prescription deleted successfully");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error deleting prescription: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a prescription to delete");
        }
    }

    private void loadPrescriptions() {
        try {
            List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
            tableModel.setRowCount(0);
            for (Prescription prescription : prescriptions) {
                tableModel.addRow(new Object[]{
                    prescription.getPrescriptionId(),
                    prescription.getDateOfPrescribe().format(dateFormatter),
                    prescription.getDosage(),
                    prescription.getDuration(),
                    prescription.getComment(),
                    prescription.getDrugId(),
                    prescription.getDoctorId(),
                    prescription.getPatientId()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading prescriptions: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
