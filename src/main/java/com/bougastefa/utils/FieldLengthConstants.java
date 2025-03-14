package com.bougastefa.utils;

/**
 * Contains constants for field length restrictions based on database schema. These values should
 * match the VARCHAR limits in the database.
 */
public class FieldLengthConstants {
  // Patient table constraints
  public static final int PATIENT_ID_MAX_LENGTH = 50;
  public static final int PATIENT_FIRSTNAME_MAX_LENGTH = 50;
  public static final int PATIENT_SURNAME_MAX_LENGTH = 50;
  public static final int PATIENT_POSTCODE_MAX_LENGTH = 15;
  public static final int PATIENT_ADDRESS_MAX_LENGTH = 100;
  public static final int PATIENT_EMAIL_MAX_LENGTH = 100;
  public static final int PATIENT_PHONE_MAX_LENGTH = 20;
  public static final int INSURANCE_ID_MAX_LENGTH = 50;

  // Doctor table constraints
  public static final int DOCTOR_ID_MAX_LENGTH = 50;
  public static final int DOCTOR_FIRSTNAME_MAX_LENGTH = 50;
  public static final int DOCTOR_SURNAME_MAX_LENGTH = 50;
  public static final int DOCTOR_ADDRESS_MAX_LENGTH = 100;
  public static final int DOCTOR_EMAIL_MAX_LENGTH = 50;
  public static final int DOCTOR_HOSPITAL_MAX_LENGTH = 100;
  public static final int SPECIALIZATION_MAX_LENGTH = 50;

  // Prescription table constraints
  public static final int PRESCRIPTION_ID_MAX_LENGTH = 100;
  public static final int PRESCRIPTION_DRUG_ID_MAX_LENGTH = 50;
  public static final int PRESCRIPTION_DOCTOR_ID_MAX_LENGTH = 50;
  public static final int PRESCRIPTION_PATIENT_ID_MAX_LENGTH = 50;
  public static final int PRESCRIPTION_COMMENT_MAX_LENGTH = 200;

  // Drug table constraints
  public static final int DRUG_ID_MAX_LENGTH = 50;
  public static final int DRUG_NAME_MAX_LENGTH = 50;
  public static final int DRUG_BENEFITS_MAX_LENGTH = 150;
  public static final int DRUG_SIDE_EFFECTS_MAX_LENGTH = 150;

  // Insurance table constraints
  public static final int INSURANCE_COMPANY_ID_MAX_LENGTH = 50;
  public static final int INSURANCE_COMPANY_NAME_MAX_LENGTH = 100;
  public static final int INSURANCE_ADDRESS_MAX_LENGTH = 100;
  public static final int INSURANCE_PHONE_MAX_LENGTH = 20;

  // Visit table constraints
  public static final int VISIT_DOCTOR_ID_MAX_LENGTH = 100;
  public static final int VISIT_PATIENT_ID_MAX_LENGTH = 100;
  public static final int VISIT_DIAGNOSIS_MAX_LENGTH = 200;
  public static final int VISIT_SYMPTOMS_MAX_LENGTH = 200;
}
