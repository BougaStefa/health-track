package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Prescription;
import com.bougastefa.services.PrescriptionService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

/**
 * Panel for managing Prescription entities in the application.
 * This panel extends BasePanel to provide specialized functionality for prescription management,
 * including adding, editing, deleting, and filtering prescriptions. It handles date formatting
 * for prescription dates and provides a detailed form for entering prescription information
 * including drug ID, doctor ID, patient ID, dosage, duration, and comments.
 */
public class PrescriptionPanel extends BasePanel<Prescription> {
  /** Service object that handles business logic and data operations for prescriptions */
  private final PrescriptionService prescriptionService;
  
  /** 
   * Date formatter for consistent display and parsing of prescription dates.
   * Uses ISO-8601 format (YYYY-MM-DD) for compatibility and clarity.
   */
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Constructs a new PrescriptionPanel.
   * Initializes the panel with the "Prescription" title and loads initial prescription data.
   */
  public PrescriptionPanel() {
    super("Prescription");
    prescriptionService = new PrescriptionService();
    loadData();
  }

  /**
   * {@inheritDoc}
   * Defines the column names for the prescription table, showing all relevant prescription details.
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
   * {@inheritDoc}
   * Loads all prescriptions from the service and populates the table with the data.
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
   * Populates the table with prescription data.
   * Formats dates using the dateFormatter to ensure consistent display.
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
   * {@inheritDoc}
   * Retrieves the currently selected prescription from the table.
   * Maps the selected row to a Prescription object by using the prescriptionId to look up the full object.
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
   * {@inheritDoc}
   * Shows a dialog for adding a new prescription.
   * Calls showPrescriptionDialog with null to indicate a new prescription is being created.
   */
  @Override
  protected void showAddDialog() {
    showPrescriptionDialog(null);
  }

  /**
   * {@inheritDoc}
   * Shows a dialog for editing an existing prescription.
   * Calls showPrescriptionDialog with the prescription object to pre-populate the form.
   * 
   * @param prescription The prescription to edit
   */
  @Override
  protected void showEditDialog(Prescription prescription) {
    showPrescriptionDialog(prescription);
  }

  /**
   * Creates and displays a form dialog for adding or editing a prescription.
   * Sets up the form with fields for all prescription properties, including
   * date field with proper formatting instructions for the user.
   * 
   * @param existingPrescription The prescription to edit, or null if creating a new prescription
   */
  private void showPrescriptionDialog(Prescription existingPrescription) {
    // Create FormDialog.Builder with appropriate title based on operation type
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(),
            existingPrescription == null ? "Add Prescription" : "Edit Prescription");

    // Add form fields with initial values if editing
    String idValue = existingPrescription != null ? existingPrescription.getPrescriptionId() : "";
    String dateValue =
        existingPrescription != null
            ? existingPrescription.getDateOfPrescribe().format(dateFormatter)
            : LocalDate.now().format(dateFormatter);
    String drugIdValue = existingPrescription != null ? existingPrescription.getDrugId() : "";
    String doctorIdValue = existingPrescription != null ? existingPrescription.getDoctorId() : "";
    String patientIdValue = existingPrescription != null ? existingPrescription.getPatientId() : "";
    String dosageValue =
        existingPrescription != null ? String.valueOf(existingPrescription.getDosage()) : "";
    String durationValue =
        existingPrescription != null ? String.valueOf(existingPrescription.getDuration()) : "";
    String commentValue = existingPrescription != null ? existingPrescription.getComment() : "";

    // Add text fields for all prescription properties
    builder.addTextField("Prescription ID", "prescriptionId", idValue);
    builder.addTextField("Date (YYYY-MM-DD)", "date", dateValue);
    builder.addTextField("Drug ID", "drugId", drugIdValue);
    builder.addTextField("Doctor ID", "doctorId", doctorIdValue);
    builder.addTextField("Patient ID", "patientId", patientIdValue);
    builder.addTextField("Dosage", "dosage", dosageValue);
    builder.addTextField("Duration (days)", "duration", durationValue);
    builder.addTextField("Comment", "comment", commentValue);

    // Define save action that will be called when form is submitted
    builder.onSave(
        formData -> {
          try {
            // Extract form data from the submitted form
            String id = (String) formData.get("prescriptionId");
            String dateText = (String) formData.get("date");
            String drugId = (String) formData.get("drugId");
            String doctorId = (String) formData.get("doctorId");
            String patientId = (String) formData.get("patientId");
            String dosageText = (String) formData.get("dosage");
            String durationText = (String) formData.get("duration");
            String comment = (String) formData.get("comment");

            // Validate prescription ID
            if (id.isEmpty()) {
              showError("Prescription ID cannot be empty", null);
              return;
            }

            // Parse date value, using current date as default if empty
            LocalDate date;
            try {
              date = dateText.isEmpty() ? LocalDate.now() : LocalDate.parse(dateText, dateFormatter);
            } catch (DateTimeParseException dtpe) {
              showError("Please enter the date in YYYY-MM-DD format or leave it empty for today's date", dtpe);
              return;
            }
            
            // Parse numeric values, using 0 as default if empty
            int dosage, duration;
            try {
              dosage = dosageText.isEmpty() ? 0 : Integer.parseInt(dosageText);
              duration = durationText.isEmpty() ? 0 : Integer.parseInt(durationText);
            } catch (NumberFormatException nfe) {
              showError("Please enter valid numbers for dosage and duration or leave them empty", nfe);
              return;
            }

            // Create prescription object with the form data
            Prescription prescription =
                new Prescription(id, date, dosage, duration, comment, drugId, doctorId, patientId);

            // Add or update prescription based on whether we're editing or creating
            if (existingPrescription == null) {
              prescriptionService.addPrescription(prescription);
              showInfo("Prescription added successfully");
            } else {
              prescriptionService.updatePrescription(prescription);
              showInfo("Prescription updated successfully");
            }

            // Refresh display to show changes
            loadData();
          } catch (Exception ex) {
            showError("Error", ex);
          }
        });

    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable ID field if editing (since ID is the primary key and shouldn't change)
    if (existingPrescription != null) {
      JComponent idField = dialog.getField("prescriptionId");
      if (idField instanceof JTextField) {
        ((JTextField) idField).setEditable(false);
      }
    }

    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc}
   * Shows a dialog for advanced filtering of prescriptions.
   * Creates a filter form with fields corresponding to all prescription properties.
   */
  @Override
  protected void showAdvancedFilterDialog() {
    // Create filter dialog with the relevant fields
    FormDialog.Builder builder = createFilterDialog(
        "Advanced Filter",
        "prescriptionId",
        "date",
        "drugId",
        "doctorId",
        "patientId",
        "dosage",
        "duration",
        "comment"
    );

    // Define filter action to be called when filter is applied
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc}
   * Applies filter criteria to the list of prescriptions and updates the table.
   * Maps numeric fields (dosage, duration) to strings for consistent filtering and
   * formats dates using the dateFormatter for proper comparison.
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
      FilterResult<Prescription> result = applyStandardFilters(prescriptions, formData, filterMappings);

      // Update the table with the filtered results
      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering prescriptions", ex);
    }
  }

  /**
   * {@inheritDoc}
   * Deletes a prescription from the system.
   * 
   * @param prescription The prescription to delete
   * @throws Exception If deletion fails
   */
  @Override
  protected void deleteItem(Prescription prescription) throws Exception {
    prescriptionService.deletePrescription(prescription.getPrescriptionId());
  }
}
