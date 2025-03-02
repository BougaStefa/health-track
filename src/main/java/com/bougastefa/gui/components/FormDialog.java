package com.bougastefa.gui.components;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * A reusable form dialog component that can be configured for different entity types. This dialog
 * handles common form operations like creating/editing entities.
 */
public class FormDialog extends JDialog {
  private final Map<String, JComponent> formFields = new HashMap<>();
  private final JPanel formPanel;
  private final JPanel buttonPanel;
  private JButton saveButton;
  private JButton cancelButton;
  private final Consumer<Map<String, Object>> saveAction;

  /**
   * Creates a new form dialog.
   *
   * @param owner The parent frame
   * @param title The dialog title
   * @param fields The form field definitions
   * @param saveAction The action to execute when saving the form
   */
  public FormDialog(
      Frame owner,
      String title,
      List<FormField<?>> fields,
      Consumer<Map<String, Object>> saveAction) {
    super(owner, title, true);
    this.saveAction = saveAction;

    setLayout(new BorderLayout(10, 10));

    // Create form panel with proper layout
    formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Create and add all form fields
    createFormFields(fields);

    // Create button panel
    buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    saveButton = new JButton("Save");
    cancelButton = new JButton("Cancel");
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    // Add components to dialog
    add(formPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    // Set up action listeners
    setupActionListeners();

    pack();
    setLocationRelativeTo(owner);
  }

  private void createFormFields(List<FormField<?>> fields) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    int row = 0;
    for (FormField<?> field : fields) {
      gbc.gridx = 0;
      gbc.gridy = row;
      gbc.weightx = 0;

      // For checkbox fields, leave label empty
      if (!(field.getComponent() instanceof JCheckBox)) {
        formPanel.add(new JLabel(field.getLabel() + ":"), gbc);
      } else {
        formPanel.add(new JLabel(), gbc);
      }

      gbc.gridx = 1;
      gbc.weightx = 1;
      formPanel.add(field.getComponent(), gbc);

      // Store the field in our map for later retrieval
      formFields.put(field.getFieldName(), field.getComponent());

      row++;
    }
  }

  private void setupActionListeners() {
    saveButton.addActionListener(
        e -> {
          try {
            Map<String, Object> formData = collectFormData();
            saveAction.accept(formData);
            dispose();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        });

    cancelButton.addActionListener(e -> dispose());
  }

  private Map<String, Object> collectFormData() {
    Map<String, Object> data = new HashMap<>();

    for (Map.Entry<String, JComponent> entry : formFields.entrySet()) {
      String fieldName = entry.getKey();
      JComponent component = entry.getValue();

      if (component instanceof JTextField) {
        data.put(fieldName, ((JTextField) component).getText().trim());
      } else if (component instanceof JCheckBox) {
        data.put(fieldName, ((JCheckBox) component).isSelected());
      }
      // Add more component types as needed
    }

    return data;
  }

  /**
   * Set a custom save button text.
   *
   * @param text The text to display on the save button
   */
  public void setSaveButtonText(String text) {
    saveButton.setText(text);
  }

  /**
   * Set a custom cancel button text.
   *
   * @param text The text to display on the cancel button
   */
  public void setCancelButtonText(String text) {
    cancelButton.setText(text);
  }

  /**
   * Add a custom button to the button panel.
   *
   * @param text The button text
   * @param listener The action listener for the button
   */
  public void addButton(String text, ActionListener listener) {
    JButton button = new JButton(text);
    button.addActionListener(listener);
    buttonPanel.add(button, buttonPanel.getComponentCount() - 1);
    buttonPanel.revalidate();
  }

  /**
   * Get a field component by its name.
   *
   * @param fieldName The field name
   * @return The component associated with the field name
   */
  public JComponent getField(String fieldName) {
    return formFields.get(fieldName);
  }

  /**
   * A class representing a form field with its associated component.
   *
   * @param <T> The type of component used for the field
   */
  public static class FormField<T extends JComponent> {
    private final String label;
    private final String fieldName;
    private final T component;

    /**
     * Creates a new form field.
     *
     * @param label The field label to display
     * @param fieldName The field name for data binding
     * @param component The component used for this field
     */
    public FormField(String label, String fieldName, T component) {
      this.label = label;
      this.fieldName = fieldName;
      this.component = component;
    }

    public String getLabel() {
      return label;
    }

    public String getFieldName() {
      return fieldName;
    }

    public T getComponent() {
      return component;
    }
  }

  /** Builder class to simplify FormDialog creation */
  public static class Builder {
    private final Frame owner;
    private final String title;
    private final List<FormField<?>> fields = new ArrayList<>();
    private Consumer<Map<String, Object>> saveAction;

    public Builder(Frame owner, String title) {
      this.owner = owner;
      this.title = title;
    }

    public Builder addTextField(String label, String fieldName) {
      return addTextField(label, fieldName, "");
    }

    public Builder addTextField(String label, String fieldName, String initialValue) {
      JTextField textField = new JTextField(20);
      textField.setText(initialValue);
      fields.add(new FormField<>(label, fieldName, textField));
      return this;
    }

    public Builder addCheckBox(String label, String fieldName, boolean selected) {
      JCheckBox checkBox = new JCheckBox(label);
      checkBox.setSelected(selected);
      fields.add(new FormField<>("", fieldName, checkBox));
      return this;
    }

    public Builder onSave(Consumer<Map<String, Object>> saveAction) {
      this.saveAction = saveAction;
      return this;
    }

    public FormDialog build() {
      if (saveAction == null) {
        throw new IllegalStateException("Save action must be defined");
      }
      return new FormDialog(owner, title, fields, saveAction);
    }
  }
}
