package ot.webtest.framework.kketshelpers;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.HelperBase;

import java.time.LocalDate;
import java.time.Month;

import static ot.webtest.framework.helpers.AllureHelper.logSkipped;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class CalendarHelper extends HelperBase {

    @Step("Устанавливаем дату '{date}' в раскрытом календаре.")
    public void setDate(LocalDate date) {
        SelenideElement goToMonthSelectionElement = findElementHavingInvisibleDuplicates(By.xpath("//div[@class='rw-calendar-header']/button[contains(@id, 'calendar_label')]"), 5000);
        click("Клик по выбранным месяц-год для перехода к выбору месяца", goToMonthSelectionElement);
        sleepMillis(300);
        SelenideElement goToYearSelectionElement = findElementHavingInvisibleDuplicates(By.xpath("//div[@class='rw-calendar-header']/button[contains(@id, 'calendar_label')]"));
        click("Клик по выбранному году для перехода к выбору года", goToYearSelectionElement);
        sleepMillis(300);
        int year = date.getYear();
        By yearToSelectLocator = By.xpath("//td[text()='" + year + "']");
        int shiftsCount = 0;
        do {
            if (isDisplayedWithTimeout(yearToSelectLocator, 0)) {
                SelenideElement yearToSelectElement = findElementHavingInvisibleDuplicatesNoErrorOnNoElements("", yearToSelectLocator);
                click("Выбираем год '" + year + "' (клик по нему в календаре)", yearToSelectElement);
                sleepMillis(300);
                break;
            } else {
                LocalDate curDate = LocalDate.now();
                if (curDate.getYear() < year) {
                    SelenideElement scrollYearsForward = findElementHavingInvisibleDuplicates(By.xpath("//button[@title='Navigate forward']"));
                    click("Крокручиваем годы вперёд (клик по >)", scrollYearsForward);
                } else {
                    SelenideElement scrollYearsBack = findElementHavingInvisibleDuplicates(By.xpath("//button[@title='Navigate back']"));
                    click("Крокручиваем годы назад (клик по <)", scrollYearsBack);
                }
                sleepMillis(300);
            }
            shiftsCount++;
        } while (shiftsCount < 10);
        String ruKketsMonthName = getRuKketsMonthName(date.getMonth());
        SelenideElement monthButton = findElementHavingInvisibleDuplicates(By.xpath("//tbody[@class='rw-calendar-body']/tr/td[text()='" + ruKketsMonthName + "']"));
        click("Клик по кнопке выбра месяца '" + ruKketsMonthName + "'", monthButton);
        sleepMillis(300);
        int dayOfMonth = date.getDayOfMonth();
        SelenideElement dayButton = findElementHavingInvisibleDuplicates(By.xpath("//tbody[@class='rw-calendar-body']/tr/td[text()='" + String.format("%02d", dayOfMonth) + "' and not(contains(@class, 'rw-cell-off-range'))]"));
        click("Клик по дню " + String.format("%02d", dayOfMonth) + "' в календаре", dayButton);
    }

    private String getRuKketsMonthName(Month month) {
        String monthName = "";
        switch (month) {
            case JANUARY:
                monthName = "янв.";
                break;
            case FEBRUARY:
                monthName = "февр.";
                break;
            case MARCH:
                monthName = "март";
                break;
            case APRIL:
                monthName = "апр.";
                break;
            case MAY:
                monthName = "май";
                break;
            case JUNE:
                monthName = "июнь";
                break;
            case JULY:
                monthName = "июль";
                break;
            case AUGUST:
                monthName = "авг.";
                break;
            case SEPTEMBER:
                monthName = "сент.";
                break;
            case OCTOBER:
                monthName = "окт.";
                break;
            case NOVEMBER:
                monthName = "нояб.";
                break;
            case DECEMBER:
                monthName = "дек.";
                break;
        }
        return monthName;
    }

    @Step("В поле '{dateFieldName}' устанавливаем дату '{date}'")
    public void setDateOrLogNoDataToSet(LocalDate date, String dateFieldName, By calendarOpenerLocator) {
        CalendarHelper calendarHelper = new CalendarHelper();
        if (date != null) {
            click("Нажимаем на 'Кнопка раскрытия календаря <" + dateFieldName + ">'", calendarOpenerLocator);
            sleepMillis(300);
            calendarHelper.setDate(date);
        } else {
            logSkipped("Нет данных для ввода в поле '" + dateFieldName + "'");
        }
    }

    @Step("В поле '{dateFieldName}' устанавливаем дату-время '{dateTime}'")
    public void setDateOrLogNoDataToSet (SpecialDateTime dateTime, String dateFieldName, String xpathToTagWithDateTimeButtons) {
        CalendarHelper calendarHelper1 = new CalendarHelper();
        if (dateTime != null && dateTime.date != null) {
            click("Раскрываем календарь установки '" + dateFieldName + "'", By.xpath(xpathToTagWithDateTimeButtons + "//button[@aria-label='Select date']"));
            sleepMillis(300);
            calendarHelper1.setDate(dateTime.date);
            dropDownSelect(
                    dateFieldName,
                    new Special<>(dateTime.getTime24()),
                    By.xpath(xpathToTagWithDateTimeButtons + "//button[@aria-label='Select time']"),
                    By.xpath(xpathToTagWithDateTimeButtons+ "//ul/li"));
        } else {
            logSkipped("Нет данных для ввода в поле '" + dateFieldName +  "'");
        }
    }
}
