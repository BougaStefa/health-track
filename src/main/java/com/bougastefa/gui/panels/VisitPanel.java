package com.bougastefa.gui.panels;

import com.bougastefa.gui.components.BasePanel;
import com.bougastefa.gui.components.FilterResult;
import com.bougastefa.gui.components.FormDialog;
import com.bougastefa.models.Visit;
import com.bougastefa.services.VisitService;
import com.bougastefa.utils.FieldLengthConstants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.*;

/**
 * Panel for managing Visit entities in the application. This panel extends BasePanel to provide
 * specialized functionality for visit management, including adding, editing, deleting, and
 * filtering visits. The Visit entity uses a composite primary key consisting of doctor ID, patient
 * ID, and date of visit, which requires special handling in the form dialogs and during CRUD
 * operations.
 */
public class VisitPanel extends BasePanel<Visit> {
  /** Service object that handles business logic and data operations for visits */
  private final VisitService visitService;

  /**
   * Date formatter for consistent display and parsing of visit dates. Uses ISO-8601 format
   * (YYYY-MM-DD) for compatibility and clarity.
   */
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Constructs a new VisitPanel. Initializes the panel with the "Visit" title and loads initial
   * visit data.
   */
  public VisitPanel() {
    super("Visit");
    visitService = new VisitService();
    loadData();
  }

  /**
   * {@inheritDoc} Defines the column names for the visit table, showing the components of the
   * composite key (date, doctor ID, patient ID) first, followed by symptoms and diagnosis.
   */
  @Override
  protected String[] getColumnNames() {
    return new String[] {"Date of Visit", "Doctor ID", "Patient ID", "Symptoms", "Diagnosis"};
  }

  /**
   * {@inheritDoc} Loads all visits from the service and populates the table with the data. Handles
   * any exceptions that may occur during the data loading process.
   */
  @Override
  protected void loadData() {
    try {
      List<Visit> visits = visitService.getAllVisits();
      populateTable(visits);
    } catch (Exception ex) {
      showError("Error loading visits", ex);
    }
  }

  /**
   * Populates the table with visit data. Formats dates using the dateFormatter to ensure consistent
   * display.
   *
   * @param visits The list of visits to display in the table
   */
  private void populateTable(List<Visit> visits) {
    tableModel.setRowCount(0);
    for (Visit visit : visits) {
      tableModel.addRow(
          new Object[] {
            visit.getDateOfVisit().format(dateFormatter),
            visit.getDoctorId(),
            visit.getPatientId(),
            visit.getSymptoms(),
            visit.getDiagnosis()
          });
    }
  }

  /**
   * {@inheritDoc} Retrieves the currently selected visit from the table. Maps the selected row to a
   * Visit object by extracting all fields from the table row, parsing the date string to a
   * LocalDate object using dateFormatter.
   *
   * @return The selected Visit object, or null if no row is selected or an error occurs
   */
  @Override
  protected Visit getSelectedItem() {
    int row = dataTable.getSelectedRow();
    if (row != -1) {
      try {
        String dateStr = (String) tableModel.getValueAt(row, 0);
        LocalDate dateOfVisit = LocalDate.parse(dateStr, dateFormatter);
        String doctorId = (String) tableModel.getValueAt(row, 1);
        String patientId = (String) tableModel.getValueAt(row, 2);
        String symptoms = (String) tableModel.getValueAt(row, 3);
        String diagnosis = (String) tableModel.getValueAt(row, 4);

        // Create a visit object from the table values
        return new Visit(dateOfVisit, symptoms, diagnosis, doctorId, patientId);
      } catch (Exception ex) {
        showError("Error retrieving visit details", ex);
      }
    } else {
      showInfo("Please select a visit first");
    }
    return null;
  }

  /**
   * {@inheritDoc} Shows a dialog for adding a new visit. Calls showVisitDialog with null to
   * indicate a new visit is being created.
   */
  @Override
  protected void showAddDialog() {
    showVisitDialog(null);
  }

  /**
   * {@inheritDoc} Shows a dialog for editing an existing visit. Calls showVisitDialog with the
   * visit object to pre-populate the form.
   *
   * @param visit The visit to edit
   */
  @Override
  protected void showEditDialog(Visit visit) {
    showVisitDialog(visit);
  }

  /**
   * Creates and displays a form dialog for adding or editing a visit. Sets up the form with fields
   * for all visit properties, including the composite key fields (date of visit, doctor ID, and
   * patient ID) which are made non-editable when updating an existing visit to maintain referential
   * integrity.
   *
   * @param existingVisit The visit to edit, or null if creating a new visit
   */
  private void showVisitDialog(Visit existingVisit) {
    // Create FormDialog.Builder with appropriate title based on operation type
    FormDialog.Builder builder =
        new FormDialog.Builder(
            getParentFrame(), existingVisit == null ? "Add Visit" : "Edit Visit");

    
    // Add form fields with initial values if editing and display max length info
    builder.addTextField(
        "Date of Visit (YYYY-MM-DD)",
        "dateOfVisit",
        existingVisit != null ? existingVisit.getDateOfVisit().toString() : LocalDate.now().format(dateFormatter));
    builder.addTextField(
        "Doctor ID (max " + FieldLengthConstants.VISIT_DOCTOR_ID_MAX_LENGTH + " chars)",
        "doctorId",
        existingVisit != null ? existingVisit.getDoctorId() : "");
    builder.addTextField(
        "Patient ID (max " + FieldLengthConstants.VISIT_PATIENT_ID_MAX_LENGTH + " chars)",
        "patientId",
        existingVisit != null ? existingVisit.getPatientId() : "");
    builder.addTextField(
        "Symptoms (max " + FieldLengthConstants.VISIT_SYMPTOMS_MAX_LENGTH + " chars)",
        "symptoms",
        existingVisit != null ? existingVisit.getSymptoms() : "");
    builder.addTextField(
        "Diagnosis (max " + FieldLengthConstants.VISIT_DIAGNOSIS_MAX_LENGTH + " chars)",
        "diagnosis",
        existingVisit != null ? existingVisit.getDiagnosis() : "");
    // Define save action that will be called when form is submitted
    builder.onSave(
    formData -> {
        try {
            // Extract form data
            String dateOfVisitText = (String) formData.get("dateOfVisit");
            String doctorId = (String) formData.get("doctorId");
            String patientId = (String) formData.get("patientId");
            String symptoms = (String) formData.get("symptoms");
            String diagnosis = (String) formData.get("diagnosis");
            
            // Validate required fields
            if (doctorId.isEmpty() || patientId.isEmpty() || dateOfVisitText.isEmpty()) {
                showError("Date of Visit, Doctor ID, and Patient ID are required fields", null);
                return;
            }
            
            // Validate field lengths
            if (doctorId.length() > FieldLengthConstants.VISIT_DOCTOR_ID_MAX_LENGTH) {
                showError("Doctor ID exceeds maximum length of " + 
                    FieldLengthConstants.VISIT_DOCTOR_ID_MAX_LENGTH + " characters", null);
                return;
            }
            
            if (patientId.length() > FieldLengthConstants.VISIT_PATIENT_ID_MAX_LENGTH) {
                showError("Patient ID exceeds maximum length of " + 
                    FieldLengthConstants.VISIT_PATIENT_ID_MAX_LENGTH + " characters", null);
                return;
            }
            
            if (symptoms != null && symptoms.length() > FieldLengthConstants.VISIT_SYMPTOMS_MAX_LENGTH) {
                showError("Symptoms exceed maximum length of " + 
                    FieldLengthConstants.VISIT_SYMPTOMS_MAX_LENGTH + " characters", null);
                return;
            }
            
            if (diagnosis != null && diagnosis.length() > FieldLengthConstants.VISIT_DIAGNOSIS_MAX_LENGTH) {
                showError("Diagnosis exceeds maximum length of " + 
                    FieldLengthConstants.VISIT_DIAGNOSIS_MAX_LENGTH + " characters", null);
                return;
            }
            
            // Parse date
            LocalDate dateOfVisit;
            try {
                dateOfVisit = LocalDate.parse(dateOfVisitText);
            } catch (DateTimeParseException e) {
                showError("Invalid date format. Please use YYYY-MM-DD format.", null);
                return;
            }
            
            // Create or update the visit
            Visit visit = new Visit(dateOfVisit, symptoms, diagnosis, doctorId, patientId);
            
            if (existingVisit == null) {
                visitService.addVisit(visit);
                showInfo("Visit added successfully");
            } else {
                visitService.updateVisit(visit);
                showInfo("Visit updated successfully");
            }
            
            loadData();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage(), null);
        } catch (Exception ex) {
            showError("Error", ex);
        }
    });
    // Create and show the dialog
    FormDialog dialog = builder.build();

    // Disable composite primary key fields if editing
    // This prevents users from changing the identity of the visit record
    if (existingVisit != null) {
      JComponent dateField = dialog.getField("dateOfVisit");
      JComponent doctorIdField = dialog.getField("doctorId");
      JComponent patientIdField = dialog.getField("patientId");

      // Make the fields non-editable but maintain their visual appearance
      if (dateField instanceof JTextField) {
        ((JTextField) dateField).setEditable(false);
      }

      if (doctorIdField instanceof JTextField) {
        ((JTextField) doctorIdField).setEditable(false);
      }

      if (patientIdField instanceof JTextField) {
        ((JTextField) patientIdField).setEditable(false);
      }
    }

    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc} Shows a dialog for advanced filtering of visits. Creates a filter form with
   * fields corresponding to all visit properties, including the composite key fields and
   * descriptive fields.
   */
  @Override
  protected void showAdvancedFilterDialog() {
    // Create filter dialog with the relevant fields
    FormDialog.Builder builder =
        createFilterDialog(
            "Advanced Filter", "dateOfVisit", "doctorId", "patientId", "symptoms", "diagnosis");

    // Define filter action to be called when filter is applied
    builder.onSave(this::applyFilters);

    // Create and customize the dialog
    FormDialog dialog = builder.build();
    dialog.setSaveButtonText("Filter");
    dialog.setCancelButtonText("Cancel");
    dialog.setVisible(true);
  }

  /**
   * {@inheritDoc} Applies filter criteria to the list of visits and updates the table. Formats
   * dates using the dateFormatter for proper comparison.
   *
   * @param formData Map of field names to filter values from the filter dialog
   */
  @Override
  protected void applyFilters(Map<String, Object> formData) {
    try {
      // Get all visits to start with
      List<Visit> visits = visitService.getAllVisits();

      // Define filter configurations for each field, with appropriate formatters for dates
      Map<String, Function<Visit, String>> filterMappings =
          Map.of(
              "dateOfVisit", visit -> visit.getDateOfVisit().format(dateFormatter),
              "doctorId", Visit::getDoctorId,
              "patientId", Visit::getPatientId,
              "symptoms", Visit::getSymptoms,
              "diagnosis", Visit::getDiagnosis);

      // Apply standard filters using the helper method from BasePanel
      FilterResult<Visit> result = applyStandardFilters(visits, formData, filterMappings);

      // Update the table with the filtered results
      populateTable(result.getResults());
    } catch (Exception ex) {
      showError("Error filtering visits", ex);
    }
  }

  /**
   * {@inheritDoc} Deletes a visit from the system. Uses the composite key (patient ID, doctor ID,
   * date of visit) to identify the visit to delete.
   *
   * @param visit The visit to delete
   * @throws Exception If deletion fails
   */
  @Override
  protected void deleteItem(Visit visit) throws Exception {
    visitService.deleteVisit(visit.getPatientId(), visit.getDoctorId(), visit.getDateOfVisit());
  }
}
