package com.bougastefa.models;

public class Insurance {
  private String insuranceId;
  private String company;
  private String address;
  private String phone;

  // Constructor
  public Insurance(String insuranceId, String company, String address, String phone) {
    this.insuranceId = insuranceId;
    this.company = company;
    this.address = address;
    this.phone = phone;
  }

  // Getters and Setters
  public String getInsuranceId() {
    return insuranceId;
  }

  public void setInsuranceId(String insuranceId) {
    this.insuranceId = insuranceId;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
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
    return "Insurance{"
        + "insuranceId="
        + insuranceId
        + ", company='"
        + company
        + '\''
        + ", address='"
        + address
        + '\''
        + ", phone='"
        + phone
        + '\''
        + '}';
  }
}
