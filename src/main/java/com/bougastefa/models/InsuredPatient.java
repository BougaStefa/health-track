package com.bougastefa.models;

/**
 * Represents a patient with insurance coverage in the healthcare system.
 * This class extends the Patient class to include insurance-specific information.
 * It maintains all the basic patient properties while adding insurance details.
 */
public class InsuredPatient extends Patient {
  /** Insurance identifier linking the patient to their insurance provider */
  private String insuranceId;

  /**
   * Constructs a new InsuredPatient with all required patient information plus insurance details.
   * 
   * @param patientId   Unique identifier for the patient
   * @param firstName   Patient's first name
   * @param surname     Patient's surname/last name
   * @param postcode    Patient's postal code
   * @param address     Patient's physical address
   * @param email       Patient's email contact
   * @param phone       Patient's phone number
   * @param insuranceId Identifier linking the patient to their insurance provider
   */
  public InsuredPatient(
      String patientId,
      String firstName,
      String surname,
      String postcode,
      String address,
      String email,
      String phone,
      String insuranceId) {
    super(patientId, firstName, surname, postcode, address, email, phone);
    this.insuranceId = insuranceId;
  }

  /**
   * @return The patient's insurance identifier
   */
  public String getInsuranceId() {
    return insuranceId;
  }

  /**
   * @param insuranceId The insurance identifier to set
   */
  public void setInsuranceId(String insuranceId) {
    this.insuranceId = insuranceId;
  }

  /**
   * Returns a string representation of the InsuredPatient object.
   * Includes all properties inherited from the Patient class plus insurance details.
   * 
   * @return A string containing all patient and insurance details in a formatted manner
   */
  @Override
  public String toString() {
    return "InsuredPatient{"
        + "patientId="
        + getPatientId()
        + ", firstName='"
        + getFirstName()
        + '\''
        + ", surname='"
        + getSurname()
        + '\''
        + ", postcode='"
        + getPostcode()
        + '\''
        + ", address='"
        + getAddress()
        + '\''
        + ", email='"
        + getEmail()
        + '\''
        + ", phone='"
        + getPhone()
        + '\''
        + ", insuranceId='"
        + insuranceId
        + '\''
        + '}';
  }
}
