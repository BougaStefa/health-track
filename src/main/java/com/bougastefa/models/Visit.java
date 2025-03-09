package com.bougastefa.models;

import java.time.LocalDate;

/**
 * Represents a patient visit record in the healthcare system.
 * This class tracks the details of a medical consultation, including
 * the date, reported symptoms, medical diagnosis, and references to
 * the involved doctor and patient.
 */
public class Visit {
  /** Date when the consultation or medical visit occurred */
  private LocalDate dateOfVisit;
  /** Patient's reported symptoms or complaints */
  private String symptoms;
  /** Medical diagnosis or assessment provided by the doctor */
  private String diagnosis;
  /** Reference to the doctor who conducted the visit */
  private String doctorId;
  /** Reference to the patient who was examined */
  private String patientId;

  /**
   * Constructs a new Visit with all required information.
   * 
   * @param dateOfVisit Date when the consultation occurred
   * @param symptoms    Patient's reported symptoms
   * @param diagnosis   Medical diagnosis provided by the doctor
   * @param doctorId    Reference to the doctor who conducted the visit
   * @param patientId   Reference to the patient who was examined
   */
  public Visit(
      LocalDate dateOfVisit, String symptoms, String diagnosis, String doctorId, String patientId) {
    this.dateOfVisit = dateOfVisit;
    this.symptoms = symptoms;
    this.diagnosis = diagnosis;
    this.doctorId = doctorId;
    this.patientId = patientId;
  }

  /**
   * @return The date when the visit occurred
   */
  public LocalDate getDateOfVisit() {
    return dateOfVisit;
  }

  /**
   * @param dateOfVisit The visit date to set
   */
  public void setDateOfVisit(LocalDate dateOfVisit) {
    this.dateOfVisit = dateOfVisit;
  }

  /**
   * @return The patient's reported symptoms
   */
  public String getSymptoms() {
    return symptoms;
  }

  /**
   * @param symptoms The symptoms to set
   */
  public void setSymptoms(String symptoms) {
    this.symptoms = symptoms;
  }

  /**
   * @return The medical diagnosis provided
   */
  public String getDiagnosis() {
    return diagnosis;
  }

  /**
   * @param diagnosis The diagnosis to set
   */
  public void setDiagnosis(String diagnosis) {
    this.diagnosis = diagnosis;
  }

  /**
   * @return The ID of the doctor who conducted the visit
   */
  public String getDoctorId() {
    return doctorId;
  }

  /**
   * @param doctorId The doctor ID to set
   */
  public void setDoctorId(String doctorId) {
    this.doctorId = doctorId;
  }

  /**
   * @return The ID of the patient who was examined
   */
  public String getPatientId() {
    return patientId;
  }

  /**
   * @param patientId The patient ID to set
   */
  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  /**
   * Returns a string representation of the Visit object.
   * 
   * @return A string containing all visit details in a formatted manner
   */
  @Override
  public String toString() {
    return "Visit{"
        + "dateOfVisit="
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
