package com.infoshare.academy.highfive.service;

import com.infoshare.academy.highfive.dao.*;
import com.infoshare.academy.highfive.domain.*;
import com.infoshare.academy.highfive.dto.request.VacationRequest;
import com.infoshare.academy.highfive.dto.view.*;
import com.infoshare.academy.highfive.mapper.entity.VacationEmployeeMapper;
import com.infoshare.academy.highfive.mapper.entity.VacationMapper;
import com.infoshare.academy.highfive.mapper.entity.VacationMonthMapper;
import com.infoshare.academy.highfive.service.configuration.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequestScoped
public class VacationService {

  Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

  @Inject
  EmployeeDao employeeDao;
  @Inject
  HolidayDao holidayDao;
  @Inject
  VacationDao vacationDao;
  @Inject
  VacationMapper vacationMapper;
  @Inject
  EntitlementDao entitlementDao;
  @Inject
  MailSender mailSender;
  @Inject
  StatisticDao statisticDao;
  @Inject
  VacationMonthMapper vacationMonthMapper;
  @Inject
  VacationEmployeeMapper vacationEmployeeMapper;

  private String status;

  public String getStatus() {
    return status;
  }

  public void addVacation(VacationRequest vacationRequest) {

    Employee employee = employeeDao.findById(vacationRequest.getEmployeeId());
    Entitlement entitlement = entitlementDao.getEntitlementByEmployeeId(employee);

    List<LocalDate> vacationDaysList = creatingVacationDaysList(vacationRequest);
    int entitledDays = getEmployeeEntitlement(entitlement, vacationRequest);
    int daysOff = calculatingDaysAmount(vacationDaysList);


    if (vacationRequest.getDateTo().isBefore((vacationRequest.getDateFrom()))) {

      status = "wrong_date";
    } else {

      if (checkIfVacationIsAvailable(daysOff, entitledDays)) {

        vacationDao.addVacation(vacationMapper.mapRequestToEntity(employee, vacationRequest));
        updateHolidayEntitlement(vacationRequest, daysOff, entitlement);

        vacationDaysList.forEach(day -> {
          Statistic statisticMonth = statisticDao.getStatisticByMonth(day.getMonthValue());
          long vacationDaysCount = statisticMonth.getVacationDaysCount();
          statisticMonth.setVacationDaysCount(vacationDaysCount + 1);
          statisticDao.saveStatistic(statisticMonth);
        });

        LOGGER.info("Vacation added successfully");
        status = "ok";
      } else {
        status = "exceeding_entitlement";

        LOGGER.warn("Given days are exceeding entitledDays.");
      }
    }
  }

  void updateHolidayEntitlement(VacationRequest vacationRequest, int daysOff, Entitlement entitlement) {
    entitlement.setVacationTaken(entitlement.getVacationTaken() + daysOff);

    if (vacationRequest.getVacationType().equals(VacationType.PARENTAL)) {

      entitlement.setAdditionalLeft(entitlement.getAdditionalLeft() - daysOff);

    } else if (vacationRequest.getVacationType().equals(VacationType.ON_DEMAND)) {

      entitlement.setOnDemandVacationLeft(entitlement.getOnDemandVacationLeft() - daysOff);
      entitlement.setVacationLeft(entitlement.getVacationLeft() - daysOff);

    } else {

      entitlement.setVacationLeft(entitlement.getVacationLeft() - daysOff);

    }
    entitlementDao.updateEntitlement(entitlement);
  }

  int getEmployeeEntitlement(Entitlement entitlement, VacationRequest vacationRequest) {

    if (vacationRequest.getVacationType().equals(VacationType.PARENTAL)) {

      return entitlement.getAdditionalLeft();

    } else if (vacationRequest.getVacationType().equals(VacationType.ON_DEMAND)) {

      return entitlement.getOnDemandVacationLeft();

    } else {

      return entitlement.getVacationLeft() + entitlement.getPreviousYearLeft();

    }

  }

  boolean checkIfVacationIsAvailable(int daysOff, int entitlement) {

    return daysOff <= entitlement;

  }

  List<LocalDate> creatingVacationDaysList(VacationRequest vacationRequest) {

    LocalDate start = vacationRequest.getDateFrom();
    LocalDate end = vacationRequest.getDateTo();
    List<LocalDate> totalDates = new LinkedList<>();
    while (!start.isAfter(end)) {
      totalDates.add(start);
      start = start.plusDays(1);
    }

    return totalDates;

  }

  Integer calculatingDaysAmount(List<LocalDate> totalDates) {

    List<LocalDate> holidays = new LinkedList<>();
    List<LocalDate> publicHolidayList = holidayDao.listAllHolidayDates();

    for (LocalDate day : totalDates) {
      if ("SATURDAY".equals(day.getDayOfWeek().name())) {

        holidays.add(day);

      } else if ("SUNDAY".equals(day.getDayOfWeek().name())) {

        holidays.add(day);

      } else if (publicHolidayList.contains(day)) {

        holidays.add(day);

      }

    }

    return totalDates.size() - holidays.size();

  }

  @Transactional
  public List<VacationView> listAllPendingRequests() {

    return vacationDao.getVacationList(VacationStatus.APPLIED)
      .stream()
      .map(vacation -> vacationMapper.mapEntityToView(vacation))
      .collect(Collectors.toList());

  }

  @Transactional
  public List<VacationSSE> listAllPendingRequestsSSE() {

    return listAllPendingRequests()
      .stream()
      .map(vacation -> vacationMapper.mapEntityToSSE(vacation))
      .collect(Collectors.toList());

  }

  @Transactional
  public void changeVacationStatus(Long vacationId, VacationStatus vacationStatus) throws IOException {

    Vacation vacation = vacationDao.getVacationById(vacationId);
    vacation.setVacationStatus(vacationStatus);
    vacationDao.updateVacationStatus(vacation);

    if (vacationStatus.equals(VacationStatus.APPROVED)) {

      mailSender.sendApprove(vacation.getEmployee().getEmail());

    } else {

      Entitlement entitlement = entitlementDao.getEntitlementByEmployeeId(vacation.getEmployee());
      List<LocalDate> vacationDaysList = creatingVacationDaysListToReturn(vacation);
      int daysOff = calculatingDaysAmount(vacationDaysList);
      int vacationTaken = entitlement.getVacationTaken();
      entitlement.setVacationTaken(vacationTaken - daysOff);
      entitlementDao.save(entitlement);

      vacationDaysList.forEach(day -> {
        Statistic statisticMonth = statisticDao.getStatisticByMonth(day.getMonthValue());
        long vacationDaysCount = statisticMonth.getVacationDaysCount();
        statisticMonth.setVacationDaysCount(vacationDaysCount - 1);
        statisticDao.saveStatistic(statisticMonth);
      });

      if (vacation.getVacationType().equals(VacationType.VACATION)) {

        int vacationLeft = entitlement.getVacationLeft();
        entitlement.setVacationLeft(vacationLeft + daysOff);
        entitlementDao.save(entitlement);

      } else if (vacation.getVacationType().equals(VacationType.ON_DEMAND)) {

        int onDemandLeft = entitlement.getOnDemandVacationLeft();
        entitlement.setOnDemandVacationLeft(onDemandLeft + daysOff);
        entitlement.setVacationLeft(entitlement.getVacationLeft() + daysOff);
        entitlementDao.save(entitlement);

      } else if (vacation.getVacationType().equals(VacationType.PARENTAL)) {

        int parentalLeft = entitlement.getAdditionalLeft();
        entitlement.setAdditionalLeft(parentalLeft + daysOff);
        entitlementDao.save(entitlement);

      }

      mailSender.sendReject(vacation.getEmployee().getEmail());

    }

  }

  List<LocalDate> creatingVacationDaysListToReturn(Vacation vacation) {

    LocalDate start = vacation.getVacationFrom();
    LocalDate end = vacation.getVacationTo();
    List<LocalDate> totalDates = new LinkedList<>();
    while (!start.isAfter(end)) {
      totalDates.add(start);
      start = start.plusDays(1);
    }

    return totalDates;

  }

  public VacationStatisticView getDashboardStatistic() {

    VacationStatisticView vacationStatisticView = new VacationStatisticView();

    List<VacationView> vacationListNextMonth = new ArrayList<>();
    List<VacationView> vacationListThisMonth = new ArrayList<>();

    vacationDao.getAmountOfAbsentNextMonth().forEach(v -> vacationListNextMonth
      .add(vacationMapper.mapEntityToView(v)));

    vacationDao.getAmountOfAbsentThisMonth().forEach(v -> vacationListThisMonth
      .add(vacationMapper.mapEntityToView(v)));

    vacationStatisticView.setNextMonthTotal(vacationListNextMonth.stream()
      .map(VacationView::getEmployeeId)
      .collect(Collectors.toSet()).size());

    vacationStatisticView.setCurrentMonthTotal(vacationListThisMonth.stream()
      .map(VacationView::getEmployeeId)
      .collect(Collectors.toSet()).size());

    vacationStatisticView.setPendingRequests(vacationDao.getVacationList(VacationStatus.APPLIED).size());
    vacationStatisticView.setAbsentToday(vacationDao.getAmountOfAbsentToday());

    return vacationStatisticView;

  }

  public Double getApprovedToDeniedVacationRatio() {

    double approvedVacation = vacationDao.getVacationList(VacationStatus.APPROVED).size();
    double deniedVacation = vacationDao.getVacationList(VacationStatus.DENIED).size();

    return approvedVacation / (deniedVacation + approvedVacation) * 100;

  }

  public List<VacationEmployeeView> getEmployeesByVacationTaken() {

    List<VacationEmployeeView> vacationEmployeeViews = new ArrayList<>();

    entitlementDao.getVacationTakenByEmployee()
      .forEach(v -> vacationEmployeeViews.add(vacationEmployeeMapper.mapEntityToView(v)));

    return vacationEmployeeViews;

  }

  public List<Entitlement> getTeamByVacationTaken() {

    return entitlementDao.getVacationTakenByTeam();

  }


  public List<VacationMonthView> getMonthStatistic() {

    List<VacationMonthView> vacationMonthView = new ArrayList<>();

    statisticDao.getStatisticListSortedByVacationDaysCount().
      forEach(v -> vacationMonthView.add(vacationMonthMapper.mapEntityToView(v)));

    return vacationMonthView;

  }

  @Transactional
  public List<VacationView> getTeamVacationByDatesInRange(Long id, LocalDate startDate, LocalDate endDate) {
    List<VacationView> vacationCalendarViewList = new ArrayList<>();
    Optional<Employee> employee = employeeDao.getById(id);
    Team team = employee.get().getTeam();
    vacationDao.getVacationByTeamInDatesRange(team, startDate, endDate).
      forEach(v -> vacationCalendarViewList.add(vacationMapper.mapEntityToView(v)));

    return vacationCalendarViewList;

  }

  @Transactional
  public List<VacationView> listAllVacation(LocalDate startDate, LocalDate endDate) {

    return vacationDao.getAllVacation(startDate, endDate)
      .stream()
      .map(vacation -> vacationMapper.mapEntityToView(vacation))
      .collect(Collectors.toList());

  }

  public List<VacationView> listAllEmployeeVacation(Long id) {
    Optional<Employee> employee = employeeDao.getById(id);
    return vacationDao.getVacationByEmployee(employee.get()).stream()
      .map(vacation -> vacationMapper.mapEntityToView(vacation))
      .collect(Collectors.toList());

  }

  @Transactional
  public void removeVacation(Long id) {


    Vacation vacation = vacationDao.getVacationById(id);
    Entitlement entitlement = entitlementDao.getEntitlementByEmployeeId(vacation.getEmployee());
    List<LocalDate> vacationDaysList = creatingVacationDaysListToReturn(vacation);
    int daysOff = calculatingDaysAmount(vacationDaysList);
    int vacationTaken = entitlement.getVacationTaken();
    entitlement.setVacationTaken(vacationTaken - daysOff);
    entitlementDao.save(entitlement);

    if (vacation.getVacationType().equals(VacationType.VACATION)) {

      int vacationLeft = entitlement.getVacationLeft();
      entitlement.setVacationLeft(vacationLeft + daysOff);
      entitlementDao.save(entitlement);

    } else if (vacation.getVacationType().equals(VacationType.ON_DEMAND)) {

      int onDemandLeft = entitlement.getOnDemandVacationLeft();
      entitlement.setOnDemandVacationLeft(onDemandLeft + daysOff);
      entitlementDao.save(entitlement);

    } else if (vacation.getVacationType().equals(VacationType.PARENTAL)) {

      int parentalLeft = entitlement.getAdditionalLeft();
      entitlement.setAdditionalLeft(parentalLeft + daysOff);
      entitlementDao.save(entitlement);

    }

    vacationDaysList.forEach(day -> {
      Statistic statisticMonth = statisticDao.getStatisticByMonth(day.getMonthValue());
      long vacationDaysCount = statisticMonth.getVacationDaysCount();
      statisticMonth.setVacationDaysCount(vacationDaysCount - 1);
      statisticDao.saveStatistic(statisticMonth);
    });

    vacationDao.removeVacation(vacationDao.getVacationById(id));

  }

  public int getVacationEntitlement(Employee employee) {
    return entitlementDao.getEntitlementByEmployee(employee).getVacationLeft();
  }

  public int getOnDemandEntitlement(Employee employee) {
    return entitlementDao.getEntitlementByEmployee(employee).getOnDemandVacationLeft();
  }

  public int getParentalEntitlement(Employee employee) {
    return entitlementDao.getEntitlementByEmployee(employee).getAdditionalLeft();
  }
}

