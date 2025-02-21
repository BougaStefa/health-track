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
 * and handles the collection of filter values.
 */
public class FilterDialog extends JDialog {
    private final Map<String, JTextField> filterFields;
    private final Consumer<Map<String, String>> onFilterApplied;

    public FilterDialog(Frame owner, String title, List<FilterableField> fields,
                       Consumer<Map<String, String>> onFilterApplied) {
        super(owner, title, true);
        this.filterFields = new HashMap<>();
        this.onFilterApplied = onFilterApplied;
        
        initializeDialog(fields);
    }

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
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

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
