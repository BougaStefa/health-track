package com.bougastefa.models;

public class InsuredPatient extends Patient {
  private String insuranceId;

  // Constructor
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

  // Getters and Setters
  public String getInsuranceId() {
    return insuranceId;
  }

  public void setInsuranceId(String insuranceId) {
    this.insuranceId = insuranceId;
  }

  // toString() method
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
