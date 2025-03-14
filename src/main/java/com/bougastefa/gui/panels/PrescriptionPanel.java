package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Prescription;
import com.bougastefa.services.PrescriptionService;
import com.bougastefa.utils.FieldLengthConstants;
import com.bougastefa.utils.InputValidationUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

/**
 * Panel for managing Prescription entities in the application. This panel extends BasePanel to
 * provide specialized functionality for prescription management, including adding, editing,
 * deleting, and filtering prescriptions. It handles date formatting for prescription dates and
 * provides a detailed form for entering prescription information including drug ID, doctor ID,
 * patient ID, dosage, duration, and comments.
 */
public class PrescriptionPanel extends BasePanel<Prescription> {
  /** Service object that handles business logic and data operations for prescriptions */
  private final PrescriptionService prescriptionService;

  /**
   * Date formatter for consistent display and parsing of prescription dates. Uses ISO-8601 format
   * (YYYY-MM-DD) for compatibility and clarity.
   */
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Constructs a new PrescriptionPanel. Initializes the panel with the "Prescription" title and
   * loads initial prescription data.
   */
  public PrescriptionPanel() {
    super("Prescription");
    prescriptionService = new PrescriptionService();
    loadData();
  }

  /**
   * {@inheritDoc} Defines the column names for the prescription table, showing all relevant
   * prescription details.
   */
  @Override
  protected String[] getColumnNames() {
    return new String[] {
      "Prescription ID",
      "Date",
      "Drug ID",
      "Doctor ID",
      "Patient ID",
      "Dosage",
      "Duration",
      "Comment"
    };
  }

  /**
   * {@inheritDoc} Loads all prescriptions from the service and populates the table with the data.
   * Handles any exceptions that may occur during the data loading process.
   */
  @Override
  protected void loadData() {
    try {
      List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
      populateTable(prescriptions);
    } catch (Exception ex) {
      showError("Error loading prescriptions", ex);
    }
  }

  /**
   * Populates the table with prescription data. Formats dates using the dateFormatter to ensure
   * consistent display.
   *
   * @param prescriptions The list of prescriptions to display in the table
   */
  private void populateTable(List<Prescription> prescriptions) {
    tableModel.setRowCount(0);
    for (Prescription prescription : prescriptions) {
      tableModel.addRow(
          new Object[] {
            prescription.getPrescriptionId(),
            prescription.getDateOfPrescribe().format(dateFormatter),
            prescription.getDrugId(),
            prescription.getDoctorId(),
            prescription.getPatientId(),
            prescription.getDosage(),
            prescription.getDuration(),
            prescription.getComment()
          });
    }
  }

  /**
   * {@inheritDoc} Retrieves the currently selected prescription from the table. Maps the selected
   * row to a Prescription object by using the prescriptionId to look up the full object.
   *
   * @return The selected Prescription object, or null if no row is selected or an error occurs
   */
  @Override
  protected Prescription getSelectedItem() {
    int row = dataTable.getSelectedRow();
    if (row != -1) {
      try {
        String prescriptionId = (String) tableModel.getValueAt(row, 0);
        Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
        if (prescription == null) {
          showError("Could not find the selected prescription", null);
        }
        return prescription;
      } catch (Exception ex) {
        showError("Error retrieving prescription details", ex);
      }
    } else {
      showInfo("Please select a prescription first");
    }
    return null;
  }

  /**
   * {@inheritDoc} Shows a dialog for adding a new prescription. Calls showPrescriptionDialog with
   * null to indicate a new prescription is being created.
   */
  @Override
  protected void showAddDialog() {
    showPrescriptionDialog(null);
  }

  /**
   * {@inheritDoc} Shows a dialog for editing an existing prescription. Calls showPrescriptionDialog
   * with the prescription object to pre-populate the form.
   *
   * @param prescription The prescription to edit
   */
  @Override
  protected void showEditDialog(Prescription prescription) {
    showPrescriptionDialog(prescription);
  }

  /**
   * Creates and displays a form dialog for adding or editing a prescription. Sets up the form with
   * fields for all prescription properties, including date field with proper formatting
   * instructions for the user.
   *
   * @param existingPrescription The prescription to edit, or null if creating a new prescription
   */
  private void showPrescriptionDialog(Prescription existingPrescription) {
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
            existingPrescription == null ? "Add Prescription" : "Edit Prescription");

    // Add form fields with initial values if editing and display max length info
    builder.addTextField(
        "Prescription ID (max " + FieldLengthConstants.PRESCRIPTION_ID_MAX_LENGTH + " chars)",
        "prescriptionId", 
        existingPrescription != null ? existingPrescription.getPrescriptionId() : "");
        
    builder.addTextField(
        "Date (YYYY-MM-DD)",
        "dateOfPrescribe", 
        existingPrescription != null ? existingPrescription.getDateOfPrescribe().toString() : 
            LocalDate.now().toString());
            
    builder.addTextField(
        "Dosage",
        "dosage", 
        existingPrescription != null ? String.valueOf(existingPrescription.getDosage()) : "");
        
    builder.addTextField(
        "Duration (days)",
        "duration", 
        existingPrescription != null ? String.valueOf(existingPrescription.getDuration()) : "");
        
    builder.addTextField(
        "Comment (max " + FieldLengthConstants.PRESCRIPTION_COMMENT_MAX_LENGTH + " chars)",
        "comment", 
        existingPrescription != null ? existingPrescription.getComment() : "");
        
    builder.addTextField(
        "Drug ID (max " + FieldLengthConstants.PRESCRIPTION_DRUG_ID_MAX_LENGTH + " chars)",
        "drugId", 
        existingPrescription != null ? existingPrescription.getDrugId() : "");
        
    builder.addTextField(
        "Doctor ID (max " + FieldLengthConstants.PRESCRIPTION_DOCTOR_ID_MAX_LENGTH + " chars)",
        "doctorId", 
        existingPrescription != null ? existingPrescription.getDoctorId() : "");
        
    builder.addTextField(
        "Patient ID (max " + FieldLengthConstants.PRESCRIPTION_PATIENT_ID_MAX_LENGTH + " chars)",
        "patientId", 
        existingPrescription != null ? existingPrescription.getPatientId() : "");

    // Define save action that will be called when form is submitted
    builder.onSave(
        formData -> {
            try {
                // Extract form data from the submitted form
                String prescriptionId = (String) formData.get("prescriptionId");
                String dateOfPrescribeText = (String) formData.get("dateOfPrescribe");
                String dosageText = (String) formData.get("dosage");
                String durationText = (String) formData.get("duration");
                String comment = (String) formData.get("comment");
                String drugId = (String) formData.get("drugId");
                String doctorId = (String) formData.get("doctorId");
                String patientId = (String) formData.get("patientId");

                // Validate required fields
                if (prescriptionId.isEmpty() ) {
                    showError("Prescription ID is a required field.", null);
                    return;
                }
                
                // Validate field lengths
                if (prescriptionId.length() > FieldLengthConstants.PRESCRIPTION_ID_MAX_LENGTH) {
                    showError("Prescription ID exceeds maximum length of " + 
                        FieldLengthConstants.PRESCRIPTION_ID_MAX_LENGTH + " characters", null);
                    return;
                }
                
                if (drugId.length() > FieldLengthConstants.PRESCRIPTION_DRUG_ID_MAX_LENGTH) {
                    showError("Drug ID exceeds maximum length of " + 
                        FieldLengthConstants.PRESCRIPTION_DRUG_ID_MAX_LENGTH + " characters", null);
                    return;
                }
                
                if (doctorId.length() > FieldLengthConstants.PRESCRIPTION_DOCTOR_ID_MAX_LENGTH) {
                    showError("Doctor ID exceeds maximum length of " + 
                        FieldLengthConstants.PRESCRIPTION_DOCTOR_ID_MAX_LENGTH + " characters", null);
                    return;
                }
                
                if (patientId.length() > FieldLengthConstants.PRESCRIPTION_PATIENT_ID_MAX_LENGTH) {
                    showError("Patient ID exceeds maximum length of " + 
                        FieldLengthConstants.PRESCRIPTION_PATIENT_ID_MAX_LENGTH + " characters", null);
                    return;
                }
                
                if (comment != null && comment.length() > FieldLengthConstants.PRESCRIPTION_COMMENT_MAX_LENGTH) {
                    showError("Comment exceeds maximum length of " + 
                        FieldLengthConstants.PRESCRIPTION_COMMENT_MAX_LENGTH + " characters", null);
                    return;
                }
                
                // Parse numeric values
                int dosage;
                int duration;
                try {
                    dosage = Integer.parseInt(dosageText);
                    if (dosage <= 0) {
                        showError("Dosage must be a positive number", null);
                        return;
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid dosage. Please enter a valid number", null);
                    return;
                }
                
                try {
                    duration = Integer.parseInt(durationText);
                    if (duration <= 0) {
                        showError("Duration must be a positive number", null);
                        return;
                    }
                } catch (NumberFormatException e) {
                    showError("Invalid duration. Please enter a valid number", null);
                    return;
                }

                // Parse date
                LocalDate dateOfPrescribe;
                try {
                    dateOfPrescribe = LocalDate.parse(dateOfPrescribeText);
                } catch (DateTimeParseException e) {
                    showError("Invalid date format. Please use YYYY-MM-DD format", null);
                    return;
                }

                // Create prescription object
                Prescription prescription = new Prescription(
                    prescriptionId, 
                    dateOfPrescribe, 
                    dosage, 
                    duration, 
                    comment, 
                    drugId, 
                    doctorId, 
                    patientId);

                // Add or update prescription
                if (existingPrescription == null) {
                    prescriptionService.addPrescription(prescription);
                    showInfo("Prescription added successfully");
                } else {
                    prescriptionService.updatePrescription(prescription);
                    showInfo("Prescription updated successfully");
                }
                
                // Refresh data in the table
                loadData();
                
            } catch (IllegalArgumentException e) {
                showError(e.getMessage(), null);
            } catch (Exception ex) {
                showError("Error: " + ex.getMessage(), ex);
            }
        });

    // Build and show the dialog
    FormDialog dialog = builder.build();

    // Disable ID field if editing
    if (existingPrescription != null) {
        JComponent idField = dialog.getField("prescriptionId");
        if (idField instanceof JTextField) {
            ((JTextField) idField).setEditable(false);
        }
    }

    dialog.setVisible(true);
}

  /**
   * {@inheritDoc} Shows a dialog for advanced filtering of prescriptions. Creates a filter form
   * with fields corresponding to all prescription properties.
   */
  @Override
  protected void showAdvancedFilterDialog() {
    // Create filter dialog with the relevant fields
    FormDialog.Builder builder =
        createFilterDialog(
            "Advanced Filter",
            "prescriptionId",
            "date",
            "drugId",
            "doctorId",
            "patientId",
            "dosage",
            "duration",
            "comment");

    // Define filter action to be called when filter is applied
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc} Applies filter criteria to the list of prescriptions and updates the table. Maps
   * numeric fields (dosage, duration) to strings for consistent filtering and formats dates using
   * the dateFormatter for proper comparison.
   *
   * @param formData Map of field names to filter values from the filter dialog
   */
  @Override
  protected void applyFilters(Map<String, Object> formData) {
    try {
      // Get all prescriptions to start with
      List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();

      // Define filter configurations for each field, with appropriate conversions
      Map<String, Function<Prescription, String>> filterMappings =
          Map.ofEntries(
              Map.entry("prescriptionId", Prescription::getPrescriptionId),
              Map.entry("date", p -> p.getDateOfPrescribe().format(dateFormatter)),
              Map.entry("drugId", Prescription::getDrugId),
              Map.entry("doctorId", Prescription::getDoctorId),
              Map.entry("patientId", Prescription::getPatientId),
              Map.entry("dosage", p -> String.valueOf(p.getDosage())),
              Map.entry("duration", p -> String.valueOf(p.getDuration())),
              Map.entry("comment", Prescription::getComment));

      // Apply standard filters using the helper method from BasePanel
      FilterResult<Prescription> result =
          applyStandardFilters(prescriptions, formData, filterMappings);

      // Update the table with the filtered results
      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering prescriptions", ex);
    }
  }

  /**
   * {@inheritDoc} Deletes a prescription from the system.
   *
   * @param prescription The prescription to delete
   * @throws Exception If deletion fails
   */
  @Override
  protected void deleteItem(Prescription prescription) throws Exception {
    prescriptionService.deletePrescription(prescription.getPrescriptionId());
  }
}
