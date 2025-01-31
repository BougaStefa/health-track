package com.bougastefa;

import java.time.LocalDate;

public class Patient {
  private int patientId;
  private String firstName;
  private String lastName;
  private LocalDate dateOfBirth;
  private String gender;
  private String email;
  private int insuranceId;

  // Constructor
  public Patient(
      int patientId,
      String firstName,
      String lastName,
      LocalDate dateOfBirth,
      String gender,
      String email,
      int insuranceId) {
    this.patientId = patientId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.gender = gender;
    this.email = email;
    this.insuranceId = insuranceId;
  }

  // Getters
  public int getPatientId() {
    return this.patientId;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public LocalDate getDateOfBirth() {
    return this.dateOfBirth;
  }

  public String getGender() {
    return this.gender;
  }

  public String getEmail() {
    return this.email;
  }

  public int getInsuranceId() {
    return this.insuranceId;
  }

  // Setters
  public void setPatientId(int patientId) {
    this.patientId = patientId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setInsuranceId(int insuranceId) {
    this.insuranceId = insuranceId;
  }
}
