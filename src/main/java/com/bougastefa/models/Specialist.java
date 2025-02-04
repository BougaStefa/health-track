package com.bougastefa.models;

public class Specialist extends Doctor {
  private String specialization;
  private int experience;

  // Constructor
  public Specialist(
      String doctorId,
      String firstName,
      String surname,
      String address,
      String email,
      String specialization,
      int experience) {
    super(doctorId, firstName, surname, address, email);
    this.specialization = specialization;
    this.experience = experience;
  }

  // Getters and Setters
  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public int getExperience() {
    return experience;
  }

  public void setExperience(int experience) {
    this.experience = experience;
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
        + ", specialization='"
        + specialization
        + '\''
        + ", experience="
        + experience
        + '}';
  }
}
