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

public class InsurancePanel extends BasePanel<Insurance> {
  private final InsuranceService insuranceService;

  public InsurancePanel() {
    super("Insurance");
    insuranceService = new InsuranceService();
    loadData();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] {"Insurance ID", "Company", "Address", "Phone"};
  }

  @Override
  protected void loadData() {
    try {
      List<Insurance> insurances = insuranceService.getAllInsurances();
      populateTable(insurances);
    } catch (Exception ex) {
      showError("Error loading insurances", ex);
    }
  }

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

  @Override
  protected void showAddDialog() {
    showInsuranceDialog(null);
  }

  @Override
  protected void showEditDialog(Insurance insurance) {
    showInsuranceDialog(insurance);
  }

  private void showInsuranceDialog(Insurance existingInsurance) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
            existingInsurance == null ? "Add Insurance" : "Edit Insurance");

    // Add form fields with initial values if editing
    String idValue = existingInsurance != null ? existingInsurance.getInsuranceId() : "";
    String companyValue = existingInsurance != null ? existingInsurance.getCompany() : "";
    String addressValue = existingInsurance != null ? existingInsurance.getAddress() : "";
    String phoneValue = existingInsurance != null ? existingInsurance.getPhone() : "";

    builder.addTextField("Insurance ID", "insuranceId", idValue);
    builder.addTextField("Company", "company", companyValue);
    builder.addTextField("Address", "address", addressValue);
    builder.addTextField("Phone", "phone", phoneValue);

    // Define save action
    builder.onSave(
        formData -> {
          try {
            // Extract form data
            String id = (String) formData.get("insuranceId");
            String company = (String) formData.get("company");
            String address = (String) formData.get("address");
            String phone = (String) formData.get("phone");

            // Validate insurance ID
            if (id.isEmpty()) {
              showError("Insurance ID cannot be empty", null);
              return;
            }

            // Create insurance object
            Insurance insurance = new Insurance(id, company, address, phone);

            // Add or update insurance
            if (existingInsurance == null) {
              insuranceService.addInsurance(insurance);
              showInfo("Insurance added successfully");
            } else {
              insuranceService.updateInsurance(insurance);
              showInfo("Insurance updated successfully");
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
    if (existingInsurance != null) {
      JComponent idField = dialog.getField("insuranceId");
      if (idField instanceof JTextField) {
        ((JTextField) idField).setEditable(false);
      }
    }

    dialog.setVisible(true);
  }

  @Override
  protected void showAdvancedFilterDialog() {
    // Create filter dialog with the relevant fields
    FormDialog.Builder builder = createFilterDialog(
        "Advanced Filter", "insuranceId", "company", "address", "phone");

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
      List<Insurance> insurances = insuranceService.getAllInsurances();
      
      // Define filter configurations for each field
      Map<String, Function<Insurance, String>> filterMappings =
          Map.of(
              "insuranceId", Insurance::getInsuranceId,
              "company", Insurance::getCompany,
              "address", Insurance::getAddress,
              "phone", Insurance::getPhone);

      // Apply standard filters
      FilterResult<Insurance> result = applyStandardFilters(insurances, formData, filterMappings);

      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering insurances", ex);
    }
  }

  @Override
  protected void deleteItem(Insurance insurance) throws Exception {
    insuranceService.deleteInsurance(insurance.getInsuranceId());
  }
}
