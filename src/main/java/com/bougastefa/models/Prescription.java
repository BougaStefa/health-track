package com.bougastefa.models;

import java.time.LocalDate;

/**
 * Represents a medical prescription in the healthcare system.
 * This class encapsulates all essential information about a prescription,
 * including medication details, dosage instructions, and references to the
 * associated doctor, patient, and drug.
 */
public class Prescription {
    /** Unique identifier for the prescription */
    private String prescriptionId;
    /** Date when the prescription was issued */
    private LocalDate dateOfPrescribe;
    /** Medication dosage in appropriate units (e.g., mg) */
    private int dosage;
    /** Duration of treatment in days */
    private int duration;
    /** Additional instructions or notes about the prescription */
    private String comment;
    /** Reference to the prescribed drug */
    private String drugId;
    /** Reference to the doctor who issued the prescription */
    private String doctorId;
    /** Reference to the patient for whom the prescription is issued */
    private String patientId;

    /**
     * Constructs a new Prescription with all required information.
     * 
     * @param prescriptionId   Unique identifier for the prescription
     * @param dateOfPrescribe  Date when the prescription was issued
     * @param dosage           Medication dosage in appropriate units
     * @param duration         Duration of treatment in days
     * @param comment          Additional instructions or notes
     * @param drugId           Reference to the prescribed drug
     * @param doctorId         Reference to the prescribing doctor
     * @param patientId        Reference to the patient
     */
    public Prescription(
        String prescriptionId,
        LocalDate dateOfPrescribe,
        int dosage,
        int duration,
        String comment,
        String drugId, 
        String doctorId,
        String patientId) {
        this.prescriptionId = prescriptionId;
        this.dateOfPrescribe = dateOfPrescribe;
        this.dosage = dosage;
        this.duration = duration;
        this.comment = comment;
        this.drugId = drugId;
        this.doctorId = doctorId;
        this.patientId = patientId;
    }

    /**
     * @return The prescription's unique identifier
     */
    public String getPrescriptionId() {
        return prescriptionId;
    }

    /**
     * @param prescriptionId The prescription ID to set
     */
    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    /**
     * @return The date when the prescription was issued
     */
    public LocalDate getDateOfPrescribe() {
        return dateOfPrescribe;
    }

    /**
     * @param dateOfPrescribe The prescription date to set
     */
    public void setDateOfPrescribe(LocalDate dateOfPrescribe) {
        this.dateOfPrescribe = dateOfPrescribe;
    }

    /**
     * @return The medication dosage
     */
    public int getDosage() {
        return dosage;
    }

    /**
     * @param dosage The dosage to set
     */
    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    /**
     * @return The duration of treatment in days
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration The duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @return Additional instructions or notes about the prescription
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment The comment or instructions to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return The ID of the prescribed drug
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
     * @return The ID of the prescribing doctor
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
     * @return The ID of the patient
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
     * Returns a string representation of the Prescription object.
     * 
     * @return A string containing all prescription details in a formatted manner
     */
    @Override
    public String toString() {
        return "Prescription{"
            + "prescriptionid='"
            + prescriptionId
            + '\''
            + ", dateprescribed="
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
            + ", patientid='"
            + patientId
            + '\''
            + '}';
    }
}
