package com.bougastefa.gui.panels;

import com.bougastefa.models.Drug;
import com.bougastefa.services.DrugService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DrugPanel extends JPanel {

    private DrugService drugService;
    private DefaultTableModel tableModel;
    private JTable drugTable;

    public DrugPanel() {
        drugService = new DrugService();
        setLayout(new BorderLayout());

        // Create table with columns
        String[] columnNames = {"ID", "Name", "Side Effects", "Benefits"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        drugTable = new JTable(tableModel);
        drugTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        drugTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        drugTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        drugTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        drugTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        drugTable.getColumnModel().getColumn(3).setPreferredWidth(200);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(drugTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Drug");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Add listeners
        addButton.addActionListener(e -> showDrugDialog(null));
        editButton.addActionListener(e -> {
            int row = drugTable.getSelectedRow();
            if (row != -1) {
                showDrugDialog(getSelectedDrug());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a drug to edit");
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedDrug());
        refreshButton.addActionListener(e -> loadDrugs());

        // Initial load
        loadDrugs();
    }

    private void showDrugDialog(Drug existingDrug) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   existingDrug == null ? "Add Drug" : "Edit Drug", 
                                   true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField sideEffectsField = new JTextField(20);
        JTextField benefitsField = new JTextField(20);

        // If editing, populate fields
        if (existingDrug != null) {
            idField.setText(existingDrug.getDrugId());
            idField.setEditable(false);
            nameField.setText(existingDrug.getName());
            sideEffectsField.setText(existingDrug.getSideEffects());
            benefitsField.setText(existingDrug.getBenefits());
        }

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Side Effects:"));
        formPanel.add(sideEffectsField);
        formPanel.add(new JLabel("Benefits:"));
        formPanel.add(benefitsField);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                Drug drug = new Drug(
                    idField.getText(),
                    nameField.getText(),
                    sideEffectsField.getText(),
                    benefitsField.getText()
                );
                
                if (existingDrug == null) {
                    drugService.addDrug(drug);
                } else {
                    drugService.updateDrug(drug);
                }
                
                loadDrugs();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, 
                    existingDrug == null ? "Drug added successfully" : "Drug updated successfully");
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

    private Drug getSelectedDrug() {
        int row = drugTable.getSelectedRow();
        if (row != -1) {
            return new Drug(
                (String) tableModel.getValueAt(row, 0),
                (String) tableModel.getValueAt(row, 1),
                (String) tableModel.getValueAt(row, 2),
                (String) tableModel.getValueAt(row, 3)
            );
        }
        return null;
    }

    private void deleteSelectedDrug() {
        int row = drugTable.getSelectedRow();
        if (row != -1) {
            String id = (String) tableModel.getValueAt(row, 0);
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this drug?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    drugService.deleteDrug(id);
                    loadDrugs();
                    JOptionPane.showMessageDialog(this, "Drug deleted successfully");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error deleting drug: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a drug to delete");
        }
    }

    private void loadDrugs() {
        try {
            List<Drug> drugs = drugService.getAllDrugs();
            tableModel.setRowCount(0);
            for (Drug drug : drugs) {
                tableModel.addRow(new Object[]{
                    drug.getDrugId(),
                    drug.getName(),
                    drug.getSideEffects(),
                    drug.getBenefits()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading drugs: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
