package com.infoshare.academy.highfive.mapper.request;


import com.infoshare.academy.highfive.domain.Role;
import com.infoshare.academy.highfive.domain.Team;
import com.infoshare.academy.highfive.dto.request.EmployeeRequest;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequestScoped
public class EmployeeRequestMapper {

  public EmployeeRequest mapParamsToRequest(HttpServletRequest request) throws ParseException {

    EmployeeRequest employeeRequest = new EmployeeRequest();
    LocalDate hireDate = LocalDate.parse(request.getParameter("hire_date"), DateTimeFormatter.ofPattern("yyyy-mm-dd"));

    employeeRequest.setFirstName((String) request.getAttribute("first_name"));
    employeeRequest.setSurname((String) request.getAttribute("surname"));
    employeeRequest.setHireDate(hireDate);
    employeeRequest.setHolidayEntitlement((Integer) request.getAttribute("holiday_entitlement"));
    employeeRequest.setAdditionalEntitlement((Integer) request.getAttribute("additional_entitlement"));
    employeeRequest.setLogin((String) request.getAttribute("login"));
    employeeRequest.setEmail((String) request.getAttribute("email"));
    employeeRequest.setPosition((String) request.getAttribute("position"));
    employeeRequest.setTeam((Long) request.getAttribute("team_id"));
    employeeRequest.setRole((Role) request.getAttribute("role"));

    return employeeRequest;
  }
}
