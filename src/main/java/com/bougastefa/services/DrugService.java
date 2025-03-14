package com.bougastefa.services;

import com.bougastefa.database.DrugDAO;
import com.bougastefa.models.Drug;
import com.bougastefa.utils.FieldLengthConstants;
import com.bougastefa.utils.InputValidationUtil;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages the business logic for Drug entities. This class provides an interface
 * between the controller layer and the data access layer, handling validation, exception
 * management, and logging for all drug-related operations. It encapsulates the complexity of
 * database interactions and provides a clean API for drug management functionality.
 */
public class DrugService {
  private DrugDAO drugDAO = new DrugDAO();
  private static final Logger logger = LoggerFactory.getLogger(DrugService.class);

  /**
   * Validates that the drug fields don't exceed database column length limits.
   *
   * @param drug The drug to validate
   * @throws IllegalArgumentException If any field exceeds its maximum length
   */
  private void validateFieldLengths(Drug drug) {
    InputValidationUtil.validateStringLength(
        drug.getDrugId(), FieldLengthConstants.DRUG_ID_MAX_LENGTH, "Drug ID");

    InputValidationUtil.validateStringLength(
        drug.getName(), FieldLengthConstants.DRUG_NAME_MAX_LENGTH, "Drug name");

    InputValidationUtil.validateStringLength(
        drug.getBenefits(), FieldLengthConstants.DRUG_BENEFITS_MAX_LENGTH, "Benefits");

    InputValidationUtil.validateStringLength(
        drug.getSideEffects(), FieldLengthConstants.DRUG_SIDE_EFFECTS_MAX_LENGTH, "Side effects");
  }

  /**
   * Adds a new drug to the system after performing validation checks. Verifies that the drug object
   * is not null and that the drug ID is unique before attempting to add it to the database.
   *
   * @param drug The Drug object to be added
   * @throws IllegalArgumentException If the drug is null or if a drug with the same ID already
   *     exists
   * @throws ServiceException If a database error occurs while adding the drug
   */
  public void addDrug(Drug drug) {
    if (drug == null) {
      throw new IllegalArgumentException("Drug cannot be null");
    }

    validateFieldLengths(drug);

    Drug existingDrug = getDrugById(drug.getDrugId());
    if (existingDrug != null) {
      throw new IllegalArgumentException("Drug ID already exists: " + drug.getDrugId());
    }
    try {
      drugDAO.addDrug(drug);
      logger.info("Drug added successfully: {}", drug.getDrugId());
    } catch (SQLException e) {
      logger.error("Error adding drug: {}", drug.getDrugId(), e);
      throw new ServiceException("Failed to add drug", e);
    }
  }

  /**
   * Retrieves all drugs from the database. Returns an empty list instead of throwing exceptions if
   * a database error occurs, providing graceful degradation for UI components that depend on this
   * data.
   *
   * @return A List containing all drugs in the database, or an empty list if an error occurs
   */
  public List<Drug> getAllDrugs() {
    try {
      return drugDAO.getAllDrugs();
    } catch (SQLException e) {
      logger.error("Error fetching drugs", e);
      return List.of(); // Return an empty list on error
    }
  }

  /**
   * Retrieves a specific drug by its ID. Validates that the provided ID is not null or empty before
   * querying the database.
   *
   * @param drugId The unique identifier of the drug to retrieve
   * @return The Drug object if found, or null if the drug doesn't exist or an error occurs
   * @throws IllegalArgumentException If the drugId is null or empty
   */
  public Drug getDrugById(String drugId) {
    if (drugId == null || drugId.isEmpty()) {
      throw new IllegalArgumentException("Drug ID cannot be empty");
    }
    try {
      return drugDAO.getDrugById(drugId);
    } catch (SQLException e) {
      logger.error("Error fetching drug: {}", drugId, e);
      return null;
    }
  }

  /**
   * Retrieves drugs by their name or name pattern. Useful for search functionality where exact drug
   * IDs aren't known. Validates that the name parameter is not null before querying.
   *
   * @param name The name or partial name to search for
   * @return A List of drugs matching the search criteria, or an empty list if none found or an
   *     error occurs
   * @throws IllegalArgumentException If the name parameter is null
   */
  public List<Drug> getDrugsByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Drug name cannot be null");
    }
    try {
      return drugDAO.getDrugsByName(name);
    } catch (SQLException e) {
      logger.error("Error fetching drugs by name: {}", name, e);
      return List.of();
    }
  }

  /**
   * Retrieves drugs by their side effects. Allows finding drugs with particular side effect
   * patterns, which is useful for medical analysis and prescribing decisions.
   *
   * @param sideEffects The side effect pattern to search for
   * @return A List of drugs matching the search criteria, or an empty list if none found or an
   *     error occurs
   * @throws IllegalArgumentException If the sideEffects parameter is null
   */
  public List<Drug> getDrugsBySideEffects(String sideEffects) {
    if (sideEffects == null) {
      throw new IllegalArgumentException("Side effects cannot be null");
    }
    try {
      return drugDAO.getDrugsBySideEffects(sideEffects);
    } catch (SQLException e) {
      logger.error("Error fetching drugs by side effects: {}", sideEffects, e);
      return List.of();
    }
  }

  /**
   * Retrieves drugs by their benefits. Allows finding drugs with specific therapeutic benefits,
   * which is useful for treatment planning and medical decision making.
   *
   * @param benefits The benefit pattern to search for
   * @return A List of drugs matching the search criteria, or an empty list if none found or an
   *     error occurs
   * @throws IllegalArgumentException If the benefits parameter is null
   */
  public List<Drug> getDrugsByBenefits(String benefits) {
    if (benefits == null) {
      throw new IllegalArgumentException("Benefits cannot be null");
    }
    try {
      return drugDAO.getDrugsByBenefits(benefits);
    } catch (SQLException e) {
      logger.error("Error fetching drugs by benefits: {}", benefits, e);
      return List.of();
    }
  }

  /**
   * Updates an existing drug's information in the database. Validates that the drug object is not
   * null before proceeding with the update.
   *
   * @param drug The Drug object containing updated information
   * @throws IllegalArgumentException If the drug is null
   * @throws ServiceException If a database error occurs while updating the drug
   */
  public void updateDrug(Drug drug) {
    if (drug == null) {
      throw new IllegalArgumentException("Drug cannot be null");
    }

    validateFieldLengths(drug);

    try {
      drugDAO.updateDrug(drug);
      logger.info("Drug updated successfully: {}", drug.getDrugId());
    } catch (SQLException e) {
      logger.error("Error updating drug: {}", drug.getDrugId(), e);
      throw new ServiceException("Failed to update drug", e);
    }
  }

  /**
   * Deletes a drug from the database by its ID. Validates that the provided ID is not null or empty
   * before attempting deletion. Note: This operation may fail if the drug is referenced by other
   * records (e.g., prescriptions), which would violate referential integrity constraints.
   *
   * @param drugId The unique identifier of the drug to delete
   * @throws IllegalArgumentException If the drugId is null or empty
   * @throws ServiceException If a database error occurs while deleting the drug
   */
  public void deleteDrug(String drugId) {
    if (drugId == null || drugId.isEmpty()) {
      throw new IllegalArgumentException("Drug ID cannot be empty");
    }
    try {
      drugDAO.deleteDrug(drugId);
      logger.info("Drug deleted successfully: {}", drugId);
    } catch (SQLException e) {
      logger.error("Error deleting drug: {}", drugId, e);
      throw new ServiceException("Failed to delete drug", e);
    }
  }
}
