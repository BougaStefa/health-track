package com.bougastefa.models;
/**
 * Represents a pharmaceutical drug in the healthcare system.
 * This class encapsulates the properties and behaviors of a medication,
 * including its identification, name, side effects, and benefits.
 */
public class Drug {
  /** Unique identifier for the drug */
  private String drugId;
  /** Name of the pharmaceutical drug */
  private String name;
  /** Known side effects of the drug */
  private String sideEffects;
  /** Medical benefits or intended effects of the drug */
  private String benefits;

  /**
   * Constructs a new Drug with all required properties.
   * 
   * @param drugId      Unique identifier for the drug
   * @param name        Name of the pharmaceutical drug
   * @param sideEffects Known side effects of the drug
   * @param benefits    Medical benefits of the drug
   */
  public Drug(String drugId, String name, String sideEffects, String benefits) {
    this.drugId = drugId;
    this.name = name;
    this.sideEffects = sideEffects;
    this.benefits = benefits;
  }

  /**
   * @return The drug's unique identifier
   */
  public String getDrugId() {
    return drugId;
  }

  /**
   * @param drugId The drug ID to set
   */
  public void setDrugId(String drugId) {
    this.drugId = drugId;
  }

  /**
   * @return The name of the drug
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The drug name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The side effects of the drug
   */
  public String getSideEffects() {
    return sideEffects;
  }

  /**
   * @param sideEffects The side effects to set for the drug
   */
  public void setSideEffects(String sideEffects) {
    this.sideEffects = sideEffects;
  }

  /**
   * @return The medical benefits of the drug
   */
  public String getBenefits() {
    return benefits;
  }

  /**
   * @param benefits The benefits to set for the drug
   */
  public void setBenefits(String benefits) {
    this.benefits = benefits;
  }

  /**
   * Returns a string representation of the Drug object.
   * 
   * @return A string containing all drug details in a formatted manner
   */
  @Override
  public String toString() {
    return "Drug{"
        + "drugId="
        + drugId
        + ", name='"
        + name
        + '\''
        + ", sideEffects='"
        + sideEffects
        + '\''
        + ", benefits='"
        + benefits
        + '\''
        + '}';
  }
}
