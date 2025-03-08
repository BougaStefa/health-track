package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Drug;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.services.DrugService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

public class DrugPanel extends BasePanel<Drug> {
  private final DrugService drugService;

  public DrugPanel() {
    super("Drug");
    drugService = new DrugService();
    loadData();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] {"Drug ID", "Name", "Side Effects", "Benefits"};
  }

  @Override
  protected void loadData() {
    try {
      List<Drug> drugs = drugService.getAllDrugs();
      populateTable(drugs);
    } catch (Exception ex) {
      showError("Error loading drugs", ex);
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

  @Override
  protected Drug getSelectedItem() {
    int row = dataTable.getSelectedRow();
    if (row != -1) {
      try {
        String drugId = (String) tableModel.getValueAt(row, 0);
        Drug drug = drugService.getDrugById(drugId);
        if (drug == null) {
          showError("Could not find the selected drug", null);
        }
        return drug;
      } catch (Exception ex) {
        showError("Error retrieving drug details", ex);
      }
    } else {
      showInfo("Please select a drug first");
    }
    return null;
  }

  @Override
  protected void showAddDialog() {
    showDrugDialog(null);
  }

  @Override
  protected void showEditDialog(Drug drug) {
    showDrugDialog(drug);
  }

  private void showDrugDialog(Drug existingDrug) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
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
              showError("Drug ID cannot be empty", null);
              return;
            }

            // Create drug object
            Drug drug = new Drug(id, name, sideEffects, benefits);

            // Add or update drug
            if (existingDrug == null) {
              drugService.addDrug(drug);
              showInfo("Drug added successfully");
            } else {
              drugService.updateDrug(drug);
              showInfo("Drug updated successfully");
            }

            // Refresh display
            loadData();
          } catch (Exception ex) {
            showError("Error", ex);
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

  @Override
  protected void showAdvancedFilterDialog() {
    // Create a filter dialog with the relevant fields
    FormDialog.Builder builder = createFilterDialog("Advanced Filter", 
                                                   "drugId", "name", "sideEffects", "benefits");

    // Define filter action
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  @Override
  protected void applyFilters(Map<String, Object> formData) {
    try {
      List<Drug> drugs = drugService.getAllDrugs();
      
      // Define filter configurations for each field
      Map<String, Function<Drug, String>> filterMappings =
          Map.of(
              "drugId", Drug::getDrugId,
              "name", Drug::getName,
              "sideEffects", Drug::getSideEffects,
              "benefits", Drug::getBenefits);

      // Apply standard filters
      FilterResult<Drug> result = applyStandardFilters(drugs, formData, filterMappings);

      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering drugs", ex);
    }
  }

  @Override
  protected void deleteItem(Drug drug) throws Exception {
    drugService.deleteDrug(drug.getDrugId());
  }
}
