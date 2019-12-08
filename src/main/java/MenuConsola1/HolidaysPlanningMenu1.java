package MenuConsola1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HolidaysPlanningMenu1 extends MainMenu1 {
    private static final Logger stdout = LoggerFactory.getLogger("CONSOLE_OUT");

    public static void main() throws Exception {

        HolidaysPlanningMenu1 holidaysPlanningMenu = new HolidaysPlanningMenu1();
        holidaysPlanningMenu.menuOptionsDisplay();
        holidaysPlanningMenu.getUserChoice();
    }

    @Override
    public void menuOptionsDisplay() {

        menuOptions.add("HOLIDAY PLANNING");
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
    public int getUserChoice() throws Exception {

        stdout.info("\n" + "Choose option from 0 to " + (menuOptions.size()-1) + "\n");

        try {
            Scanner scanner = new Scanner(System.in);
            scanner.useRadix(menuOptions.size());
            userChoice = scanner.nextInt();

            switch (userChoice) {
                case 1:
                    stdout.info("Wyszukiwarka Świąt - PODPIĄĆ !!!!!" + "\n" + "\n");
                    break;
                case 2:
                    stdout.info("Dodaj urlop - under construction" + "\n" + "\n");
                    break;
                case 3:
                    stdout.info("Add vacation- UNDER CONSTRUCTION\n\n");
                    break;
                case 4:
                    stdout.info("Cancel vacation - UNDER CONSTRUCTION\n\n");
                    break;
                default:
                    break;
            }

        } catch (InputMismatchException e) {
            stdout.info("Wrong input - try again\n");
            getUserChoice();
        }
        return userChoice;
    }

}