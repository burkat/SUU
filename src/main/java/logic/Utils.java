package logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

class Utils {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final Scanner scanner = new Scanner(System.in);

    static Date enterDate() {
        Date date = null;
        boolean parsed = false;
        while (!parsed) {
            try {
                date = DATE_FORMAT.parse(scanner.nextLine());
                parsed = true;
            } catch (ParseException e) {
                System.out.println("Wrong format, enter date again");
            }
        }
        return date;
    }

    static Date plusOneDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
}
