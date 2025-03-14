package com.bougastefa.services;

import com.bougastefa.database.InsuranceDAO;
import com.bougastefa.models.Insurance;
import com.bougastefa.utils.FieldLengthConstants;
import com.bougastefa.utils.InputValidationUtil;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages the business logic for Insurance entities. This class provides a layer
 * between the controller and data access layers, handling validation, exception management, and
 * logging for all insurance-related operations. It ensures proper business rules are applied before
 * data is persisted or retrieved.
 */
public class InsuranceService {
  private InsuranceDAO insuranceDAO = new InsuranceDAO();
  private static final Logger logger = LoggerFactory.getLogger(InsuranceService.class);

  /**
   * Validates that the insurance fields don't exceed database column length limits.
   *
   * @param insurance The insurance to validate
   * @throws IllegalArgumentException If any field exceeds its maximum length
   */
  private void validateFieldLengths(Insurance insurance) {
    InputValidationUtil.validateStringLength(
        insurance.getInsuranceId(), FieldLengthConstants.INSURANCE_ID_MAX_LENGTH, "Insurance ID");

    InputValidationUtil.validateStringLength(
        insurance.getCompany(),
        FieldLengthConstants.INSURANCE_COMPANY_NAME_MAX_LENGTH,
        "Company name");

    InputValidationUtil.validateStringLength(
        insurance.getAddress(), FieldLengthConstants.INSURANCE_ADDRESS_MAX_LENGTH, "Address");

    InputValidationUtil.validateStringLength(
        insurance.getPhone(), FieldLengthConstants.INSURANCE_PHONE_MAX_LENGTH, "Phone");
  }

  /**
   * Adds a new insurance provider to the system after performing validation checks. Verifies that
   * the insurance object is not null and that the insurance ID doesn't already exist before
   * attempting to add it to the database.
   *
   * @param insurance The Insurance object to be added
   * @throws IllegalArgumentException If the insurance is null or if an insurance with the same ID
   *     already exists
   * @throws ServiceException If a database error occurs while adding the insurance
   */
  public void addInsurance(Insurance insurance) {
    if (insurance == null) {
      throw new IllegalArgumentException("Insurance cannot be null");
    }

    validateFieldLengths(insurance);

    Insurance existingInsurance = getInsuranceById(insurance.getInsuranceId());
    if (existingInsurance != null) {
      throw new IllegalArgumentException(
          "Insurance ID already exists: " + insurance.getInsuranceId());
    }
    try {
      insuranceDAO.addInsurance(insurance);
      logger.info("Insurance added successfully: {}", insurance.getInsuranceId());
    } catch (SQLException e) {
      logger.error("Error adding insurance: {}", insurance.getInsuranceId(), e);
      throw new ServiceException("Failed to add insurance", e);
    }
  }

  /**
   * Retrieves all insurance providers from the database. Returns an empty list instead of throwing
   * exceptions if a database error occurs, providing graceful degradation for UI components that
   * depend on this data.
   *
   * @return A List containing all insurance providers, or an empty list if an error occurs
   */
  public List<Insurance> getAllInsurances() {
    try {
      return insuranceDAO.getAllInsurances();
    } catch (SQLException e) {
      logger.error("Error fetching insurances", e);
      return List.of(); // Return an empty list on error
    }
  }

  /**
   * Retrieves a specific insurance provider by its ID. Validates that the provided ID is not null
   * or empty before querying the database.
   *
   * @param insuranceId The unique identifier of the insurance to retrieve
   * @return The Insurance object if found, or null if the insurance doesn't exist or an error
   *     occurs
   * @throws IllegalArgumentException If the insuranceId is null or empty
   */
  public Insurance getInsuranceById(String insuranceId) {
    if (insuranceId == null || insuranceId.isEmpty()) {
      throw new IllegalArgumentException("Insurance ID cannot be empty");
    }
    try {
      return insuranceDAO.getInsuranceById(insuranceId);
    } catch (SQLException e) {
      logger.error("Error fetching insurance: {}", insuranceId, e);
      return null;
    }
  }

  /**
   * Updates an existing insurance provider's information in the database. Validates that the
   * insurance object is not null before proceeding with the update.
   *
   * @param insurance The Insurance object containing updated information
   * @throws IllegalArgumentException If the insurance is null
   * @throws ServiceException If a database error occurs while updating the insurance
   */
  public void updateInsurance(Insurance insurance) {
    if (insurance == null) {
      throw new IllegalArgumentException("Insurance cannot be null");
    }

    validateFieldLengths(insurance);

    try {
      insuranceDAO.updateInsurance(insurance);
      logger.info("Insurance updated successfully: {}", insurance.getInsuranceId());
    } catch (SQLException e) {
      logger.error("Error updating insurance: {}", insurance.getInsuranceId(), e);
      throw new ServiceException("Failed to update insurance", e);
    }
  }

  /**
   * Deletes an insurance provider from the database by its ID. Validates that the provided ID is
   * not null or empty before attempting deletion. Note: This operation may fail if the insurance
   * provider is referenced by other records (e.g., insured patients), which would violate
   * referential integrity constraints.
   *
   * @param insuranceId The unique identifier of the insurance to delete
   * @throws IllegalArgumentException If the insuranceId is null or empty
   * @throws ServiceException If a database error occurs while deleting the insurance
   */
  public void deleteInsurance(String insuranceId) {
    if (insuranceId == null || insuranceId.isEmpty()) {
      throw new IllegalArgumentException("Insurance ID cannot be empty");
    }
    try {
      insuranceDAO.deleteInsurance(insuranceId);
      logger.info("Insurance deleted successfully: {}", insuranceId);
    } catch (SQLException e) {
      logger.error("Error deleting insurance: {}", insuranceId, e);
      throw new ServiceException("Failed to delete insurance", e);
    }
  }
}
