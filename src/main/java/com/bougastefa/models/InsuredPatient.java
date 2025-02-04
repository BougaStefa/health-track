package com.bougastefa.models;

public class InsuredPatient extends Patient {
  private String insuranceType;
  private String insuranceCompanyName;
  private int durationOfInsurance;

  // Constructor
  public InsuredPatient(
      String patientId,
      String firstName,
      String surname,
      String postcode,
      String address,
      String email,
      String phone,
      String insuranceType,
      String insuranceCompanyName,
      int durationOfInsurance) {
    super(patientId, firstName, surname, postcode, address, email, phone);
    this.insuranceType = insuranceType;
    this.insuranceCompanyName = insuranceCompanyName;
    this.durationOfInsurance = durationOfInsurance;
  }

  // Getters and Setters
  public String getInsuranceType() {
    return insuranceType;
  }

  public void setInsuranceType(String insuranceType) {
    this.insuranceType = insuranceType;
  }

  public String getInsuranceCompanyName() {
    return insuranceCompanyName;
  }

  public void setInsuranceCompanyName(String insuranceCompanyName) {
    this.insuranceCompanyName = insuranceCompanyName;
  }

  public int getDurationOfInsurance() {
    return durationOfInsurance;
  }

  public void setDurationOfInsurance(int durationOfInsurance) {
    this.durationOfInsurance = durationOfInsurance;
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
        + ", insuranceType='"
        + insuranceType
        + '\''
        + ", insuranceCompanyName='"
        + insuranceCompanyName
        + '\''
        + ", durationOfInsurance="
        + durationOfInsurance
        + '}';
  }
}
