package ot.webtest.framework.kketshelpers;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import ot.webtest.framework.HelperBase;

import java.time.LocalDate;
import java.time.Month;

import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class CalendarHelper extends HelperBase {

    @Step("Устанавливаем дату '{date}' в раскрытом календаре.")
    public void setDate(LocalDate date) {
        SelenideElement goToMonthSelectionElement = findElementHavingInvisibleDuplicates(By.xpath("//div[@class='rw-calendar-header']/button[contains(@id, 'calendar_label')]"));
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
}
