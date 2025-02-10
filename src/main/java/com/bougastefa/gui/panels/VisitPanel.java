package com.bougastefa.gui.panels;

import com.bougastefa.models.Visit;
import com.bougastefa.services.VisitService;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class VisitPanel extends JPanel {

  private VisitService visitService;
  private DefaultListModel<Visit> visitListModel;

  public VisitPanel() {
    visitService = new VisitService();
    visitListModel = new DefaultListModel<>();

    setLayout(new BorderLayout());

    // List of visits
    JList<Visit> visitList = new JList<>(visitListModel);
    JScrollPane scrollPane = new JScrollPane(visitList);
    add(scrollPane, BorderLayout.CENTER);

    // Form to add new visit
    JPanel formPanel = new JPanel(new GridLayout(6, 2));
    JTextField dateField = new JTextField();
    JTextField symptomsField = new JTextField();
    JTextField diagnosisField = new JTextField();
    JTextField doctorIdField = new JTextField();
    JTextField patientIdField = new JTextField();
    JButton addButton = new JButton("Add Visit");

    formPanel.add(new JLabel("Date of Visit (YYYY-MM-DD):"));
    formPanel.add(dateField);
    formPanel.add(new JLabel("Symptoms:"));
    formPanel.add(symptomsField);
    formPanel.add(new JLabel("Diagnosis:"));
    formPanel.add(diagnosisField);
    formPanel.add(new JLabel("Doctor ID:"));
    formPanel.add(doctorIdField);
    formPanel.add(new JLabel("Patient ID:"));
    formPanel.add(patientIdField);
    formPanel.add(new JLabel());
    formPanel.add(addButton);

    add(formPanel, BorderLayout.SOUTH);

    addButton.addActionListener(
        e -> {
          String dateStr = dateField.getText();
          LocalDate dateOfVisit = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
          String symptoms = symptomsField.getText();
          String diagnosis = diagnosisField.getText();
          String doctorId = doctorIdField.getText();
          String patientId = patientIdField.getText();

          Visit visit = new Visit(dateOfVisit, symptoms, diagnosis, doctorId, patientId);
          try {
            visitService.addVisit(visit);
            visitListModel.addElement(visit);
            JOptionPane.showMessageDialog(this, "Visit added successfully!");
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error adding visit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    loadVisits();
  }

  private void loadVisits() {
    try {
      List<Visit> visits = visitService.getAllVisits();
      visitListModel.clear();
      for (Visit visit : visits) {
        visitListModel.addElement(visit);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this, "Error loading visits: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
