package com.bougastefa.gui.components;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A reusable form dialog component that can be configured for different entity types.
 * This dialog handles common form operations like creating/editing entities through
 * a flexible, configurable interface. It supports various input types and can be
 * customized for different entity forms with minimal code duplication.
 * The class uses a builder pattern to simplify the creation and configuration process.
 */
public class FormDialog extends JDialog {
  /**
   * Map storing all form field components by their field names.
   * This allows for easy access and retrieval of field values when processing the form.
   */
  private final Map<String, JComponent> formFields = new HashMap<>();
  
  /** Panel containing all form fields in a structured layout */
  private final JPanel formPanel;
  
  /** Panel containing action buttons such as Save and Cancel */
  private final JPanel buttonPanel;
  
  /** Button that triggers the save action when clicked */
  private JButton saveButton;
  
  /** Button that closes the dialog without saving */
  private JButton cancelButton;
  
  /** 
   * Callback function that processes the form data when the Save button is clicked.
   * Accepts a map of field names to their values.
   */
  private final Consumer<Map<String, Object>> saveAction;

  /**
   * Creates a new form dialog with the specified properties and fields.
   *
   * @param owner The parent frame that owns this dialog
   * @param title The title to display in the dialog's title bar
   * @param fields The list of form field definitions that specify the form's structure
   * @param saveAction The callback function that processes the form data when saved
   */
  public FormDialog(
      Frame owner,
      String title,
      List<FormField<?>> fields,
      Consumer<Map<String, Object>> saveAction) {
    super(owner, title, true); // Create a modal dialog
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
    setLocationRelativeTo(owner); // Center dialog relative to owner
  }

  /**
   * Creates and arranges form fields based on the provided field definitions.
   * Uses a GridBagLayout to properly align labels with their corresponding input components.
   * Special handling is provided for checkbox fields which have a different layout pattern.
   *
   * @param fields The list of form field definitions to create UI components from
   */
  private void createFormFields(List<FormField<?>> fields) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    int row = 0;
    for (FormField<?> field : fields) {
      gbc.gridx = 0;
      gbc.gridy = row;
      gbc.weightx = 0;

      // For checkbox fields, leave label empty since the checkbox itself has a label
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

  /**
   * Sets up event listeners for the dialog's buttons.
   * The save button collects form data and passes it to the saveAction callback,
   * while the cancel button simply closes the dialog.
   */
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

  /**
   * Collects all form field values into a map for processing.
   * Iterates through all form components and extracts their values based on component type.
   * Currently supports JTextField and JCheckBox components, but can be extended for other types.
   *
   * @return A map where keys are field names and values are the corresponding field values
   */
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
   * Sets a custom text for the save button.
   * This allows for context-specific labeling (e.g., "Create", "Update", "Submit").
   *
   * @param text The text to display on the save button
   */
  public void setSaveButtonText(String text) {
    saveButton.setText(text);
  }

  /**
   * Sets a custom text for the cancel button.
   * This allows for context-specific labeling (e.g., "Cancel", "Close", "Back").
   *
   * @param text The text to display on the cancel button
   */
  public void setCancelButtonText(String text) {
    cancelButton.setText(text);
  }

  /**
   * Adds a custom button to the button panel.
   * This allows for additional actions beyond the standard save and cancel operations.
   *
   * @param text The button text to display
   * @param listener The action listener for the button
   */
  public void addButton(String text, ActionListener listener) {
    JButton button = new JButton(text);
    button.addActionListener(listener);
    buttonPanel.add(button, buttonPanel.getComponentCount() - 1);
    buttonPanel.revalidate();
  }

  /**
   * Gets a field component by its name.
   * This allows for programmatic manipulation of form fields after the dialog is created.
   *
   * @param fieldName The field name to look up
   * @return The component associated with the field name, or null if not found
   */
  public JComponent getField(String fieldName) {
    return formFields.get(fieldName);
  }

  /**
   * A class representing a form field with its associated component.
   * Each form field consists of a label, a field name for data binding,
   * and a Swing component for user input.
   *
   * @param <T> The type of component used for the field (must be a JComponent subclass)
   */
  public static class FormField<T extends JComponent> {
    /** The display label for this field */
    private final String label;
    
    /** The field name used for data binding and identification */
    private final String fieldName;
    
    /** The Swing component used for user input */
    private final T component;

    /**
     * Creates a new form field.
     *
     * @param label The field label to display next to the component
     * @param fieldName The field name for data binding and identification
     * @param component The component used for user input
     */
    public FormField(String label, String fieldName, T component) {
      this.label = label;
      this.fieldName = fieldName;
      this.component = component;
    }

    /**
     * Gets the display label for this field.
     *
     * @return The label text
     */
    public String getLabel() {
      return label;
    }

    /**
     * Gets the field name used for data binding.
     *
     * @return The field name
     */
    public String getFieldName() {
      return fieldName;
    }

    /**
     * Gets the component used for this field.
     *
     * @return The input component
     */
    public T getComponent() {
      return component;
    }
  }

  /**
   * Builder class to simplify FormDialog creation.
   * Implements the builder pattern to provide a fluent API for creating form dialogs
   * with various types of fields and configurations.
   */
  public static class Builder {
    /** The parent frame that will own the dialog */
    private final Frame owner;
    
    /** The title for the dialog */
    private final String title;
    
    /** The list of field definitions that will make up the form */
    private final List<FormField<?>> fields = new ArrayList<>();
    
    /** The action to perform when the form is saved */
    private Consumer<Map<String, Object>> saveAction;

    /**
     * Creates a new builder for a form dialog.
     *
     * @param owner The parent frame that will own the dialog
     * @param title The title for the dialog
     */
    public Builder(Frame owner, String title) {
      this.owner = owner;
      this.title = title;
    }

    /**
     * Adds a text field to the form with an empty initial value.
     *
     * @param label The label to display for the field
     * @param fieldName The field name for data binding
     * @return This builder, for method chaining
     */
    public Builder addTextField(String label, String fieldName) {
      return addTextField(label, fieldName, "");
    }

    /**
     * Adds a text field to the form with a specified initial value.
     *
     * @param label The label to display for the field
     * @param fieldName The field name for data binding
     * @param initialValue The initial value for the text field
     * @return This builder, for method chaining
     */
public Builder addTextField(String label, String fieldName, String initialValue) {
  JTextField textField = new JTextField(20);
  textField.setText(initialValue);
  
  // Extract max length from label if it contains the pattern "max X chars"
  if (label.contains("max ")) {
    try {
      String maxLengthStr = label.substring(label.indexOf("max ") + 4);
      maxLengthStr = maxLengthStr.substring(0, maxLengthStr.indexOf(" chars"));
      int maxLength = Integer.parseInt(maxLengthStr);
      
      // Store the original background color
      final Color originalColor = textField.getBackground();
      final Color errorColor = new Color(255, 200, 200); // Light red
      
      // Add a document listener to check text length as user types
      textField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          checkLength();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          checkLength();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          checkLength();
        }
        
        private void checkLength() {
          if (textField.getText().length() > maxLength) {
            textField.setBackground(errorColor);
            textField.setToolTipText("Text exceeds maximum length of " + maxLength + " characters");
          } else {
            textField.setBackground(originalColor);
            textField.setToolTipText(null);
          }
        }
      });
      
      // Set initial state
      if (initialValue != null && initialValue.length() > maxLength) {
        textField.setBackground(errorColor);
        textField.setToolTipText("Text exceeds maximum length of " + maxLength + " characters");
      }
    } catch (Exception e) {
      // Ignore parsing errors, validation won't be applied
    }
  }
  
  fields.add(new FormField<>(label, fieldName, textField));
  return this;
}

    /**
     * Adds a checkbox field to the form.
     *
     * @param label The label for the checkbox
     * @param fieldName The field name for data binding
     * @param selected Whether the checkbox should be initially selected
     * @return This builder, for method chaining
     */
    public Builder addCheckBox(String label, String fieldName, boolean selected) {
      JCheckBox checkBox = new JCheckBox(label);
      checkBox.setSelected(selected);
      fields.add(new FormField<>("", fieldName, checkBox));
      return this;
    }

    /**
     * Sets the save action for the form.
     *
     * @param saveAction The callback function that processes the form data when saved
     * @return This builder, for method chaining
     */
    public Builder onSave(Consumer<Map<String, Object>> saveAction) {
      this.saveAction = saveAction;
      return this;
    }

    /**
     * Builds and returns the form dialog based on the configuration.
     * Validates that a save action has been provided before creating the dialog.
     *
     * @return The configured FormDialog instance
     * @throws IllegalStateException if no save action has been defined
     */
    public FormDialog build() {
      if (saveAction == null) {
        throw new IllegalStateException("Save action must be defined");
      }
      return new FormDialog(owner, title, fields, saveAction);
    }
  }
}
