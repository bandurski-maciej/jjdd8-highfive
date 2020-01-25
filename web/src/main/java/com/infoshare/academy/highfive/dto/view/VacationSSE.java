package com.infoshare.academy.highfive.dto.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.infoshare.academy.highfive.domain.VacationStatus;

import java.time.LocalDateTime;

public class VacationSSE {
    private static LocalDateTime dateOfRequest;
    private Long id;
    @JsonProperty("employee_id")
    private Long employeeId;

    @JsonProperty("first_name")
    private String firstName;

    private String surname;

    @JsonProperty("iso_date_of_request")
    private String dateOfRequestIso;

    @JsonProperty("vacation_status")
    private VacationStatus vacationStatus;

    public static void setDateOfRequest(LocalDateTime dateOfRequest) {
        VacationSSE.dateOfRequest = dateOfRequest;
    }

    public static LocalDateTime getDateOfRequest() {
        return dateOfRequest;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public void setDateOfRequestIso(String dateOfRequestIso) {
        this.dateOfRequestIso = dateOfRequestIso;
    }

    public String getDateOfRequestIso() {
        return dateOfRequestIso;
    }

    public void setVacationStatus(VacationStatus vacationStatus) {
        this.vacationStatus = vacationStatus;
    }

    public VacationStatus getVacationStatus() {
        return vacationStatus;
    }
}
