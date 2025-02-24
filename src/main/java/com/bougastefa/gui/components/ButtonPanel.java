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

  public ButtonPanel() {
    setLayout(new FlowLayout(FlowLayout.CENTER));

    addButton = new JButton("Add Drug");
    editButton = new JButton("Edit Drug");
    deleteButton = new JButton("Delete Drug");
    filterButton = new JButton("Advanced Filter");
    refreshButton = new JButton("Refresh");

    add(addButton);
    add(editButton);
    add(deleteButton);
    add(filterButton);
    add(refreshButton);
  }

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
}
