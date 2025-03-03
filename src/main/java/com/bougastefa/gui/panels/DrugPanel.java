package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Drug;
import com.bougastefa.services.DrugService;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    ButtonPanel buttonPanel = new ButtonPanel("Drug");
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
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingDrug == null ? "Add Drug" : "Edit Drug");

    // Add form fields with initial values if editing
    String idValue = existingDrug != null ? existingDrug.getDrugId() : "";
    String nameValue = existingDrug != null ? existingDrug.getName() : "";
    String sideEffectsValue = existingDrug != null ? existingDrug.getSideEffects() : "";
    String benefitsValue = existingDrug != null ? existingDrug.getBenefits() : "";

    builder.addTextField("Drug ID", "drugId", idValue);
    builder.addTextField("Name", "name", nameValue);
    builder.addTextField("Side Effects", "sideEffects", sideEffectsValue);
    builder.addTextField("Benefits", "benefits", benefitsValue);

    // Define save action
    builder.onSave(
        formData -> {
          try {
            // Extract form data
            String id = (String) formData.get("drugId");
            String name = (String) formData.get("name");
            String sideEffects = (String) formData.get("sideEffects");
            String benefits = (String) formData.get("benefits");

            // Validate drug ID
            if (id.isEmpty()) {
              JOptionPane.showMessageDialog(
                  this, "Drug ID cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
              return;
            }

            // Create drug object
            Drug drug = new Drug(id, name, sideEffects, benefits);

            // Add or update drug
            if (existingDrug == null) {
              drugService.addDrug(drug);
              JOptionPane.showMessageDialog(this, "Drug added successfully");
            } else {
              drugService.updateDrug(drug);
              JOptionPane.showMessageDialog(this, "Drug updated successfully");
            }

            // Refresh display
            loadDrugs();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable ID field if editing
    if (existingDrug != null) {
      JComponent idField = dialog.getField("drugId");
      if (idField instanceof JTextField) {
        ((JTextField) idField).setEditable(false);
      }
    }

    dialog.setVisible(true);
  }

  private void showAdvancedFilterDialog() {
    // Create a FormDialog.Builder for the filter dialog
    FormDialog.Builder builder =
        new FormDialog.Builder((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter");

    // Add filter fields
    builder.addTextField("Drug ID", "drugId");
    builder.addTextField("Name", "name");
    builder.addTextField("Side Effects", "sideEffects");
    builder.addTextField("Benefits", "benefits");

    // Define filter action
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  private void applyFilters(Map<String, Object> formData) {
    try {
      List<Drug> drugs = drugService.getAllDrugs();
      FilterResult<Drug> result = new FilterResult<>(drugs);

      // Define filter configurations for each field
      Map<String, Function<Drug, String>> filterMappings =
          Map.of(
              "drugId", Drug::getDrugId,
              "name", Drug::getName,
              "sideEffects", Drug::getSideEffects,
              "benefits", Drug::getBenefits);

      // Apply filters for non-empty fields
      for (Map.Entry<String, Function<Drug, String>> entry : filterMappings.entrySet()) {
        String fieldName = entry.getKey();
        Function<Drug, String> getter = entry.getValue();

        String filterValue = (String) formData.get(fieldName);
        if (filterValue != null && !filterValue.isEmpty()) {
          result = result.filter(filterValue, getter);
        }
      }

      populateTable(result.getResults());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error filtering drugs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
