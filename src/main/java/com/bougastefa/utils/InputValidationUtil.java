package com.bougastefa.utils;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Utility class for validating user input in forms. */
public class InputValidationUtil {
  /**
   * Validates that a string field doesn't exceed a maximum length.
   *
   * @param value The string to validate
   * @param maxLength The maximum allowed length
   * @param fieldName The name of the field for error messages
   * @throws IllegalArgumentException If the string exceeds the maximum length
   */
  public static void validateStringLength(String value, int maxLength, String fieldName) {
    if (value != null && value.length() > maxLength) {
      throw new IllegalArgumentException(
          fieldName + " exceeds maximum length of " + maxLength + " characters");
    }
  }

  /**
   * Sets up length validation for a text field with visual feedback. The field will display in red
   * if the text exceeds the maximum length.
   *
   * @param textField The JTextField to validate
   * @param maxLength The maximum allowed length
   * @return The configured JTextField
   */
  public static JTextField setupLengthValidation(JTextField textField, int maxLength) {
    // Store the original background color
    final Color originalColor = textField.getBackground();
    final Color errorColor = new Color(255, 200, 200); // Light red

    // Add a document listener to check text length as user types
    textField
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
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
                  textField.setToolTipText(
                      "Text exceeds maximum length of " + maxLength + " characters");
                } else {
                  textField.setBackground(originalColor);
                  textField.setToolTipText(null);
                }
              }
            });

    // Set initial state
    if (textField.getText().length() > maxLength) {
      textField.setBackground(errorColor);
      textField.setToolTipText("Text exceeds maximum length of " + maxLength + " characters");
    }

    return textField;
  }

  /**
   * Validates that a string doesn't exceed a maximum length.
   *
   * @param value The string to validate
   * @param maxLength The maximum allowed length
   * @param fieldName The name of the field for error messages
   * @return The original string if valid
   * @throws IllegalArgumentException If the string exceeds the maximum length
   */
  public static String validateLength(String value, int maxLength, String fieldName) {
    if (value != null && value.length() > maxLength) {
      throw new IllegalArgumentException(
          fieldName + " exceeds maximum length of " + maxLength + " characters");
    }
    return value;
  }
}
