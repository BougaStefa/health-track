package com.bougastefa.models;

public class Drug {
  private String drugId;
  private String name;
  private String sideEffects;
  private String benefits;

  // Constructor
  public Drug(String drugId, String name, String sideEffects, String benefits) {
    this.drugId = drugId;
    this.name = name;
    this.sideEffects = sideEffects;
    this.benefits = benefits;
  }

  // Getters and Setters
  public String getDrugId() {
    return drugId;
  }

  public void setDrugId(String drugId) {
    this.drugId = drugId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSideEffects() {
    return sideEffects;
  }

  public void setSideEffects(String sideEffects) {
    this.sideEffects = sideEffects;
  }

  public String getBenefits() {
    return benefits;
  }

  public void setBenefits(String benefits) {
    this.benefits = benefits;
  }

  // toString() method
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
