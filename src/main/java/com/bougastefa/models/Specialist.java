package com.bougastefa.models;

public class Specialist extends Doctor {
  private String specialization;

  // Constructor
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

  // Getters and Setters
  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  // toString() method
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
