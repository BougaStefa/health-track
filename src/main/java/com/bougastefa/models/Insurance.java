package com.bougastefa.models;
/**
 * Represents an insurance provider in the healthcare system.
 * This class stores key information about insurance companies including
 * their identification, company name, address, and contact details.
 */
public class Insurance {
  /** Unique identifier for the insurance provider */
  private String insuranceId;
  /** Name of the insurance company */
  private String company;
  /** Physical address of the insurance company */
  private String address;
  /** Contact phone number for the insurance company */
  private String phone;

  /**
   * Constructs a new Insurance object with all required information.
   * 
   * @param insuranceId Unique identifier for the insurance provider
   * @param company     Name of the insurance company
   * @param address     Physical address of the insurance company
   * @param phone       Contact phone number for the insurance company
   */
  public Insurance(String insuranceId, String company, String address, String phone) {
    this.insuranceId = insuranceId;
    this.company = company;
    this.address = address;
    this.phone = phone;
  }

  /**
   * @return The insurance provider's unique identifier
   */
  public String getInsuranceId() {
    return insuranceId;
  }

  /**
   * @param insuranceId The insurance ID to set
   */
  public void setInsuranceId(String insuranceId) {
    this.insuranceId = insuranceId;
  }

  /**
   * @return The name of the insurance company
   */
  public String getCompany() {
    return company;
  }

  /**
   * @param company The company name to set
   */
  public void setCompany(String company) {
    this.company = company;
  }

  /**
   * @return The physical address of the insurance company
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address The address to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * @return The contact phone number of the insurance company
   */
  public String getPhone() {
    return phone;
  }

  /**
   * @param phone The phone number to set
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * Returns a string representation of the Insurance object.
   * 
   * @return A string containing all insurance provider details in a formatted manner
   */
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
