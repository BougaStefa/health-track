package com.bougastefa.gui.panels;

import com.bougastefa.models.Insurance;
import com.bougastefa.services.InsuranceService;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class InsurancePanel extends JPanel {

  private InsuranceService insuranceService;
  private DefaultListModel<Insurance> insuranceListModel;

  public InsurancePanel() {
    insuranceService = new InsuranceService();
    insuranceListModel = new DefaultListModel<>();

    setLayout(new BorderLayout());

    // List of insurances
    JList<Insurance> insuranceList = new JList<>(insuranceListModel);
    JScrollPane scrollPane = new JScrollPane(insuranceList);
    add(scrollPane, BorderLayout.CENTER);

    // Form to add new insurance
    JPanel formPanel = new JPanel(new GridLayout(5, 2));
    JTextField idField = new JTextField();
    JTextField companyField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField phoneField = new JTextField();
    JButton addButton = new JButton("Add Insurance");

    formPanel.add(new JLabel("Insurance ID:"));
    formPanel.add(idField);
    formPanel.add(new JLabel("Company:"));
    formPanel.add(companyField);
    formPanel.add(new JLabel("Address:"));
    formPanel.add(addressField);
    formPanel.add(new JLabel("Phone:"));
    formPanel.add(phoneField);
    formPanel.add(new JLabel());
    formPanel.add(addButton);

    add(formPanel, BorderLayout.SOUTH);

    addButton.addActionListener(
        e -> {
          String id = idField.getText();
          String company = companyField.getText();
          String address = addressField.getText();
          String phone = phoneField.getText();

          Insurance insurance = new Insurance(id, company, address, phone);
          try {
            insuranceService.addInsurance(insurance);
            insuranceListModel.addElement(insurance);
            JOptionPane.showMessageDialog(this, "Insurance added successfully!");
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error adding insurance: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    loadInsurances();
  }

  private void loadInsurances() {
    try {
      List<Insurance> insurances = insuranceService.getAllInsurances();
      insuranceListModel.clear();
      for (Insurance insurance : insurances) {
        insuranceListModel.addElement(insurance);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading insurances: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
