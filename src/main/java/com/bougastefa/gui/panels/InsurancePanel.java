package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Insurance;
import com.bougastefa.services.InsuranceService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

/**
 * Panel for managing Insurance entities in the application.
 * This panel extends BasePanel to provide specialized functionality for insurance provider management,
 * including adding, editing, deleting, and filtering insurance providers. The panel displays
 * insurance providers in a table and provides forms for editing their details.
 */
public class InsurancePanel extends BasePanel<Insurance> {
  /** Service object that handles business logic and data operations for insurance providers */
  private final InsuranceService insuranceService;

  /**
   * Constructs a new InsurancePanel.
   * Initializes the panel with the "Insurance" title and loads initial insurance data.
   */
  public InsurancePanel() {
    super("Insurance");
    insuranceService = new InsuranceService();
    loadData();
  }

  /**
   * {@inheritDoc}
   * Defines the column names for the insurance table, displaying ID, company name, address and phone.
   */
  @Override
  protected String[] getColumnNames() {
    return new String[] {"Insurance ID", "Company", "Address", "Phone"};
  }

  /**
   * {@inheritDoc}
   * Loads all insurance providers from the service and populates the table with the data.
   * Handles any exceptions that may occur during the data loading process.
   */
  @Override
  protected void loadData() {
    try {
      List<Insurance> insurances = insuranceService.getAllInsurances();
      populateTable(insurances);
    } catch (Exception ex) {
      showError("Error loading insurances", ex);
    }
  }

  /**
   * Populates the table with insurance provider data.
   * Clears the existing table data and adds each insurance provider as a new row.
   * 
   * @param insurances The list of insurance providers to display in the table
   */
  private void populateTable(List<Insurance> insurances) {
    tableModel.setRowCount(0);
    for (Insurance insurance : insurances) {
      tableModel.addRow(
          new Object[] {
            insurance.getInsuranceId(),
            insurance.getCompany(),
            insurance.getAddress(),
            insurance.getPhone()
          });
    }
  }

  /**
   * {@inheritDoc}
   * Retrieves the currently selected insurance provider from the table.
   * Maps the selected row to an Insurance object by using the insuranceId to look up the full object.
   * 
   * @return The selected Insurance object, or null if no row is selected or an error occurs
   */
  @Override
  protected Insurance getSelectedItem() {
    int row = dataTable.getSelectedRow();
    if (row != -1) {
      try {
        String insuranceId = (String) tableModel.getValueAt(row, 0);
        Insurance insurance = insuranceService.getInsuranceById(insuranceId);
        if (insurance == null) {
          showError("Could not find the selected insurance", null);
        }
        return insurance;
      } catch (Exception ex) {
        showError("Error retrieving insurance details", ex);
      }
    } else {
      showInfo("Please select an insurance first");
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * Shows a dialog for adding a new insurance provider.
   * Calls showInsuranceDialog with null to indicate a new insurance provider is being created.
   */
  @Override
  protected void showAddDialog() {
    showInsuranceDialog(null);
  }

  /**
   * {@inheritDoc}
   * Shows a dialog for editing an existing insurance provider.
   * Calls showInsuranceDialog with the insurance object to pre-populate the form.
   * 
   * @param insurance The insurance provider to edit
   */
  @Override
  protected void showEditDialog(Insurance insurance) {
    showInsuranceDialog(insurance);
  }

  /**
   * Creates and displays a form dialog for adding or editing an insurance provider.
   * Sets up the form with fields for ID, company name, address, and phone,
   * and handles form submission by creating or updating an insurance provider in the database.
   * 
   * @param existingInsurance The insurance provider to edit, or null if creating a new one
   */
  private void showInsuranceDialog(Insurance existingInsurance) {
    // Create FormDialog.Builder with appropriate title based on operation type
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
            existingInsurance == null ? "Add Insurance" : "Edit Insurance");

    // Add form fields with initial values if editing
    String idValue = existingInsurance != null ? existingInsurance.getInsuranceId() : "";
    String companyValue = existingInsurance != null ? existingInsurance.getCompany() : "";
    String addressValue = existingInsurance != null ? existingInsurance.getAddress() : "";
    String phoneValue = existingInsurance != null ? existingInsurance.getPhone() : "";

    // Add text fields for all insurance properties
    builder.addTextField("Insurance ID", "insuranceId", idValue);
    builder.addTextField("Company", "company", companyValue);
    builder.addTextField("Address", "address", addressValue);
    builder.addTextField("Phone", "phone", phoneValue);

    // Define save action that will be called when form is submitted
    builder.onSave(
        formData -> {
          try {
            // Extract form data from the submitted form
            String id = (String) formData.get("insuranceId");
            String company = (String) formData.get("company");
            String address = (String) formData.get("address");
            String phone = (String) formData.get("phone");

            // Validate insurance ID
            if (id.isEmpty()) {
              showError("Insurance ID cannot be empty", null);
              return;
            }

            // Create insurance object with the form data
            Insurance insurance = new Insurance(id, company, address, phone);

            // Add or update insurance based on whether we're editing or creating
            if (existingInsurance == null) {
              insuranceService.addInsurance(insurance);
              showInfo("Insurance added successfully");
            } else {
              insuranceService.updateInsurance(insurance);
              showInfo("Insurance updated successfully");
            }

            // Refresh display to show changes
            loadData();
          } catch (Exception ex) {
            showError("Error", ex);
          }
        });

    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable ID field if editing (since ID is the primary key and shouldn't change)
    if (existingInsurance != null) {
      JComponent idField = dialog.getField("insuranceId");
      if (idField instanceof JTextField) {
        ((JTextField) idField).setEditable(false);
      }
    }

    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc}
   * Shows a dialog for advanced filtering of insurance providers.
   * Creates a filter form with fields corresponding to all insurance properties.
   */
  @Override
  protected void showAdvancedFilterDialog() {
    // Create filter dialog with the relevant fields
    FormDialog.Builder builder = createFilterDialog(
        "Advanced Filter", "insuranceId", "company", "address", "phone");

    // Define filter action to be called when filter is applied
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc}
   * Applies filter criteria to the list of insurance providers and updates the table.
   * Uses the FilterResult helper class to apply string-based filtering to 
   * all insurance fields based on the criteria provided in the form data.
   * 
   * @param formData Map of field names to filter values from the filter dialog
   */
  @Override
  protected void applyFilters(Map<String, Object> formData) {
    try {
      // Get all insurance providers to start with
      List<Insurance> insurances = insuranceService.getAllInsurances();
      
      // Define filter configurations for each field
      Map<String, Function<Insurance, String>> filterMappings =
          Map.of(
              "insuranceId", Insurance::getInsuranceId,
              "company", Insurance::getCompany,
              "address", Insurance::getAddress,
              "phone", Insurance::getPhone);

      // Apply standard filters for each field using the helper method from BasePanel
      FilterResult<Insurance> result = applyStandardFilters(insurances, formData, filterMappings);

      // Update the table with the filtered results
      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering insurances", ex);
    }
  }

  /**
   * {@inheritDoc}
   * Deletes an insurance provider from the system.
   * Note: This operation may fail if there are patients associated with this insurance provider,
   * as there will be a foreign key constraint in the database.
   * 
   * @param insurance The insurance provider to delete
   * @throws Exception If deletion fails
   */
  @Override
  protected void deleteItem(Insurance insurance) throws Exception {
    insuranceService.deleteInsurance(insurance.getInsuranceId());
  }
}

