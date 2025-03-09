package com.bougastefa.gui.components;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * A reusable Swing panel component that contains a standardized set of buttons
 * for performing CRUD (Create, Read, Update, Delete) operations and other common
 * actions on entities in the application.
 * This panel provides a consistent user interface for different entity management screens
 * while allowing customization of button labels and behaviors.
 */
public class ButtonPanel extends JPanel {
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;
  private JButton filterButton;
  private JButton refreshButton;

  /**
   * Constructs a new ButtonPanel with entity-specific button labels.
   * Creates a set of standard buttons (Add, Edit, Delete, Filter, and Refresh)
   * with appropriate labels based on the entity type being managed.
   *
   * @param entityName The name of the entity (e.g., "Patient", "Doctor") to be used
   *                   in button labels for context-specific operations
   */
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

  /**
   * Sets the action listener for the Add button.
   * This defines the behavior when a user clicks the Add button.
   *
   * @param listener The ActionListener that will handle the button click event
   */
  public void setAddButtonListener(ActionListener listener) {
    addButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the Edit button.
   * This defines the behavior when a user clicks the Edit button.
   *
   * @param listener The ActionListener that will handle the button click event
   */
  public void setEditButtonListener(ActionListener listener) {
    editButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the Delete button.
   * This defines the behavior when a user clicks the Delete button.
   *
   * @param listener The ActionListener that will handle the button click event
   */
  public void setDeleteButtonListener(ActionListener listener) {
    deleteButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the Filter button.
   * This defines the behavior when a user clicks the Advanced Filter button.
   *
   * @param listener The ActionListener that will handle the button click event
   */
  public void setFilterButtonListener(ActionListener listener) {
    filterButton.addActionListener(listener);
  }

  /**
   * Sets the action listener for the Refresh button.
   * This defines the behavior when a user clicks the Refresh button.
   *
   * @param listener The ActionListener that will handle the button click event
   */
  public void setRefreshButtonListener(ActionListener listener) {
    refreshButton.addActionListener(listener);
  }

  /**
   * Adds an additional custom button to the panel.
   * This method allows for extending the standard button set with entity-specific
   * functionality when needed. It's particularly designed for the Patient screen
   * which requires special operations beyond the standard CRUD actions.
   *
   * @param buttonText The text to display on the custom button
   * @param listener The ActionListener that will handle the custom button's click event
   */
  public void addCustomButton(String buttonText, ActionListener listener) {
    JButton customButton = new JButton(buttonText);
    customButton.addActionListener(listener);
    add(customButton);
    revalidate();  // Update the layout to include the new button
    repaint();     // Ensure the new button is visually displayed
  }
}
