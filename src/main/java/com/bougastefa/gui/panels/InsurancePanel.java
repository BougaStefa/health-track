package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Insurance;
import com.bougastefa.services.InsuranceService;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InsurancePanel extends JPanel {
  private final InsuranceService insuranceService;
  private DefaultTableModel tableModel;
  private JTable insuranceTable;

  public InsurancePanel() {
    insuranceService = new InsuranceService();
    setLayout(new BorderLayout());

    // Add button panel
    ButtonPanel buttonPanel = new ButtonPanel("Insurance");
    buttonPanel.setAddButtonListener(e -> showInsuranceDialog(null));
    buttonPanel.setEditButtonListener(e -> editSelectedInsurance());
    buttonPanel.setDeleteButtonListener(e -> deleteSelectedInsurance());
    buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
    buttonPanel.setRefreshButtonListener(e -> loadInsurances());

    add(buttonPanel, BorderLayout.NORTH);

    // Setup table for Insurances
    setupInsuranceTable();
    JScrollPane scrollPane = new JScrollPane(insuranceTable);
    add(scrollPane, BorderLayout.CENTER);

    // Initial load of insurances
    loadInsurances();
  }

  private void setupInsuranceTable() {
    String[] columnNames = {"Insurance ID", "Company", "Address", "Phone"};
    tableModel =
        new DefaultTableModel(columnNames, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };
    insuranceTable = new JTable(tableModel);
    insuranceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    insuranceTable.getTableHeader().setReorderingAllowed(false);
    insuranceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    insuranceTable.setFillsViewportHeight(true);
  }

  private void loadInsurances() {
    try {
      List<Insurance> insurances = insuranceService.getAllInsurances();
      populateTable(insurances);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading insurances: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  private Insurance getSelectedInsurance() {
    int row = insuranceTable.getSelectedRow();
    if (row != -1) {
      try {
        String insuranceId = (String) tableModel.getValueAt(row, 0);
        Insurance insurance = insuranceService.getInsuranceById(insuranceId);
        if (insurance == null) {
          JOptionPane.showMessageDialog(
              this, "Could not find the selected insurance", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return insurance;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            "Error retrieving insurance details: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(
          this,
          "Please select an insurance first",
          "No Selection",
          JOptionPane.INFORMATION_MESSAGE);
    }
    return null;
  }

  private void editSelectedInsurance() {
    Insurance insurance = getSelectedInsurance();
    if (insurance != null) {
      showInsuranceDialog(insurance);
    }
  }

  private void deleteSelectedInsurance() {
    Insurance insurance = getSelectedInsurance();
    if (insurance != null) {
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this insurance?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          insuranceService.deleteInsurance(insurance.getInsuranceId());
          loadInsurances();
          JOptionPane.showMessageDialog(this, "Insurance deleted successfully");
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
              this,
              "Error deleting insurance: " + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  private void showInsuranceDialog(Insurance existingInsurance) {
    // Create FormDialog.Builder
    FormDialog.Builder builder =
        new FormDialog.Builder(
            (Frame) SwingUtilities.getWindowAncestor(this),
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
              JOptionPane.showMessageDialog(
                  this,
                  "Insurance ID cannot be empty",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            // Create insurance object
            Insurance insurance = new Insurance(id, company, address, phone);

            // Add or update insurance
            if (existingInsurance == null) {
              insuranceService.addInsurance(insurance);
              JOptionPane.showMessageDialog(this, "Insurance added successfully");
            } else {
              insuranceService.updateInsurance(insurance);
              JOptionPane.showMessageDialog(this, "Insurance updated successfully");
            }

            // Refresh display
            loadInsurances();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

  private void showAdvancedFilterDialog() {
    // Create a FormDialog.Builder for the filter dialog
    FormDialog.Builder builder =
        new FormDialog.Builder((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter");

    // Add filter fields
    builder.addTextField("Insurance ID", "insuranceId");
    builder.addTextField("Company", "company");
    builder.addTextField("Address", "address");
    builder.addTextField("Phone", "phone");

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
      List<Insurance> insurances = insuranceService.getAllInsurances();
      FilterResult<Insurance> result = new FilterResult<>(insurances);

      // Define filter configurations for each field
      Map<String, Function<Insurance, String>> filterMappings =
          Map.of(
              "insuranceId", Insurance::getInsuranceId,
              "company", Insurance::getCompany,
              "address", Insurance::getAddress,
              "phone", Insurance::getPhone);

      // Apply filters for non-empty fields
      for (Map.Entry<String, Function<Insurance, String>> entry : filterMappings.entrySet()) {
        String fieldName = entry.getKey();
        Function<Insurance, String> getter = entry.getValue();

        String filterValue = (String) formData.get(fieldName);
        if (filterValue != null && !filterValue.isEmpty()) {
          result = result.filter(filterValue, getter);
        }
      }

      populateTable(result.getResults());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this,
          "Error filtering insurances: " + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
