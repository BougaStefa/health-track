package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Drug;
import com.bougastefa.services.DrugService;
import com.bougastefa.utils.FieldLengthConstants;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

/**
 * Panel for managing Drug entities in the application. This panel extends BasePanel to provide
 * specialized functionality for drug management, including adding, editing, deleting, and filtering
 * drugs. It displays drugs in a table and provides forms for editing drug details such as name,
 * side effects, and benefits.
 */
public class DrugPanel extends BasePanel<Drug> {
  /** Service object that handles business logic and data operations for drugs */
  private final DrugService drugService;

  /**
   * Constructs a new DrugPanel. Initializes the panel with the "Drug" title and loads initial drug
   * data.
   */
  public DrugPanel() {
    super("Drug");
    drugService = new DrugService();
    loadData();
  }

  /**
   * {@inheritDoc} Defines the column names for the drug table, showing ID, name, side effects and
   * benefits.
   */
  @Override
  protected String[] getColumnNames() {
    return new String[] {"Drug ID", "Name", "Side Effects", "Benefits"};
  }

  /**
   * {@inheritDoc} Loads all drugs from the service and populates the table with the data. Handles
   * any exceptions that may occur during the data loading process.
   */
  @Override
  protected void loadData() {
    try {
      List<Drug> drugs = drugService.getAllDrugs();
      populateTable(drugs);
    } catch (Exception ex) {
      showError("Error loading drugs", ex);
    }
  }

  /**
   * Populates the table with drug data. Clears the existing table data and adds each drug as a new
   * row.
   *
   * @param drugs The list of drugs to display in the table
   */
  private void populateTable(List<Drug> drugs) {
    tableModel.setRowCount(0);
    for (Drug drug : drugs) {
      tableModel.addRow(
          new Object[] {
            drug.getDrugId(), drug.getName(), drug.getSideEffects(), drug.getBenefits()
          });
    }
  }

  /**
   * {@inheritDoc} Retrieves the currently selected drug from the table. Maps the selected row to a
   * Drug object by using the drugId to look up the full object.
   *
   * @return The selected Drug object, or null if no row is selected or an error occurs
   */
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

  /**
   * {@inheritDoc} Shows a dialog for adding a new drug. Calls showDrugDialog with null to indicate
   * a new drug is being created.
   */
  @Override
  protected void showAddDialog() {
    showDrugDialog(null);
  }

  /**
   * {@inheritDoc} Shows a dialog for editing an existing drug. Calls showDrugDialog with the drug
   * object to pre-populate the form.
   *
   * @param drug The drug to edit
   */
  @Override
  protected void showEditDialog(Drug drug) {
    showDrugDialog(drug);
  }

  /**
   * Creates and displays a form dialog for adding or editing a drug. Sets up the form with fields
   * for ID, name, side effects, and benefits, and handles form submission by creating or updating a
   * drug in the database.
   *
   * @param existingDrug The drug to edit, or null if creating a new drug
   */
  private void showDrugDialog(Drug existingDrug) {
    FormDialog.Builder builder =
        new FormDialog.Builder(getParentFrame(), existingDrug == null ? "Add Drug" : "Edit Drug");

    // Add form fields with initial values if editing and display max length info
    builder.addTextField(
        "Drug ID (max " + FieldLengthConstants.DRUG_ID_MAX_LENGTH + " chars)",
        "drugId",
        existingDrug != null ? existingDrug.getDrugId() : "");

    builder.addTextField(
        "Name (max " + FieldLengthConstants.DRUG_NAME_MAX_LENGTH + " chars)",
        "name",
        existingDrug != null ? existingDrug.getName() : "");

    builder.addTextField(
        "Side Effects (max " + FieldLengthConstants.DRUG_SIDE_EFFECTS_MAX_LENGTH + " chars)",
        "sideEffects",
        existingDrug != null ? existingDrug.getSideEffects() : "");
    builder.addTextField(
        "Benefits (max " + FieldLengthConstants.DRUG_BENEFITS_MAX_LENGTH + " chars)",
        "benefits",
        existingDrug != null ? existingDrug.getBenefits() : "");

    // Define save action that will be called when form is submitted
    builder.onSave(
        formData -> {
          try {
            // Extract form data from the submitted form
            String drugId = (String) formData.get("drugId");
            String name = (String) formData.get("name");
            String benefits = (String) formData.get("benefits");
            String sideEffects = (String) formData.get("sideEffects");

            // Validate required fields
            if (drugId.isEmpty()) {
              showError("Drug ID is a required field", null);
              return;
            }

            // Validate field lengths
            if (drugId.length() > FieldLengthConstants.DRUG_ID_MAX_LENGTH) {
              showError(
                  "Drug ID exceeds maximum length of "
                      + FieldLengthConstants.DRUG_ID_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (name.length() > FieldLengthConstants.DRUG_NAME_MAX_LENGTH) {
              showError(
                  "Drug name exceeds maximum length of "
                      + FieldLengthConstants.DRUG_NAME_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (benefits != null
                && benefits.length() > FieldLengthConstants.DRUG_BENEFITS_MAX_LENGTH) {
              showError(
                  "Benefits exceed maximum length of "
                      + FieldLengthConstants.DRUG_BENEFITS_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            if (sideEffects != null
                && sideEffects.length() > FieldLengthConstants.DRUG_SIDE_EFFECTS_MAX_LENGTH) {
              showError(
                  "Side effects exceed maximum length of "
                      + FieldLengthConstants.DRUG_SIDE_EFFECTS_MAX_LENGTH
                      + " characters",
                  null);
              return;
            }

            // Create drug object
            Drug drug = new Drug(drugId, name, sideEffects, benefits);

            // Add or update drug
            if (existingDrug == null) {
              drugService.addDrug(drug);
              showInfo("Drug added successfully");
            } else {
              drugService.updateDrug(drug);
              showInfo("Drug updated successfully");
            }

            // Refresh data in the table
            loadData();

          } catch (IllegalArgumentException e) {
            showError(e.getMessage(), null);
          } catch (Exception ex) {
            showError("Error: " + ex.getMessage(), ex);
          }
        });

    // Build and show the dialog
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

  /**
   * {@inheritDoc} Shows a dialog for advanced filtering of drugs. Creates a filter form with fields
   * corresponding to all drug properties.
   */
  @Override
  protected void showAdvancedFilterDialog() {
    // Create a filter dialog with the relevant fields
    FormDialog.Builder builder =
        createFilterDialog("Advanced Filter", "drugId", "name", "sideEffects", "benefits");

    // Define filter action to be called when filter is applied
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc} Applies filter criteria to the list of drugs and updates the table. Uses the
   * FilterResult helper class to apply string-based filtering to all drug fields based on the
   * criteria provided in the form data.
   *
   * @param formData Map of field names to filter values from the filter dialog
   */
  @Override
  protected void applyFilters(Map<String, Object> formData) {
    try {
      // Get all drugs to start with
      List<Drug> drugs = drugService.getAllDrugs();

      // Define filter configurations for each field
      Map<String, Function<Drug, String>> filterMappings =
          Map.of(
              "drugId", Drug::getDrugId,
              "name", Drug::getName,
              "sideEffects", Drug::getSideEffects,
              "benefits", Drug::getBenefits);

      // Apply standard filters for each field using the helper method from BasePanel
      FilterResult<Drug> result = applyStandardFilters(drugs, formData, filterMappings);

      // Update the table with the filtered results
      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering drugs", ex);
    }
  }

  /**
   * {@inheritDoc} Deletes a drug from the system.
   *
   * @param drug The drug to delete
   * @throws Exception If deletion fails
   */
  @Override
  protected void deleteItem(Drug drug) throws Exception {
    drugService.deleteDrug(drug.getDrugId());
  }
}
