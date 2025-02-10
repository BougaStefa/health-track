package com.bougastefa.gui.panels;

import com.bougastefa.models.Prescription;
import com.bougastefa.services.PrescriptionService;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class PrescriptionPanel extends JPanel {

  private PrescriptionService prescriptionService;
  private DefaultListModel<Prescription> prescriptionListModel;

  public PrescriptionPanel() {
    prescriptionService = new PrescriptionService();
    prescriptionListModel = new DefaultListModel<>();

    setLayout(new BorderLayout());

    // List of prescriptions
    JList<Prescription> prescriptionList = new JList<>(prescriptionListModel);
    JScrollPane scrollPane = new JScrollPane(prescriptionList);
    add(scrollPane, BorderLayout.CENTER);

    // Form to add new prescription
    JPanel formPanel = new JPanel(new GridLayout(8, 2));
    JTextField idField = new JTextField();
    JTextField datePrescribedField = new JTextField();
    JTextField dosageField = new JTextField();
    JTextField durationField = new JTextField();
    JTextField commentField = new JTextField();
    JTextField drugIdField = new JTextField();
    JTextField doctorIdField = new JTextField();
    JTextField patientIdField = new JTextField();
    JButton addButton = new JButton("Add Prescription");

    formPanel.add(new JLabel("Prescription ID:"));
    formPanel.add(idField);
    formPanel.add(new JLabel("Date Prescribed (YYYY-MM-DD):"));
    formPanel.add(datePrescribedField);
    formPanel.add(new JLabel("Dosage:"));
    formPanel.add(dosageField);
    formPanel.add(new JLabel("Duration:"));
    formPanel.add(durationField);
    formPanel.add(new JLabel("Comment:"));
    formPanel.add(commentField);
    formPanel.add(new JLabel("Drug ID:"));
    formPanel.add(drugIdField);
    formPanel.add(new JLabel("Doctor ID:"));
    formPanel.add(doctorIdField);
    formPanel.add(new JLabel("Patient ID:"));
    formPanel.add(patientIdField);
    formPanel.add(new JLabel());
    formPanel.add(addButton);

    add(formPanel, BorderLayout.SOUTH);

    addButton.addActionListener(
        e -> {
          String id = idField.getText();
          String datePrescribedStr = datePrescribedField.getText();
          LocalDate datePrescribed =
              LocalDate.parse(datePrescribedStr, DateTimeFormatter.ISO_LOCAL_DATE);
          int dosage = Integer.parseInt(dosageField.getText());
          int duration = Integer.parseInt(durationField.getText());
          String comment = commentField.getText();
          String drugId = drugIdField.getText();
          String doctorId = doctorIdField.getText();
          String patientId = patientIdField.getText();

          Prescription prescription =
              new Prescription(
                  id, datePrescribed, dosage, duration, comment, drugId, doctorId, patientId);
          try {
            prescriptionService.addPrescription(prescription);
            prescriptionListModel.addElement(prescription);
            JOptionPane.showMessageDialog(this, "Prescription added successfully!");
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error adding prescription: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    loadPrescriptions();
  }

  private void loadPrescriptions() {
    try {
      List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
      prescriptionListModel.clear();
      for (Prescription prescription : prescriptions) {
        prescriptionListModel.addElement(prescription);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this,
          "Error loading prescriptions: " + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
