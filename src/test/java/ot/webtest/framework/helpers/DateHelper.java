package ot.webtest.framework.helpers;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateHelper {

    public static LocalDate getFromDDMMYYYY(String dateText) {
        Pattern patternDate = Pattern.compile("^\\d{2}\\.\\d{2}\\.\\d{4}$");
        Matcher matcher = patternDate.matcher(dateText);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Date = '" + dateText + "' does not match pattern = '" + patternDate + "'");
        }
        int day = Integer.valueOf(dateText.substring(0, 2));
        int month = Integer.valueOf(dateText.substring(3, 5));
        int year = Integer.valueOf(dateText.substring(6, 10));
        LocalDate localDate = LocalDate.now().withDayOfMonth(day).withMonth(month).withYear(year);
        return localDate;
    }
}
