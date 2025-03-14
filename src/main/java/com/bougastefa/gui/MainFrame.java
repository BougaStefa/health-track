package com.bougastefa.gui;

import com.bougastefa.gui.panels.DoctorPanel;
import com.bougastefa.gui.panels.DrugPanel;
import com.bougastefa.gui.panels.InsurancePanel;
import com.bougastefa.gui.panels.PatientPanel;
import com.bougastefa.gui.panels.PrescriptionPanel;
import com.bougastefa.gui.panels.VisitPanel;
import java.awt.*;
import javax.swing.*;

/**
 * The main application window that serves as the container for all entity management panels.
 * This class implements a tab-based interface to switch between different
 * entity management panels (Drugs, Doctors, Patients, Prescriptions, Insurances, Visits).
 * Each tab corresponds to a specific entity type and displays the appropriate panel when selected.
 */
public class MainFrame extends JFrame {

  /**
   * The JTabbedPane that holds all entity-specific panels.
   * Allows users to switch between panels by clicking on tabs.
   */
  private JTabbedPane tabbedPane;

  /**
   * Constructs the main application frame and initializes the user interface.
   * Sets up the window properties, creates the tabbed pane, initializes all entity panels,
   * and adds them as tabs with appropriate labels.
   */
  public MainFrame() {
    // Configure the main window properties
    setTitle("Health Track Application");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Center the window on the screen

    // Initialize tabbed pane
    tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);

    // Add panels for different entities as tabs
    tabbedPane.addTab("Drugs", new DrugPanel());
    tabbedPane.addTab("Doctors", new DoctorPanel());
    tabbedPane.addTab("Patients", new PatientPanel());
    tabbedPane.addTab("Prescriptions", new PrescriptionPanel());
    tabbedPane.addTab("Insurances", new InsurancePanel());
    tabbedPane.addTab("Visits", new VisitPanel());

    // Add the tabbed pane to the frame
    add(tabbedPane);

    // Display the frame
    setVisible(true);
  }
}
