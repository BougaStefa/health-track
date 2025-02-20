package com.bougastefa.gui.dialogs;

import com.bougastefa.models.Doctor;
import java.awt.*;
import javax.swing.*;

public class DoctorDetailsDialog extends JDialog {

  public DoctorDetailsDialog(Frame owner, Doctor doctor) {
    super(owner, "Doctor Details", true);
    setLayout(new BorderLayout(10, 10));

    // Create panel for doctor details
    JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Add doctor details
    detailsPanel.add(new JLabel("Doctor ID:"));
    detailsPanel.add(new JLabel(doctor.getDoctorId()));

    detailsPanel.add(new JLabel("First Name:"));
    detailsPanel.add(new JLabel(doctor.getFirstName()));

    detailsPanel.add(new JLabel("Surname:"));
    detailsPanel.add(new JLabel(doctor.getSurname()));

    detailsPanel.add(new JLabel("Address:"));
    detailsPanel.add(new JLabel(doctor.getAddress()));

    detailsPanel.add(new JLabel("Email:"));
    detailsPanel.add(new JLabel(doctor.getEmail()));

    detailsPanel.add(new JLabel("Hospital:"));
    detailsPanel.add(new JLabel(doctor.getHospital()));

    // Add close button
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dispose());

    add(detailsPanel, BorderLayout.CENTER);
    add(closeButton, BorderLayout.SOUTH);

    // Set dialog size and location
    pack();
    setLocationRelativeTo(owner);
  }
}
