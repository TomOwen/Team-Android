package com.websoftmagic.team;

public class PatientResults {
	private String name = "";
    private String cityState = "";
    private String phone = "";
    private String patient_id = "";
    private String patient_name = "";
    private String patient_study_id = "";
    private String patient_doctor = "";
    

    public void setName(String name) {
     this.name = name;
    }

    public String getName() {
     return name;
    }

    public void setCityState(String cityState) {
     this.cityState = cityState;
    }

    public String getCityState() {
     return cityState;
    }
    /*
    public void setPhone(String phone) {
     this.phone = phone;
    }

    public String getPhone() {
     return phone;
    }
    */
    public void setPatient_id(String pid) {
        this.patient_id = pid;
       }

    public String getPatient_id() {
        return patient_id;
       }
    public void setPatient_name(String name) {
        this.patient_name = name;
       }

    public String getPatient_name() {
        return patient_name;
       }
    public void setPatient_study_id(String study) {
        this.patient_study_id = study;
       }

    public String getPatient_study_id() {
        return patient_study_id;
       }
    public void setPatient_doctor(String doctor) {
        this.patient_doctor = doctor;
       }

    public String getPatient_doctor() {
        return patient_doctor;
       }
}
