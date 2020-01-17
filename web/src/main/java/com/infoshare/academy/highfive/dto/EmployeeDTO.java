package com.infoshare.academy.highfive.dto;

import com.infoshare.academy.highfive.domain.Role;
import com.infoshare.academy.highfive.domain.Team;

import java.time.LocalDate;

public class EmployeeDTO {

  private Long Id;

  private String firstName;

  private String surname;

  private LocalDate hireDate;

  private int holidayEntitlement;

  private int additionalEntitlement;

  private Team teamId;

  private Role roleId;

  public EmployeeDTO(Long Id, String firstName, String surname, LocalDate hireDate, int holidayEntitlement, int additionalEntitlement, Team teamId, Role roleId) {
    this.Id = Id;
    this.firstName = firstName;
    this.surname = surname;
    this.hireDate = hireDate;
    this.holidayEntitlement = holidayEntitlement;
    this.additionalEntitlement = additionalEntitlement;
    this.teamId = teamId;
    this.roleId = roleId;
  }

  public Long getId() {
    return Id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getSurname() {
    return surname;
  }

  public LocalDate getHireDate() {
    return hireDate;
  }

  public int getHolidayEntitlement() {
    return holidayEntitlement;
  }

  public int getAdditionalEntitlement() {
    return additionalEntitlement;
  }


  public Team getTeamId() {
    return teamId;
  }

  public Role getRoleId() {
    return roleId;
  }
}