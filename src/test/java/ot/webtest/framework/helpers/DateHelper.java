package ot.webtest.framework.helpers;

import ot.webtest.dataobject.SpecialDateTime;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ot.webtest.framework.helpers.AllureHelper.logBroken;

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
        LocalDate localDate;
        try {
            // the only correct of date setting!!! otherwise LocalDate.now() could be Feb. but first you try to set day 31st of Feb, Ok you'll have no chance to set month to March
            localDate = LocalDate.now().withYear(year).withMonth(month).withDayOfMonth(day);
        } catch (DateTimeException exception) {
            logBroken("Проблема в одном из значений: входная строка '" + dateText + "', day '" + day + "', month '" + month + "', year '" + year + "'");
            throw exception;
        }
        return localDate;
    }

    public static SpecialDateTime getFromDDMMYYYY_HHMM(String dateTimeText) {
        SpecialDateTime dateTime;
        String date = RegExpHelper.getSubstring("\\d{2}\\.\\d{2}\\.\\d{4}", dateTimeText);
        if (date != null)
            dateTime = new SpecialDateTime(date);
        else
            throw new IllegalArgumentException("Дата не определена '" + dateTimeText + "'");

        String time = RegExpHelper.getSubstring("\\d{2}:\\d{2}", dateTimeText);
        if (time != null)
            dateTime.withTime(time);
        else
            throw new IllegalArgumentException("Время не определено '" + dateTimeText + "'");
        return dateTime;
    }

    public static SpecialDateTime getSpecialDateTimeNow() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        SpecialDateTime now =
                new SpecialDateTime(LocalDate.now())
                        .withHour(calendar.get(Calendar.HOUR_OF_DAY))
                        .withMinute(calendar.get(Calendar.MINUTE));
        return now;
    }
}
