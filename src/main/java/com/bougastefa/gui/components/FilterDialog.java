package com.bougastefa.gui.components;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A reusable dialog component for filtering data in tables.
 * This dialog creates a form with text fields for each filterable field
 * and handles the collection of filter values. When the user applies the filter,
 * the collected values are passed to a callback function that implements the filtering logic.
 * This design separates the UI concerns from the filtering implementation.
 */
public class FilterDialog extends JDialog {
    /** 
     * Stores the mapping between field names and their corresponding text input components.
     * Keys are the field names (database column names), values are the JTextField controls.
     */
    private final Map<String, JTextField> filterFields;
    
    /** 
     * Callback function that will be invoked when the user applies the filter.
     * This consumer accepts a Map of field names to filter values.
     */
    private final Consumer<Map<String, String>> onFilterApplied;

    /**
     * Constructs a new filter dialog with fields for filtering data.
     * 
     * @param owner The parent Frame that owns this dialog
     * @param title The title to display in the dialog's title bar
     * @param fields A list of FilterableField objects defining the fields that can be filtered
     * @param onFilterApplied A callback function that receives the filter criteria
     *                       when the user applies the filter
     */
    public FilterDialog(Frame owner, String title, List<FilterableField> fields,
                       Consumer<Map<String, String>> onFilterApplied) {
        super(owner, title, true); // Call parent constructor with modal=true
        this.filterFields = new HashMap<>();
        this.onFilterApplied = onFilterApplied;
        
        initializeDialog(fields);
    }

    /**
     * Initializes the dialog's UI components and layout.
     * Creates a form with labels and text fields for each filterable field,
     * and adds buttons for applying or canceling the filter operation.
     * 
     * @param fields A list of FilterableField objects defining the fields that can be filtered
     */
    private void initializeDialog(List<FilterableField> fields) {
        setLayout(new BorderLayout(10, 10));
        
        // Create the form panel with a grid layout
        JPanel formPanel = new JPanel(new GridLayout(fields.size(), 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add fields to the form
        for (FilterableField field : fields) {
            JLabel label = new JLabel(field.getLabel() + ":");
            JTextField textField = new JTextField(20);
            
            formPanel.add(label);
            formPanel.add(textField);
            
            filterFields.put(field.getFieldName(), textField);
        }

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton filterButton = new JButton("Filter");
        JButton cancelButton = new JButton("Cancel");

        // Add action listeners
        filterButton.addActionListener(e -> applyFilter());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(filterButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        pack();
        setLocationRelativeTo(getOwner()); // Center relative to owner
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Collects the filter values from the text fields and applies the filter.
     * Only collects non-empty values, creating a map of field names to filter values,
     * then passes this map to the callback function and closes the dialog.
     * This approach allows different screens to implement their own filtering logic
     * while reusing the same dialog component.
     */
    private void applyFilter() {
        Map<String, String> filterValues = new HashMap<>();
        
        // Collect non-empty filter values
        filterFields.forEach((fieldName, textField) -> {
            String value = textField.getText().trim();
            if (!value.isEmpty()) {
                filterValues.put(fieldName, value);
            }
        });

        // Call the callback with the filter values
        onFilterApplied.accept(filterValues);
        dispose();
    }
}
