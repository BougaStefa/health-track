package com.bougastefa.models;

import java.time.LocalDate;

public class Prescription {
  private String prescriptionId;
  private LocalDate dateOfPrescribe;
  private String dosage;
  private String duration;
  private String comment;
  private String doctorId;
  private String patientId; // Foreign key to Patient
  private String drugId; // Foreign key to Drug

  // Constructor
  public Prescription(
      String prescriptionId,
      LocalDate dateOfPrescribe,
      String dosage,
      String duration,
      String comment,
      String doctorId,
      String patientId,
      String drugId) {
    this.prescriptionId = prescriptionId;
    this.dateOfPrescribe = dateOfPrescribe;
    this.dosage = dosage;
    this.duration = duration;
    this.comment = comment;
    this.doctorId = doctorId;
    this.patientId = patientId;
    this.drugId = drugId;
  }

  // Getters and Setters
  public String getPrescriptionId() {
    return prescriptionId;
  }

  public void setPrescriptionId(String prescriptionId) {
    this.prescriptionId = prescriptionId;
  }

  public LocalDate getDateOfPrescribe() {
    return dateOfPrescribe;
  }

  public void setDateOfPrescribe(LocalDate dateOfPrescribe) {
    this.dateOfPrescribe = dateOfPrescribe;
  }

  public String getDosage() {
    return dosage;
  }

  public void setDosage(String dosage) {
    this.dosage = dosage;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
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

  public String getDrugId() {
    return drugId;
  }

  public void setDrugId(String drugId) {
    this.drugId = drugId;
  }

  @Override
  public String toString() {
    return "Prescription{"
        + "prescriptionid='"
        + prescriptionId
        + '\''
        + ", dateprescriberd="
        + dateOfPrescribe
        + ", dosage="
        + dosage
        + ", duration="
        + duration
        + ", comment='"
        + comment
        + '\''
        + ", drugid='"
        + drugId
        + '\''
        + ", doctorid='"
        + doctorId
        + '\''
        + // Added doctorid
        ", patientid='"
        + patientId
        + '\''
        + '}';
  }
}
