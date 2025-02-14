package com.bougastefa.gui;

import com.bougastefa.gui.panels.DoctorPanel;
import com.bougastefa.gui.panels.DrugPanel;
import com.bougastefa.gui.panels.InsurancePanel;
import com.bougastefa.gui.panels.PatientPanel;
import com.bougastefa.gui.panels.PrescriptionPanel;
import com.bougastefa.gui.panels.VisitPanel;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

  private CardLayout cardLayout;
  private JPanel mainPanel;

  public MainFrame() {
    setTitle("Health Track Application");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

    // Add panels for different entities
    mainPanel.add(new DrugPanel(), "DrugPanel");
    mainPanel.add(new DoctorPanel(), "DoctorPanel");
    mainPanel.add(new PatientPanel(), "PatientPanel");
    mainPanel.add(new PrescriptionPanel(), "PrescriptionPanel");
    mainPanel.add(new InsurancePanel(), "InsurancePanel");
    mainPanel.add(new VisitPanel(), "VisitPanel");

    // Menu
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Tables");

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

    menu.add(drugMenuItem);
    menu.add(doctorMenuItem);
    menu.add(patientMenuItem);
    menu.add(prescriptionMenuItem);
    menu.add(insuranceMenuItem);
    menu.add(visitMenuItem);
    menuBar.add(menu);

    setJMenuBar(menuBar);
    add(mainPanel);

    setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(MainFrame::new);
  }
}
