package com.bougastefa.models;

public class Doctor {
  private String doctorId;
  private String firstName;
  private String surname;
  private String address;
  private String email;

  // Constructor
  public Doctor(String doctorId, String firstName, String surname, String address, String email) {
    this.doctorId = doctorId;
    this.firstName = firstName;
    this.surname = surname;
    this.address = address;
    this.email = email;
  }

  // Getters and Setters
  public String getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(String doctorId) {
    this.doctorId = doctorId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

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
        + '\''
        + '}';
  }
}
