package ot.webtest.dataobject;

import ot.webtest.framework.helpers.DateHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialDateTime {
    public LocalDate date;
    public Integer hour;
    public Integer minute;

    public SpecialDateTime(LocalDate date) {
        this.date = date;
    }

    public SpecialDateTime(String date) {
        this.date = DateHelper.getFromDDMMYYYY(date);
    }

    public SpecialDateTime withHour (Integer hour) {
        this.hour = hour;
        return this;
    }

    public SpecialDateTime withMinute (Integer minute) {
        this.minute = minute;
        return this;
    }

    public SpecialDateTime withTime (String time) {
        Pattern patternTime = Pattern.compile("^\\d{2}:\\d{2}$");
        Matcher matcher = patternTime.matcher(time);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Time = '" + time + "' does not match pattern = '" + patternTime + "'");
        }
        this.hour = Integer.valueOf(time.substring(0, 2));
        this.minute = Integer.valueOf(time.substring(3, 5));
        return this;
    }

    public String getTime24() {
        String hourStr, minuteStr;
        if (hour == null)
            hourStr = "--";
        else
            hourStr = String.format("%02d", hour);
        if (minute == null)
            minuteStr = "--";
        else
            minuteStr = String.format("%02d", minute);
        return hourStr + ":" + minuteStr;
    }

    public String getDate() {
        if (date == null)
            return "--.--.----";
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.LL.yyyy");
            return date.format(formatter);
        }
    }

    public long getMillisecondsSinceEpoch() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), hour, minute);
        return calendar.getTimeInMillis();
    }

    @Override
    public String toString() {
        return getDate() + " " + getTime24();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof SpecialDateTime) {
            SpecialDateTime specialDateTime = (SpecialDateTime)obj;
            if (this.toString().equals(specialDateTime.toString()))
                return true;
        }
        return false;
    }
}
