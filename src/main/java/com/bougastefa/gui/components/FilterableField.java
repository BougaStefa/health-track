package com.bougastefa.gui.components;

/**
 * A data container class that holds information about fields that can be used
 * in the advanced filtering system of the application.
 * Each FilterableField instance represents a database column or object property
 * that users can filter on, storing both the user-friendly label and the actual 
 * field name used in queries.
 */
public class FilterableField {
  /** The user-friendly display label shown in the UI for this field */
  private final String label;
  
  /** The actual field/column name used in database queries or object properties */
  private final String fieldName;

  /**
   * Constructs a new FilterableField with the specified label and field name.
   * 
   * @param label The human-readable label to display in the user interface
   *              (e.g., "First Name", "Date of Birth")
   * @param fieldName The corresponding field or column name used in database queries
   *                  or object properties (e.g., "firstname", "dateOfBirth")
   */
  public FilterableField(String label, String fieldName) {
    this.label = label;
    this.fieldName = fieldName;
  }

  /**
   * Retrieves the user-friendly display label for this field.
   * This label is shown in dropdown menus and filter dialogs to help users
   * identify which field they want to filter on.
   * 
   * @return The human-readable label for this field
   */
  public String getLabel() {
    return label;
  }

  /**
   * Retrieves the actual field or column name used for filtering operations.
   * This name corresponds to a database column or object property and is used
   * in constructing queries or filtering collections programmatically.
   * 
   * @return The technical field/column name for this field
   */
  public String getFieldName() {
    return fieldName;
  }
}
