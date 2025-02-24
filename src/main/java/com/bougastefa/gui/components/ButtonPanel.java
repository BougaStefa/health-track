package com.bougastefa.gui.components;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ButtonPanel extends JPanel {
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;
  private JButton filterButton;
  private JButton refreshButton;

  public ButtonPanel(String entityName) {
    setLayout(new FlowLayout(FlowLayout.CENTER));

    // Initialize buttons with dynamic entity name
    addButton = new JButton("Add " + entityName);
    editButton = new JButton("Edit " + entityName);
    deleteButton = new JButton("Delete " + entityName);
    filterButton = new JButton("Advanced Filter");
    refreshButton = new JButton("Refresh");

    // Add all buttons to the panel
    add(addButton);
    add(editButton);
    add(deleteButton);
    add(filterButton);
    add(refreshButton);
  }

  // Listener setters
  public void setAddButtonListener(ActionListener listener) {
    addButton.addActionListener(listener);
  }

  public void setEditButtonListener(ActionListener listener) {
    editButton.addActionListener(listener);
  }

  public void setDeleteButtonListener(ActionListener listener) {
    deleteButton.addActionListener(listener);
  }

  public void setFilterButtonListener(ActionListener listener) {
    filterButton.addActionListener(listener);
  }

  public void setRefreshButtonListener(ActionListener listener) {
    refreshButton.addActionListener(listener);
  }

  // Optional: Method to add additional buttons if needed for specific panels
  // Designed for use with Patient as it requires an extra button
  public void addCustomButton(String buttonText, ActionListener listener) {
    JButton customButton = new JButton(buttonText);
    customButton.addActionListener(listener);
    add(customButton);
    revalidate();
    repaint();
  }
}
