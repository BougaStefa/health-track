package com.bougastefa.gui.panels;

import com.bougastefa.models.Insurance;
import com.bougastefa.services.InsuranceService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InsurancePanel extends JPanel {
  private InsuranceService insuranceService;
  private DefaultTableModel tableModel;
  private JTable insuranceTable;

  public InsurancePanel() {
    insuranceService = new InsuranceService();
    setLayout(new BorderLayout());

    // Top panel with three sections:
    // Left: Advanced Filter and Clear Filters buttons.
    // Center: Add, Edit, Delete buttons.
    // Right: Refresh button.
    JPanel topPanel = new JPanel(new BorderLayout());

    // Left section: Advanced Filter and Clear Filters buttons
    JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton advancedFilterButton = new JButton("Advanced Filter");
    JButton clearFiltersButton = new JButton("Clear Filters");
    leftButtonPanel.add(advancedFilterButton);
    leftButtonPanel.add(clearFiltersButton);

    // Center section: Add, Edit and Delete buttons
    JPanel centerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton addButton = new JButton("Add Insurance");
    JButton editButton = new JButton("Edit Insurance");
    JButton deleteButton = new JButton("Delete Insurance");
    centerButtonPanel.add(addButton);
    centerButtonPanel.add(editButton);
    centerButtonPanel.add(deleteButton);

    // Right section: Refresh button
    JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton refreshButton = new JButton("Refresh");
    rightButtonPanel.add(refreshButton);

    topPanel.add(leftButtonPanel, BorderLayout.WEST);
    topPanel.add(centerButtonPanel, BorderLayout.CENTER);
    topPanel.add(rightButtonPanel, BorderLayout.EAST);
    add(topPanel, BorderLayout.NORTH);

    // Setup table for Insurances
    String[] columnNames = {"ID", "Company", "Address", "Phone"};
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
    JScrollPane scrollPane = new JScrollPane(insuranceTable);
    add(scrollPane, BorderLayout.CENTER);

    // Listeners for top buttons
    refreshButton.addActionListener(e -> loadInsurances());
    advancedFilterButton.addActionListener(e -> showAdvancedFilterDialog());
    clearFiltersButton.addActionListener(e -> loadInsurances());

    addButton.addActionListener(e -> showInsuranceDialog(null));
    editButton.addActionListener(
        e -> {
          Insurance selectedInsurance = getSelectedInsurance();
          if (selectedInsurance != null) {
            showInsuranceDialog(selectedInsurance);
          } else {
            JOptionPane.showMessageDialog(this, "Please select an insurance to edit");
          }
        });
    deleteButton.addActionListener(e -> deleteSelectedInsurance());

    // Initial load of insurances
    loadInsurances();
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
      String insuranceId = (String) tableModel.getValueAt(row, 0);
      String company = (String) tableModel.getValueAt(row, 1);
      String address = (String) tableModel.getValueAt(row, 2);
      String phone = (String) tableModel.getValueAt(row, 3);
      return new Insurance(insuranceId, company, address, phone);
    }
    return null;
  }

  private void showInsuranceDialog(Insurance existingInsurance) {
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingInsurance == null ? "Add Insurance" : "Edit Insurance",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering insurance details
    JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField idField = new JTextField(20);
    JTextField companyField = new JTextField(20);
    JTextField addressField = new JTextField(20);
    JTextField phoneField = new JTextField(20);

    if (existingInsurance != null) {
      idField.setText(existingInsurance.getInsuranceId());
      idField.setEditable(false);
      companyField.setText(existingInsurance.getCompany());
      addressField.setText(existingInsurance.getAddress());
      phoneField.setText(existingInsurance.getPhone());
    }

    formPanel.add(new JLabel("ID:"));
    formPanel.add(idField);
    formPanel.add(new JLabel("Company:"));
    formPanel.add(companyField);
    formPanel.add(new JLabel("Address:"));
    formPanel.add(addressField);
    formPanel.add(new JLabel("Phone:"));
    formPanel.add(phoneField);

    // Button panel for save and cancel
    JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    dialogButtonPanel.add(saveButton);
    dialogButtonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            Insurance insurance =
                new Insurance(
                    idField.getText(),
                    companyField.getText(),
                    addressField.getText(),
                    phoneField.getText());
            if (existingInsurance == null) {
              insuranceService.addInsurance(insurance);
              JOptionPane.showMessageDialog(this, "Insurance added successfully");
            } else {
              insuranceService.updateInsurance(insurance);
              JOptionPane.showMessageDialog(this, "Insurance updated successfully");
            }
            loadInsurances();
            dialog.dispose();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(dialogButtonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  private void deleteSelectedInsurance() {
    int row = insuranceTable.getSelectedRow();
    if (row != -1) {
      String insuranceId = (String) tableModel.getValueAt(row, 0);
      int result =
          JOptionPane.showConfirmDialog(
              this,
              "Are you sure you want to delete this insurance?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
        try {
          insuranceService.deleteInsurance(insuranceId);
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
    } else {
      JOptionPane.showMessageDialog(this, "Please select an insurance to delete");
    }
  }

  // Popup dialog for advanced filtering
  private void showAdvancedFilterDialog() {
    JDialog filterDialog =
        new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Advanced Filter", true);
    filterDialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering advanced filter criteria
    JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JTextField filterIdField = new JTextField(20);
    JTextField filterCompanyField = new JTextField(20);
    JTextField filterAddressField = new JTextField(20);
    JTextField filterPhoneField = new JTextField(20);

    formPanel.add(new JLabel("ID contains:"));
    formPanel.add(filterIdField);
    formPanel.add(new JLabel("Company contains:"));
    formPanel.add(filterCompanyField);
    formPanel.add(new JLabel("Address contains:"));
    formPanel.add(filterAddressField);
    formPanel.add(new JLabel("Phone contains:"));
    formPanel.add(filterPhoneField);

    // Button panel for filter actions (only Filter and Cancel buttons remain here)
    JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton filterButton = new JButton("Filter");
    JButton cancelButton = new JButton("Cancel");
    filterButtonPanel.add(filterButton);
    filterButtonPanel.add(cancelButton);

    // Action for Filter button: apply filters based on criteria
    filterButton.addActionListener(
        e -> {
          try {
            List<Insurance> insurances = insuranceService.getAllInsurances();

            String idFilter = filterIdField.getText().trim();
            String companyFilter = filterCompanyField.getText().trim();
            String addressFilter = filterAddressField.getText().trim();
            String phoneFilter = filterPhoneField.getText().trim();

            if (!idFilter.isEmpty()) {
              insurances =
                  insurances.stream()
                      .filter(
                          insurance ->
                              insurance
                                  .getInsuranceId()
                                  .toLowerCase()
                                  .contains(idFilter.toLowerCase()))
                      .toList();
            }
            if (!companyFilter.isEmpty()) {
              insurances =
                  insurances.stream()
                      .filter(
                          insurance ->
                              insurance
                                  .getCompany()
                                  .toLowerCase()
                                  .contains(companyFilter.toLowerCase()))
                      .toList();
            }
            if (!addressFilter.isEmpty()) {
              insurances =
                  insurances.stream()
                      .filter(
                          insurance ->
                              insurance
                                  .getAddress()
                                  .toLowerCase()
                                  .contains(addressFilter.toLowerCase()))
                      .toList();
            }
            if (!phoneFilter.isEmpty()) {
              insurances =
                  insurances.stream()
                      .filter(
                          insurance ->
                              insurance
                                  .getPhone()
                                  .toLowerCase()
                                  .contains(phoneFilter.toLowerCase()))
                      .toList();
            }
            populateTable(insurances);
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error filtering insurances: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
          filterDialog.dispose();
        });

    cancelButton.addActionListener(e -> filterDialog.dispose());

    filterDialog.add(formPanel, BorderLayout.CENTER);
    filterDialog.add(filterButtonPanel, BorderLayout.SOUTH);
    filterDialog.pack();
    filterDialog.setLocationRelativeTo(this);
    filterDialog.setVisible(true);
  }
}
