package com.bougastefa.models;
/**
 * Represents a medical doctor in the healthcare system.
 * This class contains personal and professional information about a doctor.
 */
public class Doctor {
  /** Unique identifier for the doctor */
  private String doctorId;
  /** Doctor's first name */
  private String firstName;
  /** Doctor's surname/last name */
  private String surname;
  /** Doctor's physical address */
  private String address;
  /** Doctor's email contact */
  private String email;
  /** Hospital where the doctor is affiliated */
  private String hospital;

  /**
   * Constructs a new Doctor with all required fields.
   * 
   * @param doctorId  Unique identifier for the doctor
   * @param firstName Doctor's first name
   * @param surname   Doctor's surname/last name
   * @param address   Doctor's physical address
   * @param email     Doctor's email contact
   * @param hospital  Hospital where the doctor is affiliated
   */
  public Doctor(String doctorId, String firstName, String surname, String address, String email, String hospital) {
    this.doctorId = doctorId;
    this.firstName = firstName;
    this.surname = surname;
    this.address = address;
    this.email = email;
    this.hospital = hospital;
  }

  // Getters and Setters
  
  /**
   * @return The hospital where the doctor is affiliated
   */
  public String getHospital() {
    return hospital;
  }

  /**
   * @param hospital The hospital to set as the doctor's affiliation
   */
  public void setHospital(String hospital) {
    this.hospital = hospital;
  }

  /**
   * @return The doctor's unique identifier
   */
  public String getDoctorId() {
    return doctorId;
  }

  /**
   * @param doctorId The doctor ID to set
   */
  public void setDoctorId(String doctorId) {
    this.doctorId = doctorId;
  }

  /**
   * @return The doctor's first name
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
   * @return The doctor's surname
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
   * @return The doctor's address
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
   * @return The doctor's email
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
   * Returns a string representation of the Doctor object.
   * 
   * @return A string containing all doctor details in a formatted manner
   */
  @Override
  public String toString() {
    return "Doctor{"
        + "doctorId="
        + doctorId
        + ", firstName='"
        + firstName
        + '\''
        + ", surname='"
        + surname
        + '\''
        + ", address='"
        + address
        + '\''
        + ", email='"
        + email
        + ", hospital='"
        + hospital
        + '\''
        + '}';
  }
}
