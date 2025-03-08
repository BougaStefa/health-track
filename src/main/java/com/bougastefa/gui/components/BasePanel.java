package com.bougastefa.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * An abstract base panel class that provides common functionality for all entity panels.
 * 
 * @param <T> The entity type managed by this panel
 */
public abstract class BasePanel<T> extends JPanel {
    protected DefaultTableModel tableModel;
    protected JTable dataTable;
    protected ButtonPanel buttonPanel;

    /**
     * Creates a new BasePanel.
     * 
     * @param entityName The name of the entity managed by this panel (e.g., "Patient", "Doctor")
     */
    public BasePanel(String entityName) {
        setLayout(new BorderLayout());
        
        // Create the button panel with standard buttons
        buttonPanel = new ButtonPanel(entityName);
        buttonPanel.setAddButtonListener(e -> showAddDialog());
        buttonPanel.setEditButtonListener(e -> editSelectedItem());
        buttonPanel.setDeleteButtonListener(e -> deleteSelectedItem());
        buttonPanel.setFilterButtonListener(e -> showAdvancedFilterDialog());
        buttonPanel.setRefreshButtonListener(e -> loadData());
        
        add(buttonPanel, BorderLayout.NORTH);
        
        // Set up the data table with column names from subclass
        String[] columnNames = getColumnNames();
        tableModel = createTableModel(columnNames);
        dataTable = createTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Allow subclasses to customize table if needed
        customizeTable(dataTable);
    }
    
    /**
     * Gets the column names for this entity's table.
     * Must be implemented by subclasses.
     * 
     * @return An array of column names
     */
    protected abstract String[] getColumnNames();
    
    /**
     * Optional method for subclasses to customize the table beyond the defaults.
     * 
     * @param table The JTable to customize
     */
    protected void customizeTable(JTable table) {
        // Default implementation does nothing
        // Subclasses can override to add custom table configuration
    }
    
    /**
     * Adds a custom button to the button panel.
     * 
     * @param buttonText The text to display on the button
     * @param listener The action listener to handle button clicks
     */
    protected void addCustomButton(String buttonText, ActionListener listener) {
        buttonPanel.addCustomButton(buttonText, listener);
    }
    
    /**
     * Loads data into the table.
     * Must be implemented by subclasses to load entity-specific data.
     */
    protected abstract void loadData();
    
    /**
     * Gets the selected item from the table.
     * 
     * @return The selected item or null if no item is selected
     */
    protected abstract T getSelectedItem();
    
    /**
     * Shows a dialog to add a new item.
     */
    protected abstract void showAddDialog();
    
    /**
     * Shows a dialog to edit an existing item.
     * 
     * @param item The item to edit
     */
    protected abstract void showEditDialog(T item);
    
    /**
     * Shows a dialog for advanced filtering.
     */
    protected abstract void showAdvancedFilterDialog();
    
    /**
     * Applies filters to the data and updates the display.
     * 
     * @param formData The filter criteria from the filter dialog
     */
    protected abstract void applyFilters(Map<String, Object> formData);
    
    /**
     * Edits the selected item.
     */
    protected void editSelectedItem() {
        T selectedItem = getSelectedItem();
        if (selectedItem != null) {
            showEditDialog(selectedItem);
        }
    }
    
    /**
     * Deletes the selected item after confirmation.
     */
    protected void deleteSelectedItem() {
        T selectedItem = getSelectedItem();
        if (selectedItem != null) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                try {
                    deleteItem(selectedItem);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Item deleted successfully");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Error deleting item: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }
    
    /**
     * Deletes an item from the data source.
     * Must be implemented by subclasses to handle entity-specific deletion.
     * 
     * @param item The item to delete
     * @throws Exception if deletion fails
     */
    protected abstract void deleteItem(T item) throws Exception;
    
    /**
     * Creates a table model with the given column names.
     * 
     * @param columnNames The column names for the table
     * @return A DefaultTableModel with the specified columns
     */
    protected DefaultTableModel createTableModel(String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return model;
    }
    
    /**
     * Creates a standard JTable with common settings.
     * 
     * @param model The table model to use
     * @return A configured JTable
     */
    protected JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        return table;
    }
    
    /**
     * Gets the parent frame for dialogs.
     * 
     * @return The parent frame
     */
    protected Frame getParentFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }
    
    /**
     * Applies standard filters to a list of items.
     * 
     * @param <E> The entity type
     * @param items The items to filter
     * @param formData The filter criteria
     * @param filterMappings Mappings between field names and getter functions
     * @return A FilterResult containing the filtered items
     */
    protected <E> FilterResult<E> applyStandardFilters(
            List<E> items,
            Map<String, Object> formData,
            Map<String, Function<E, String>> filterMappings) {
        
        FilterResult<E> result = new FilterResult<>(items);
        
        // Apply standard filters for non-empty fields
        for (Map.Entry<String, Function<E, String>> entry : filterMappings.entrySet()) {
            String fieldName = entry.getKey();
            Function<E, String> getter = entry.getValue();
            
            String filterValue = (String) formData.get(fieldName);
            if (filterValue != null && !filterValue.isEmpty()) {
                result = result.filter(filterValue, getter);
            }
        }
        
        return result;
    }
    
    /**
     * Shows an error message.
     * 
     * @param message The error message
     * @param ex The exception (can be null)
     */
    protected void showError(String message, Exception ex) {
        String fullMessage = ex != null ? message + ": " + ex.getMessage() : message;
        JOptionPane.showMessageDialog(this, fullMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an information message.
     * 
     * @param message The message to show
     */
    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    /**
     * Helper method for creating a standard form dialog with filter fields.
     * 
     * @param title The dialog title
     * @param fieldNames The filter field names
     * @return A FormDialog.Builder configured for filtering
     */
    protected FormDialog.Builder createFilterDialog(String title, String... fieldNames) {
        FormDialog.Builder builder = new FormDialog.Builder(getParentFrame(), title);
        
        for (String fieldName : fieldNames) {
            // Convert camelCase to Title Case with spaces (e.g., "firstName" -> "First Name")
            String label = fieldName.replaceAll("([a-z])([A-Z])", "$1 $2");
            label = Character.toUpperCase(label.charAt(0)) + label.substring(1);
            builder.addTextField(label, fieldName);
        }
        
        return builder;
    }
    
    /**
     * Sets up a standard form dialog with readonly fields.
     * 
     * @param dialog The form dialog to configure
     * @param fieldNames The names of fields to make read-only
     */
protected void setupReadOnlyDialog(FormDialog dialog, String... fieldNames) {
    for (String fieldName : fieldNames) {
        JComponent field = dialog.getField(fieldName);
        if (field instanceof JTextField) {
            JTextField textField = (JTextField) field;
            textField.setEditable(false);
            
            // Keep the normal background but add a subtle border change
            // to indicate read-only status
            textField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        }
    }
    
    dialog.setSaveButtonText("Close");
    dialog.setCancelButtonText("Cancel");
}
}
