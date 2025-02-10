package com.bougastefa.gui.panels;

import com.bougastefa.models.Drug;
import com.bougastefa.services.DrugService;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class DrugPanel extends JPanel {

  private DrugService drugService;
  private DefaultListModel<Drug> drugListModel;

  public DrugPanel() {
    drugService = new DrugService();
    drugListModel = new DefaultListModel<>();

    setLayout(new BorderLayout());

    // List of drugs
    JList<Drug> drugList = new JList<>(drugListModel);
    JScrollPane scrollPane = new JScrollPane(drugList);
    add(scrollPane, BorderLayout.CENTER);

    // Form to add new drug
    JPanel formPanel = new JPanel(new GridLayout(5, 2));
    JTextField idField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField sideEffectsField = new JTextField();
    JTextField benefitsField = new JTextField();
    JButton addButton = new JButton("Add Drug");

    formPanel.add(new JLabel("Drug ID:"));
    formPanel.add(idField);
    formPanel.add(new JLabel("Name:"));
    formPanel.add(nameField);
    formPanel.add(new JLabel("Side Effects:"));
    formPanel.add(sideEffectsField);
    formPanel.add(new JLabel("Benefits:"));
    formPanel.add(benefitsField);
    formPanel.add(new JLabel());
    formPanel.add(addButton);

    add(formPanel, BorderLayout.SOUTH);

    addButton.addActionListener(
        e -> {
          String id = idField.getText();
          String name = nameField.getText();
          String sideEffects = sideEffectsField.getText();
          String benefits = benefitsField.getText();

          Drug drug = new Drug(id, name, sideEffects, benefits);
          try {
            drugService.addDrug(drug);
            drugListModel.addElement(drug);
            JOptionPane.showMessageDialog(this, "Drug added successfully!");
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error adding drug: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    loadDrugs();
  }

  private void loadDrugs() {
    try {
      List<Drug> drugs = drugService.getAllDrugs();
      drugListModel.clear();
      for (Drug drug : drugs) {
        drugListModel.addElement(drug);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading drugs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
