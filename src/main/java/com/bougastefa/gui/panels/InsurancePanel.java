package com.bougastefa.gui.panels;

import com.bougastefa.models.Insurance;
import com.bougastefa.services.InsuranceService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InsurancePanel extends JPanel {

    private InsuranceService insuranceService;
    private DefaultTableModel tableModel;
    private JTable insuranceTable;

    public InsurancePanel() {
        insuranceService = new InsuranceService();
        setLayout(new BorderLayout());

        // Create table with columns
        String[] columnNames = {"ID", "Company", "Address", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        insuranceTable = new JTable(tableModel);
        insuranceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        insuranceTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        insuranceTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        insuranceTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        insuranceTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        insuranceTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(insuranceTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Insurance");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Add listeners
        addButton.addActionListener(e -> showInsuranceDialog(null));
        editButton.addActionListener(e -> {
            int row = insuranceTable.getSelectedRow();
            if (row != -1) {
                showInsuranceDialog(getSelectedInsurance());
            } else {
                JOptionPane.showMessageDialog(this, "Please select an insurance to edit");
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedInsurance());
        refreshButton.addActionListener(e -> loadInsurances());

        // Initial load
        loadInsurances();
    }

    private void showInsuranceDialog(Insurance existingInsurance) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   existingInsurance == null ? "Add Insurance" : "Edit Insurance", 
                                   true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField(20);
        JTextField companyField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);

        // If editing, populate fields
        if (existingInsurance != null) {
            idField.setText(existingInsurance.getInsuranceId());
            idField.setEditable(false);
            companyField.setText(existingInsurance.getCompany());
            addressField.setText(existingInsurance.getAddress());
            phoneField.setText(existingInsurance.getPhone());
        }

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Company:"));
        formPanel.add(companyField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                Insurance insurance = new Insurance(
                    idField.getText(),
                    companyField.getText(),
                    addressField.getText(),
                    phoneField.getText()
                );
                
                if (existingInsurance == null) {
                    insuranceService.addInsurance(insurance);
                } else {
                    insuranceService.updateInsurance(insurance);
                }
                
                loadInsurances();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    existingInsurance == null ? "Insurance added successfully" : "Insurance updated successfully");
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

    private Insurance getSelectedInsurance() {
        int row = insuranceTable.getSelectedRow();
        if (row != -1) {
            return new Insurance(
                (String) tableModel.getValueAt(row, 0),
                (String) tableModel.getValueAt(row, 1),
                (String) tableModel.getValueAt(row, 2),
                (String) tableModel.getValueAt(row, 3)
            );
        }
        return null;
    }

    private void deleteSelectedInsurance() {
        int row = insuranceTable.getSelectedRow();
        if (row != -1) {
            String id = (String) tableModel.getValueAt(row, 0);
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this insurance?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    insuranceService.deleteInsurance(id);
                    loadInsurances();
                    JOptionPane.showMessageDialog(this, "Insurance deleted successfully");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error deleting insurance: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an insurance to delete");
        }
    }

    private void loadInsurances() {
        try {
            List<Insurance> insurances = insuranceService.getAllInsurances();
            tableModel.setRowCount(0);
            for (Insurance insurance : insurances) {
                tableModel.addRow(new Object[]{
                    insurance.getInsuranceId(),
                    insurance.getCompany(),
                    insurance.getAddress(),
                    insurance.getPhone()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading insurances: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
