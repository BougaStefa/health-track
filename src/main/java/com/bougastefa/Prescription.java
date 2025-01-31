package com.bougastefa;

import java.time.LocalDate;

public class Prescription {
  private int prescriptionId;
  private int patientId;
  private int doctorId;
  private int drugId;
  private String dosage;
  private LocalDate dateIssued;
  private int duration;
  private String notes;

  // Constructor
  public Prescription(
      int prescriptionId,
      int patientId,
      int doctorId,
      int drugId,
      String dosage,
      LocalDate dateIssued,
      int duration,
      String notes) {
    this.prescriptionId = prescriptionId;
    this.patientId = patientId;
    this.doctorId = doctorId;
    this.drugId = drugId;
    this.dosage = dosage;
    this.dateIssued = dateIssued;
    this.duration = duration;
    this.notes = notes;
  }

  // Setters
  public void setPrescriptionId(int prescriptionId) {
    this.prescriptionId = prescriptionId;
  }

  public void setPatientId(int patientId) {
    this.patientId = patientId;
  }

  public void setDoctorId(int doctorId) {
    this.doctorId = doctorId;
  }

  public void setDrugId(int drugId) {
    this.drugId = drugId;
  }

  public void setDosage(String dosage) {
    this.dosage = dosage;
  }

  public void setDateIssues(LocalDate dateIssued) {
    this.dateIssued = dateIssued;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  // Getters
  public int getPrescriptionId() {
    return this.prescriptionId;
  }

  public int getPatientId() {
    return this.patientId;
  }

  public int getDoctorId() {
    return this.doctorId;
  }

  public int getDrugId() {
    return this.drugId;
  }

  public String getDosage() {
    return this.dosage;
  }

  public LocalDate getDateIssued() {
    return this.dateIssued;
  }

  public int getDuration() {
    return this.duration;
  }

  public String getNotes() {
    return this.notes;
  }
}
