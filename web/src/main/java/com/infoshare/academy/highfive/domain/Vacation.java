package com.infoshare.academy.highfive.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NamedQueries({
  @NamedQuery(name = "Vacation.findVacationByStatus", query = "SELECT vacation " +
    "FROM Vacation vacation " +
    "JOIN FETCH Employee employee ON vacation.employee = employee " +
    "WHERE vacation.vacationStatus = :status"),
  @NamedQuery(name = "Vacation.findVacationById", query = "SELECT vacation " +
    "FROM Vacation vacation " +
    "WHERE vacation.id = :vacationId"),
  @NamedQuery(name = "Vacation.findAbsentToday", query = "SELECT vacation " +
    "FROM Vacation vacation " +
    "WHERE vacation.vacationStatus = :status " +
    "AND (vacation.vacationFrom <= :today " +
    "AND vacation.vacationTo >= :today)"),
  @NamedQuery(name = "Vacation.findAbsentByMonth", query = "SELECT vacation " +
    "FROM Vacation vacation " +
    "WHERE vacation.vacationStatus = :status " +
    "AND (vacation.vacationFrom BETWEEN :firstDayOfMonth AND :lastDayOfMonth " +
    "OR vacation.vacationTo BETWEEN :firstDayOfMonth AND :lastDayOfMonth)"),
  @NamedQuery(name = "Vacation.getByTeamInDateRange", query = "SELECT vacation " +
    "FROM Vacation vacation " +
    "JOIN FETCH Employee employee ON vacation.employee = employee " +
    "JOIN FETCH Team team ON employee.team = team " +
    "WHERE team = :team " +
    "AND ((:startDate BETWEEN vacation.vacationFrom AND vacation.vacationTo) " +
    "OR (:endDate BETWEEN vacation.vacationFrom AND vacation.vacationTo)) " +
    "OR (vacation.vacationFrom BETWEEN :startDate AND :endDate) " +
    "OR (vacation.vacationTo BETWEEN :startDate AND :endDate)"),
  @NamedQuery(name = "Vacation.getAllVacation", query = "SELECT vacation " +
    "FROM Vacation vacation " +
    "WHERE ((:startDate BETWEEN vacation.vacationFrom AND vacation.vacationTo) " +
    "OR (:endDate BETWEEN vacation.vacationFrom AND vacation.vacationTo)) " +
    "OR (vacation.vacationFrom BETWEEN :startDate AND :endDate) " +
    "OR (vacation.vacationTo BETWEEN :startDate AND :endDate)"),
  @NamedQuery(name = "Vacation.getAllEmployeeVacation", query = "SELECT vacation " +
    "FROM Vacation vacation " +
    "WHERE vacation.employee = :employee " +
    "AND (vacation.vacationStatus = :approved OR vacation.vacationStatus = :applied) " +
    "AND vacation.vacationFrom > :today"),
  @NamedQuery(name = "Vacation.findPendingOldRequests", query = "SELECT vacation " +
    "FROM Vacation vacation " +
    "WHERE vacation.vacationStatus = :status AND vacation.dateOfRequest < :date AND vacation.reminderEmailSent LIKE '0'")
})

@Entity
@Table(name = "vacation")
public class Vacation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @JoinColumn(name = "employee_id", nullable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private Employee employee;

  @Column(name = "from_date", nullable = false)
  private LocalDate vacationFrom;

  @Column(name = "to_date", nullable = false)
  private LocalDate vacationTo;

  @Column(name = "date_of_request", nullable = false)
  private LocalDateTime dateOfRequest;

  @Enumerated(EnumType.STRING)
  @Column(name = "vacation_type")
  private VacationType vacationType;

  @Enumerated(EnumType.STRING)
  @Column(name = "vacation_status")
  private VacationStatus vacationStatus;

  @Column(name = "reminder_email_sent")
  private String reminderEmailSent;

  public String getReminderEmailSent() {
    return reminderEmailSent;
  }

  public void setReminderEmailSent(String reminderEmailSent) {
    this.reminderEmailSent = reminderEmailSent;
  }

  public Long getId() {
    return id;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public LocalDate getVacationFrom() {
    return vacationFrom;
  }

  public void setVacationFrom(LocalDate vacationFrom) {
    this.vacationFrom = vacationFrom;
  }

  public LocalDate getVacationTo() {
    return vacationTo;
  }

  public void setVacationTo(LocalDate vacationTo) {
    this.vacationTo = vacationTo;
  }

  public LocalDateTime getDateOfRequest() {
    return dateOfRequest;
  }

  public void setDateOfRequest(LocalDateTime dateOfRequest) {
    this.dateOfRequest = dateOfRequest;
  }

  public VacationType getVacationType() {
    return vacationType;
  }

  public void setVacationType(VacationType vacationType) {
    this.vacationType = vacationType;
  }

  public VacationStatus getVacationStatus() {
    return vacationStatus;
  }

  public void setVacationStatus(VacationStatus vacationStatus) {
    this.vacationStatus = vacationStatus;
  }
}
