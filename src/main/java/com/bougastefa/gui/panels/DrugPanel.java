package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.FilterableField;
import com.bougastefa.gui.components.FilterDialog;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.models.Drug;
import com.bougastefa.services.DrugService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DrugPanel extends JPanel {
    private final DrugService drugService;
    private final JTable drugTable;
    private final DefaultTableModel tableModel;

    public DrugPanel() {
        drugService = new DrugService();

        setLayout(new BorderLayout());

        // Create toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton addButton = new JButton("Add Drug");
        JButton editButton = new JButton("Edit Drug");
        JButton deleteButton = new JButton("Delete Drug");
        JButton filterButton = new JButton("Advanced Filter");

        addButton.addActionListener(e -> showDrugDialog(null));
        editButton.addActionListener(e -> editSelectedDrug());
        deleteButton.addActionListener(e -> deleteSelectedDrug());
        filterButton.addActionListener(e -> showAdvancedFilterDialog());

        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(filterButton);
      
        // Create table
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

        // Add components to panel
        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        loadDrugs();
    }

    private void loadDrugs() {
        try {
            List<Drug> drugs = drugService.getAllDrugs();
            populateTable(drugs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading drugs: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Drug> drugs) {
        tableModel.setRowCount(0);
        for (Drug drug : drugs) {
            Vector<String> row = new Vector<>();
            row.add(drug.getDrugId());
            row.add(drug.getName());
            row.add(drug.getSideEffects());
            row.add(drug.getBenefits());
            tableModel.addRow(row);
        }
    }

    private void showDrugDialog(Drug existingDrug) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existingDrug == null ? "Add Drug" : "Edit Drug",
                true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField idField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextArea sideEffectsArea = new JTextArea(3, 20);
        JTextArea benefitsArea = new JTextArea(3, 20);

        sideEffectsArea.setLineWrap(true);
        sideEffectsArea.setWrapStyleWord(true);
        benefitsArea.setLineWrap(true);
        benefitsArea.setWrapStyleWord(true);

        if (existingDrug != null) {
            idField.setText(existingDrug.getDrugId());
            nameField.setText(existingDrug.getName());
            sideEffectsArea.setText(existingDrug.getSideEffects());
            benefitsArea.setText(existingDrug.getBenefits());
            idField.setEditable(false);
        }

        addFormField(formPanel, "Drug ID:", idField, gbc, 0);
        addFormField(formPanel, "Name:", nameField, gbc, 1);
        addFormField(formPanel, "Side Effects:", new JScrollPane(sideEffectsArea), gbc, 2);
        addFormField(formPanel, "Benefits:", new JScrollPane(benefitsArea), gbc, 3);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                Drug drug = new Drug(
                        idField.getText(),
                        nameField.getText(),
                        sideEffectsArea.getText(),
                        benefitsArea.getText()
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

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String label, JComponent field,
                            GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void editSelectedDrug() {
        int row = drugTable.getSelectedRow();
        if (row != -1) {
            String drugId = (String) tableModel.getValueAt(row, 0);
            try {
                Drug drug = drugService.getDrugById(drugId);
                showDrugDialog(drug);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error editing drug: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a drug to edit");
        }
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

    private void showAdvancedFilterDialog() {
        List<FilterableField> fields = Arrays.asList(
            new FilterableField("Drug ID", "drugId"),
            new FilterableField("Name", "name"),
            new FilterableField("Side Effects", "sideEffects"),
            new FilterableField("Benefits", "benefits")
        );

        FilterDialog dialog = new FilterDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Advanced Filter",
            fields,
            this::applyFilters
        );
        
        dialog.setVisible(true);
    }

    private void applyFilters(Map<String, String> filters) {
        try {
            List<Drug> drugs = drugService.getAllDrugs();
            FilterResult<Drug> result = new FilterResult<>(drugs);
            
            if (filters.containsKey("drugId")) {
                result = result.filter(filters.get("drugId"), Drug::getDrugId);
            }
            if (filters.containsKey("name")) {
                result = result.filter(filters.get("name"), Drug::getName);
            }
            if (filters.containsKey("sideEffects")) {
                result = result.filter(filters.get("sideEffects"), Drug::getSideEffects);
            }
            if (filters.containsKey("benefits")) {
                result = result.filter(filters.get("benefits"), Drug::getBenefits);
            }

            populateTable(result.getResults());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error filtering drugs: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private Drug getSelectedDrug() {
        int row = drugTable.getSelectedRow();
        if (row != -1) {
            try {
                String drugId = (String) tableModel.getValueAt(row, 0);
                return drugService.getDrugById(drugId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error retrieving drug details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
    }}
