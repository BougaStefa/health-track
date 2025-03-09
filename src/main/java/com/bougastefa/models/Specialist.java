package com.bougastefa.models;

/**
 * Represents a medical specialist doctor in the healthcare system.
 * This class extends the Doctor class to include a medical specialization field,
 * distinguishing specialists from general practitioners.
 */
public class Specialist extends Doctor {
  /** The medical specialization area of the doctor (e.g., cardiology, neurology) */
  private String specialization;

  /**
   * Constructs a new Specialist with all required doctor information plus specialization.
   * 
   * @param doctorId       Unique identifier for the doctor
   * @param firstName      Doctor's first name
   * @param surname        Doctor's surname/last name
   * @param address        Doctor's physical address
   * @param email          Doctor's email contact
   * @param hospital       Hospital where the doctor is affiliated
   * @param specialization The medical specialization area of the doctor
   */
  public Specialist(
      String doctorId,
      String firstName,
      String surname,
      String address,
      String email,
      String hospital,
      String specialization) {
    super(doctorId, firstName, surname, address, email, hospital);
    this.specialization = specialization;
  }

  /**
   * @return The doctor's medical specialization area
   */
  public String getSpecialization() {
    return specialization;
  }

  /**
   * @param specialization The medical specialization to set
   */
  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  /**
   * Returns a string representation of the Specialist object.
   * Includes all properties inherited from the Doctor class plus specialization.
   * 
   * @return A string containing all specialist doctor details in a formatted manner
   */
  @Override
  public String toString() {
    return "Specialist{"
        + "doctorId="
        + getDoctorId()
        + ", firstName='"
        + getFirstName()
        + '\''
        + ", surname='"
        + getSurname()
        + '\''
        + ", address='"
        + getAddress()
        + '\''
        + ", email='"
        + getEmail()
        + '\''
        + ", hospital="
        + getHospital()
        + ", specialization='"
        + specialization
        + '\''
        + '}';
  }
}
