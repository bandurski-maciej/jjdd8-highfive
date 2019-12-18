package com.infoshare.academy.highfive.consolmenu;

import com.infoshare.academy.highfive.holiday.HolidaysFilter;
import com.infoshare.academy.highfive.tool.ColorsSet;
import com.infoshare.academy.highfive.vacation.VacationPlanner;
import com.infoshare.academy.highfive.vacation.VacationRemoval;
import com.infoshare.academy.highfive.vacation.VacationSingleton;
import com.infoshare.academy.highfive.tool.TerminalCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

class HolidaysPlanningMenu extends MainMenu {
    private static final Logger stdout = LoggerFactory.getLogger("CONSOLE_OUT");

    public static void runSubmenu() throws Exception {

        HolidaysPlanningMenu holidaysPlanningMenu = new HolidaysPlanningMenu();
        holidaysPlanningMenu.menuOptionsDisplay();
        holidaysPlanningMenu.getUserChoice();
    }

    @Override
    void menuOptionsDisplay() {

        TerminalCleaner.cleanTerminal();

        stdout.info(ColorsSet.ANSI_YELLOW + ">>>>> " + mainMenuTitle + " / " + holidaysPlanningMenuTitle +" / \n" + ColorsSet.ANSI_RESET);

        menuOptions.add(holidaysPlanningMenuTitle);
        menuOptions.add("Search holiday by DATE");
        menuOptions.add("Search holiday by NAME");
        menuOptions.add("Add vacation");
        menuOptions.add("Cancel vacation");
        menuOptions.add("Previous menu");

        stdout.info("\n\n" + menuOptions.get(0) + "\n\n");

        for (int i = 1; i < menuOptions.size(); i++) {

            stdout.info(i + ": " + menuOptions.get(i) + "\n");
        }

    }

    @Override
    int getUserChoice() throws Exception {

        boolean matchedToPattern;
        stdout.info("\n" + "Choose option from 1 to " + (menuOptions.size() - 1) + "\n");
        Scanner scanner = new Scanner(System.in);
        String numberPattern = "[0-9]";
        userChoiceString = scanner.nextLine();
        matchedToPattern = userChoiceString.matches(numberPattern);
        if (!matchedToPattern) {
            stdout.info("Wrong input - try again\n");
            getUserChoice();
        }
        userChoice = Integer.parseInt(userChoiceString);

        if (userChoice > menuOptions.size() - 1 || userChoice == 0) {
            stdout.info("Wrong input - try again\n");
            getUserChoice();
        } else {
            switch (userChoice) {
                case 1:
                    HolidaysFilter.searchByDate();
                    break;
                case 2:
                    HolidaysFilter.searchByName();
                    break;
                case 3:
                    VacationPlanner vacationPlanner = new VacationPlanner();
                    vacationPlanner.planVacation();
                    break;
                case 4:
                    VacationRemoval vacationRemoval = new VacationRemoval();
//                    vacationRemoval.cancelVacation();

                    break;
                default:
                    MainMenu.runMenu();
            }
        }
        return userChoice;
    }
}