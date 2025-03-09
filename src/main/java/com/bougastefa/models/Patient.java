package com.bougastefa.models;

/**
 * Represents a patient in the healthcare system.
 * This class encapsulates all fundamental patient information including
 * personal details and contact information.
 */
public class Patient {
  /** Unique identifier for the patient */
  private String patientId;
  /** Patient's first name */
  private String firstName;
  /** Patient's surname/last name */
  private String surname;
  /** Patient's postal code for geographical location */
  private String postcode;
  /** Patient's physical address */
  private String address;
  /** Patient's email contact for electronic communications */
  private String phone;
  /** Patient's phone number for direct contact */
  private String email;

  /**
   * Constructs a new Patient with all required information.
   * 
   * @param patientId Unique identifier for the patient
   * @param firstName Patient's first name
   * @param surname   Patient's surname/last name
   * @param postcode  Patient's postal code
   * @param address   Patient's physical address
   * @param email     Patient's email contact
   * @param phone     Patient's phone number
   */
  public Patient(
      String patientId,
      String firstName,
      String surname,
      String postcode,
      String address,
      String email,
      String phone) {
    this.patientId = patientId;
    this.firstName = firstName;
    this.surname = surname;
    this.postcode = postcode;
    this.address = address;
    this.email = email;
    this.phone = phone;
  }

  /**
   * @return The patient's unique identifier
   */
  public String getPatientId() {
    return patientId;
  }

  /**
   * @param patientId The patient ID to set
   */
  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  /**
   * @return The patient's first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @param firstName The first name to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return The patient's surname
   */
  public String getSurname() {
    return surname;
  }

  /**
   * @param surname The surname to set
   */
  public void setSurname(String surname) {
    this.surname = surname;
  }

  /**
   * @return The patient's postal code
   */
  public String getPostcode() {
    return postcode;
  }

  /**
   * @param postcode The postal code to set
   */
  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  /**
   * @return The patient's physical address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address The address to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * @return The patient's email contact
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email The email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return The patient's phone number
   */
  public String getPhone() {
    return phone;
  }

  /**
   * @param phone The phone number to set
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * Returns a string representation of the Patient object.
   * 
   * @return A string containing all patient details in a formatted manner
   */
  @Override
  public String toString() {
    return "Patient{"
        + "patientId="
        + patientId
        + ", firstName='"
        + firstName
        + '\''
        + ", surname='"
        + surname
        + '\''
        + ", postcode='"
        + postcode
        + '\''
        + ", address='"
        + address
        + '\''
        + ", email='"
        + email
        + '\''
        + ", phone='"
        + phone
        + '\''
        + '}';
  }
}
