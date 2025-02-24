package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterDialog;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FilterableField;
import com.bougastefa.models.Drug;
import com.bougastefa.services.DrugService;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DrugPanel extends JPanel {
  private final DrugService drugService;
  private DefaultTableModel tableModel;
  private JTable drugTable;

  public DrugPanel() {
    drugService = new DrugService();
    setLayout(new BorderLayout());

    // Add button panel
    ButtonPanel buttonPanel = new ButtonPanel();
    buttonPanel.setAddButtonListener(e -> showDrugDialog(null));
    buttonPanel.setEditButtonListener(e -> editSelectedDrug());
    buttonPanel.setDeleteButtonListener(e -> deleteSelectedDrug());
    buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
    buttonPanel.setRefreshButtonListener(e -> loadDrugs());

    add(buttonPanel, BorderLayout.NORTH);

    // Setup table for Drugs
    setupDrugTable();
    JScrollPane scrollPane = new JScrollPane(drugTable);
    add(scrollPane, BorderLayout.CENTER);

    // Initial load of drugs
    loadDrugs();
  }

  private void setupDrugTable() {
    String[] columnNames = {"Drug ID", "Name", "Side Effects", "Benefits"};
    tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };
    drugTable = new JTable(tableModel);
    drugTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  private void loadDrugs() {
    try {
      List<Drug> drugs = drugService.getAllDrugs();
      populateTable(drugs);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading drugs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void populateTable(List<Drug> drugs) {
    tableModel.setRowCount(0);
    for (Drug drug : drugs) {
      tableModel.addRow(
          new Object[] {
            drug.getDrugId(), drug.getName(), drug.getSideEffects(), drug.getBenefits()
          });
    }
  }

  private Drug getSelectedDrug() {
    int row = drugTable.getSelectedRow();
    if (row != -1) {
      try {
        String drugId = (String) tableModel.getValueAt(row, 0);
        Drug drug = drugService.getDrugById(drugId);
        if (drug == null) {
          JOptionPane.showMessageDialog(
              this, "Could not find the selected drug", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return drug;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "Error retrieving drug details: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(
          this, "Please select a drug first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }
    return null;
  }

  private void editSelectedDrug() {
    Drug drug = getSelectedDrug();
    if (drug != null) {
      showDrugDialog(drug);
    }
  }

  private void deleteSelectedDrug() {
    Drug drug = getSelectedDrug();
    if (drug != null) {
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this drug?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          drugService.deleteDrug(drug.getDrugId());
          loadDrugs();
          JOptionPane.showMessageDialog(this, "Drug deleted successfully");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
              this, "Error deleting drug: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  private void showDrugDialog(Drug existingDrug) {
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingDrug == null ? "Add Drug" : "Edit Drug",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering drug details
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

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

    addFormField(formPanel, "Drug ID:", idField, gbc, 0);
    addFormField(formPanel, "Name:", nameField, gbc, 1);
    addFormField(formPanel, "Side Effects:", sideEffectsField, gbc, 2);
    addFormField(formPanel, "Benefits:", benefitsField, gbc, 3);

    // Button panel for save and cancel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            Drug drug =
                new Drug(
                    idField.getText(),
                    nameField.getText(),
                    sideEffectsField.getText(),
                    benefitsField.getText());
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
            new FilterableField("Drug ID", "drugId"),
            new FilterableField("Name", "name"),
            new FilterableField("Side Effects", "sideEffects"),
            new FilterableField("Benefits", "benefits"));

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
      JOptionPane.showMessageDialog(
          this, "Error filtering drugs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
