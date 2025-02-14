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

    // Filtering fields
    private JTextField filterNameField;
    private JTextField filterSideEffectsField;
    private JTextField filterBenefitsField;

    public DrugPanel() {
        drugService = new DrugService();
        setLayout(new BorderLayout());

        // Top panel for filtering and refresh
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Name:"));
        filterNameField = new JTextField(10);
        filterPanel.add(filterNameField);

        filterPanel.add(new JLabel("Side Effects:"));
        filterSideEffectsField = new JTextField(10);
        filterPanel.add(filterSideEffectsField);

        filterPanel.add(new JLabel("Benefits:"));
        filterBenefitsField = new JTextField(10);
        filterPanel.add(filterBenefitsField);

        JButton filterButton = new JButton("Filter");
        filterPanel.add(filterButton);

        JButton clearFilterButton = new JButton("Clear Filters");
        filterPanel.add(clearFilterButton);

        topPanel.add(filterPanel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        topPanel.add(refreshButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Setup table for Drugs
        String[] columnNames = {"Drug ID", "Name", "Side Effects", "Benefits"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        drugTable = new JTable(tableModel);
        drugTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(drugTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel for add, edit, delete actions
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Drug");
        JButton editButton = new JButton("Edit Drug");
        JButton deleteButton = new JButton("Delete Drug");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners for buttons
        refreshButton.addActionListener(e -> loadDrugs());

        filterButton.addActionListener(e -> applyFilters());
        clearFilterButton.addActionListener(e -> {
            filterNameField.setText("");
            filterSideEffectsField.setText("");
            filterBenefitsField.setText("");
            loadDrugs();
        });

        addButton.addActionListener(e -> showDrugDialog(null));
        editButton.addActionListener(e -> {
            Drug selectedDrug = getSelectedDrug();
            if (selectedDrug != null) {
                showDrugDialog(selectedDrug);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a drug to edit");
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedDrug());

        // Initial load of drugs
        loadDrugs();
    }

    private void loadDrugs() {
        try {
            List<Drug> drugs = drugService.getAllDrugs();
            populateTable(drugs);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading drugs: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Drug> drugs) {
        tableModel.setRowCount(0);
        for (Drug drug : drugs) {
            tableModel.addRow(new Object[]{
                drug.getDrugId(),
                drug.getName(),
                drug.getSideEffects(),
                drug.getBenefits()
            });
        }
    }

    private Drug getSelectedDrug() {
        int row = drugTable.getSelectedRow();
        if (row != -1) {
            String drugId = (String) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            String sideEffects = (String) tableModel.getValueAt(row, 2);
            String benefits = (String) tableModel.getValueAt(row, 3);
            return new Drug(drugId, name, sideEffects, benefits);
        }
        return null;
    }

    private void showDrugDialog(Drug existingDrug) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existingDrug == null ? "Add Drug" : "Edit Drug",
                true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Form panel for entering drug details
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField sideEffectsField = new JTextField(20);
        JTextField benefitsField = new JTextField(20);

        if (existingDrug != null) {
            idField.setText(existingDrug.getDrugId());
            idField.setEditable(false);
            nameField.setText(existingDrug.getName());
            sideEffectsField.setText(existingDrug.getSideEffects());
            benefitsField.setText(existingDrug.getBenefits());
        }

        formPanel.add(new JLabel("Drug ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Side Effects:"));
        formPanel.add(sideEffectsField);
        formPanel.add(new JLabel("Benefits:"));
        formPanel.add(benefitsField);

        // Button panel for save and cancel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

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
                    JOptionPane.showMessageDialog(this, "Drug added successfully");
                } else {
                    drugService.updateDrug(drug);
                    JOptionPane.showMessageDialog(this, "Drug updated successfully");
                }
                loadDrugs();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedDrug() {
        int row = drugTable.getSelectedRow();
        if (row != -1) {
            String drugId = (String) tableModel.getValueAt(row, 0);
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this drug?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    drugService.deleteDrug(drugId);
                    loadDrugs();
                    JOptionPane.showMessageDialog(this, "Drug deleted successfully");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting drug: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a drug to delete");
        }
    }

    private void applyFilters() {
        try {
            String nameFilter = filterNameField.getText().trim();
            String sideEffectsFilter = filterSideEffectsField.getText().trim();
            String benefitsFilter = filterBenefitsField.getText().trim();

            List<Drug> drugs = drugService.getAllDrugs();

            // Apply filters using streams
            if (!nameFilter.isEmpty()) {
                drugs = drugs.stream()
                        .filter(drug -> drug.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                        .toList();
            }
            if (!sideEffectsFilter.isEmpty()) {
                drugs = drugs.stream()
                        .filter(drug -> drug.getSideEffects().toLowerCase().contains(sideEffectsFilter.toLowerCase()))
                        .toList();
            }
            if (!benefitsFilter.isEmpty()) {
                drugs = drugs.stream()
                        .filter(drug -> drug.getBenefits().toLowerCase().contains(benefitsFilter.toLowerCase()))
                        .toList();
            }

            populateTable(drugs);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error filtering drugs: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
