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
 * This class implements a menu-driven interface with a CardLayout to switch between different
 * entity management panels (Drugs, Doctors, Patients, Prescriptions, Insurances, Visits).
 * Each menu item corresponds to a specific entity type and displays the appropriate panel
 * when selected.
 */
public class MainFrame extends JFrame {

  /**
   * CardLayout manager that controls which panel is currently visible.
   * Allows for switching between different panels while keeping only one visible at a time.
   */
  private CardLayout cardLayout;
  
  /**
   * The main container panel that holds all entity-specific panels.
   * Uses CardLayout to display one panel at a time based on user selection.
   */
  private JPanel mainPanel;

  /**
   * Constructs the main application frame and initializes the user interface.
   * Sets up the window properties, creates the menu bar, initializes all entity panels,
   * and adds them to the card layout with appropriate navigation controls.
   */
  public MainFrame() {
    // Configure the main window properties
    setTitle("Health Track Application");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Center the window on the screen

    // Initialize layout manager and main container panel
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

    // Add panels for different entities, each with a unique name for the CardLayout
    mainPanel.add(new DrugPanel(), "DrugPanel");
    mainPanel.add(new DoctorPanel(), "DoctorPanel");
    mainPanel.add(new PatientPanel(), "PatientPanel");
    mainPanel.add(new PrescriptionPanel(), "PrescriptionPanel");
    mainPanel.add(new InsurancePanel(), "InsurancePanel");
    mainPanel.add(new VisitPanel(), "VisitPanel");

    // Create the application menu bar
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Tables");

    // Create menu items for each entity type with action listeners to switch panels
    JMenuItem drugMenuItem = new JMenuItem("Drugs");
    drugMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "DrugPanel"));

    JMenuItem doctorMenuItem = new JMenuItem("Doctors");
    doctorMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "DoctorPanel"));

    JMenuItem patientMenuItem = new JMenuItem("Patients");
    patientMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "PatientPanel"));

    JMenuItem prescriptionMenuItem = new JMenuItem("Prescriptions");
    prescriptionMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "PrescriptionPanel"));

    JMenuItem insuranceMenuItem = new JMenuItem("Insurances");
    insuranceMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "InsurancePanel"));

    JMenuItem visitMenuItem = new JMenuItem("Visits");
    visitMenuItem.addActionListener(e -> cardLayout.show(mainPanel, "VisitPanel"));

    // Add all menu items to the menu
    menu.add(drugMenuItem);
    menu.add(doctorMenuItem);
    menu.add(patientMenuItem);
    menu.add(prescriptionMenuItem);
    menu.add(insuranceMenuItem);
    menu.add(visitMenuItem);
    menuBar.add(menu);

    // Set the menu bar and add the main panel to the frame
    setJMenuBar(menuBar);
    add(mainPanel);

    // Display the frame
    setVisible(true);
  }
}
