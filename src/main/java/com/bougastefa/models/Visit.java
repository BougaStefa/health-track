package com.bougastefa.models;

import java.time.LocalDate;

public class Visit {
  private LocalDate dateOfVisit;
  private String symptoms;
  private String diagnosis;
  private String doctorId;
  private String patientId;

  // Constructor
  public Visit(
      LocalDate dateOfVisit, String symptoms, String diagnosis, String doctorId, String patientId) {
    this.dateOfVisit = dateOfVisit;
    this.symptoms = symptoms;
    this.diagnosis = diagnosis;
    this.doctorId = doctorId;
    this.patientId = patientId;
  }

  // Getters and Setters
  public LocalDate getDateOfVisit() {
    return dateOfVisit;
  }

  public void setDateOfVisit(LocalDate dateOfVisit) {
    this.dateOfVisit = dateOfVisit;
  }

  public String getSymptoms() {
    return symptoms;
  }

  public void setSymptoms(String symptoms) {
    this.symptoms = symptoms;
  }

  public String getDiagnosis() {
    return diagnosis;
  }

  public void setDiagnosis(String diagnosis) {
    this.diagnosis = diagnosis;
  }

  public String getDoctorId() {
    return doctorId;
  }

  public void setDoctorId(String doctorId) {
    this.doctorId = doctorId;
  }

  public String getPatientId() {
    return patientId;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  @Override
  public String toString() {
    return "Visit{"
        + ", dateOfVisit="
        + dateOfVisit
        + ", symptoms='"
        + symptoms
        + '\''
        + ", diagnosis='"
        + diagnosis
        + '\''
        + ", doctorId="
        + doctorId
        + ", patientId="
        + patientId
        + '}';
  }
}
