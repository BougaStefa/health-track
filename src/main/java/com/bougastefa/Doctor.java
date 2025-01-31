package com.bougastefa;

public class Doctor {
  private int doctorId;
  private String firstName;
  private String lastName;
  private String specialization;
  private String email;

  // Constructor
  public Doctor(
      int doctorId, String firstName, String lastName, String specialization, String email) {
    this.doctorId = doctorId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialization = specialization;
    this.email = email;
  }

  // Getters
  public int getDoctorId() {
    return this.doctorId;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public String getSpecialization() {
    return this.specialization;
  }

  public String getEmail() {
    return this.email;
  }

  // Setters
  public void setDoctorId(int doctorId) {
    this.doctorId = doctorId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
