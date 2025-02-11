package com.bougastefa.models;

import java.time.LocalDate;

public class Prescription {
    private String prescriptionId;
    private LocalDate dateOfPrescribe;
    private int dosage;
    private int duration;
    private String comment;
    private String drugId;
    private String doctorId;
    private String patientId;

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

    // Getters and Setters remain the same
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

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
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
