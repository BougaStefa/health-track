package com.bougastefa.models;

public class Patient {
  private String patientId;
  private String firstName;
  private String surname;
  private String postcode;
  private String address;
  private String email;
  private String phone;

  // Constructor
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

  // Getters and Setters
  public String getPatientId() {
    return patientId;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
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

  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  // toString() method
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
