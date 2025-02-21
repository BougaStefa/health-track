package com.bougastefa.gui.components;

// Holds each field's label and name from the ones that can be "filtered by" in the advanced
// filters.
public class FilterableField {
  private final String label;
  private final String fieldName;

  public FilterableField(String label, String fieldName) {
    this.label = label;
    this.fieldName = fieldName;
  }

  public String getLabel() {
    return label;
  }

  public String getFieldName() {
    return fieldName;
  }
}
