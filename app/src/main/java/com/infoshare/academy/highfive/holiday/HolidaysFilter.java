package com.infoshare.academy.highfive.holiday;

import com.infoshare.academy.highfive.mapper.HolidayMapper;
import com.infoshare.academy.highfive.tool.ColorsSet;
import com.infoshare.academy.highfive.tool.ParseStringToIsoDate;
import com.infoshare.academy.highfive.tool.TerminalCleaner;
import com.infoshare.academy.highfive.view.HolidayDateView;
import com.infoshare.academy.highfive.view.HolidayView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public final class HolidaysFilter {

    private static final Logger stdout = LoggerFactory.getLogger("CONSOLE_OUT");
    private static final String BY_NAME = "byName";
    private static final String BY_DATE = "byDate";


    private HolidaysFilter() {
        throw new IllegalStateException("Utility filter class");
    }


    private static List<HolidayView> getHolidayViews() {
        HolidayMapper holidayMapper = new HolidayMapper();
        return HolidaysSingleton.getInstance().getAllHolidays().stream()
                .map(holidayMapper::mapEntityToView)
                .collect(Collectors.toList());
    }

    private static HolidayDateView getHolidayDateViewMinDate() {
        return getHolidayViews().stream()
                .map(HolidayView::getDate)
                .min(Comparator.comparing(HolidayDateView::getDateIso))
                .orElse(null);
    }

    private static HolidayDateView getHolidayDateViewMaxDate() {
        return getHolidayViews().stream()
                .map(HolidayView::getDate)
                .max(Comparator.comparing(HolidayDateView::getDateIso))
                .orElse(null);
    }

    private static void showMaxMinDateInfo() {
        stdout.info(ColorsSet.ANSI_YELLOW + "Database entry's in scope of "
                + getHolidayDateViewMinDate().toString() + " to "
                + getHolidayDateViewMaxDate().toString() + ColorsSet.ANSI_RESET
                + "\n");
    }

    private static boolean isInputDateInScope(String inputTxt) {
        return ParseStringToIsoDate.parseStringToDate(inputTxt).isAfter(getHolidayDateViewMaxDate().getDate())
                || ParseStringToIsoDate.parseStringToDate(inputTxt).isBefore(getHolidayDateViewMinDate().getDate());
    }

    public static void searchByName() {

        String inputTxt;
        Scanner scanner = new Scanner(System.in);
        do {
            TerminalCleaner.cleanTerminal();
            showMaxMinDateInfo();
            stdout.info("Type Holiday Name(min. 3 char.) or type [0] to exit: ");
            inputTxt = scanner.nextLine().strip();

            if (inputTxt.length() == 1 && inputTxt.equals("0")) {
                break;
            }

        } while (inputTxt.length() < 3);

        stdout.info("You typed: " + inputTxt + "\n");

        if (!inputTxt.equals("0")) {
            queryResults(inputTxt.toLowerCase(), BY_NAME);

            stdout.info("Type [1] to search again or something else to exit: ");

            inputTxt = scanner.nextLine();

            if (inputTxt.length() == 1 && inputTxt.equals("1")) {
                searchByName();
            }
        }
    }

    public static void searchByDate() {

        String datePattern = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
        boolean matchedToDatePattern;
        String inputTxt;
        Scanner scanner;


        do {
            TerminalCleaner.cleanTerminal();
            showMaxMinDateInfo();
            stdout.info("Type Date in format yyyy-mm-dd or type [0] to exit: ");
            scanner = new Scanner(System.in);
            inputTxt = scanner.nextLine().strip();
            matchedToDatePattern = inputTxt.matches(datePattern);
            if (matchedToDatePattern && isInputDateInScope(inputTxt)) {
                matchedToDatePattern = false;
                stdout.info("\nDate out of scope!\n");
                continue;
            }
            if (inputTxt.equals("0")) {
                matchedToDatePattern = false;
                break;
            }
        } while (!matchedToDatePattern);

        stdout.info("You typed: " + inputTxt + "\n");

        if (matchedToDatePattern) {

            queryResults(inputTxt, BY_DATE);

            stdout.info("Type [1] to search again or something else to exit: ");

            inputTxt = scanner.nextLine();

            if (inputTxt.length() == 1 && inputTxt.equals("1")) {
                searchByDate();
            }
        }

    }

    private static void queryResults(String filter, String filterType) {

        List<Holiday> resultHolidayList;

        if (filterType.equals(BY_DATE)) {
            resultHolidayList = HolidaysSingleton.getInstance().getHolidaysFilteredByDate(filter);
        } else if (filterType.equals(BY_NAME)) {
            resultHolidayList = HolidaysSingleton.getInstance().getHolidaysFilteredByName(filter);
        } else {
            stdout.info("Operation not supported!!");
            return;
        }

        List<HolidayView> viewList = new ArrayList<>();

        for (Holiday holiday : resultHolidayList) {
            HolidayView holidayView = new HolidayView(holiday.getName(), holiday.getDescription(), new HolidayDateView(holiday), holiday.getTypes());

            if (holidayView.getDate().getDayInWeek() == 6 && holidayView.getTypes().equals(HolidayType.NATIONAL_HOLIDAY)) {
                stdout.info("For this day you will be able to take extra holiday!\n");
            }

            viewList.add(holidayView);
        }
        if (!viewList.isEmpty()) {
            viewList.forEach(i -> stdout.info(i.toString()));
            stdout.info("______\n" + viewList.size() + " result(s) found for this query!\n------\n");
        } else {
            stdout.info("______\nNo result(s) found for this query!\n------\n");
        }

    }

}
