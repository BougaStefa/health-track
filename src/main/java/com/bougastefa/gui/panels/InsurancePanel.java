package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.ButtonPanel;
import com.bougastefa.gui.components.FilterDialog;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FilterableField;
import com.bougastefa.models.Insurance;
import com.bougastefa.services.InsuranceService;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    JDialog dialog =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existingInsurance == null ? "Add Insurance" : "Edit Insurance",
            true);
    dialog.setLayout(new BorderLayout(10, 10));

    // Form panel for entering insurance details
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

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

    addFormField(formPanel, "Insurance ID:", idField, gbc, 0);
    addFormField(formPanel, "Company:", companyField, gbc, 1);
    addFormField(formPanel, "Address:", addressField, gbc, 2);
    addFormField(formPanel, "Phone:", phoneField, gbc, 3);

    // Button panel for save and cancel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    saveButton.addActionListener(
        e -> {
          try {
            // Enforce PK constraint
            String id = idField.getText().trim();
            if (id.isEmpty()) {
              JOptionPane.showMessageDialog(
                  this,
                  "Insurance ID cannot be empty",
                  "Validation Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }

            Insurance insurance =
                new Insurance(
                    id, companyField.getText(), addressField.getText(), phoneField.getText());
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
            new FilterableField("ID", "insuranceId"),
            new FilterableField("Company", "company"),
            new FilterableField("Address", "address"),
            new FilterableField("Phone", "phone"));

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
      List<Insurance> insurances = insuranceService.getAllInsurances();
      FilterResult<Insurance> result = new FilterResult<>(insurances);

      if (filters.containsKey("insuranceId")) {
        result = result.filter(filters.get("insuranceId"), Insurance::getInsuranceId);
      }
      if (filters.containsKey("company")) {
        result = result.filter(filters.get("company"), Insurance::getCompany);
      }
      if (filters.containsKey("address")) {
        result = result.filter(filters.get("address"), Insurance::getAddress);
      }
      if (filters.containsKey("phone")) {
        result = result.filter(filters.get("phone"), Insurance::getPhone);
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
