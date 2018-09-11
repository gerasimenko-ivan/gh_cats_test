package ot.webtest.framework.kketshelpers.waybill;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.helpers.DateHelper;
import ot.webtest.framework.helpers.RegExpHelper;
import ot.webtest.framework.helpers.RobotHelper;
import ot.webtest.framework.kketshelpers.CalendarHelper;
import ot.webtest.framework.kketshelpers.LoaderHelper;
import ot.webtest.framework.kketshelpers.dataobjects.Waybill;

import java.time.LocalDate;

import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.StringHelper.isNotEmptyString;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class WaybillJournalHelper extends HelperBase {

    /*****************************************************************************************************************
     * FILTERING
     ****************************************************************************************************************/

    By openCloseFilterButtonLocator = By.xpath("//button[@id='show-options-filter']");

    @Step("Фильтрация платёжных листов по рег.номеру ТС '{regNumber}'")
    public void filterByVehicleRegNumber(String regNumber) {
        openFilter();
        resetIfPossible();
        setRegNumberValue(regNumber);
        applyAndCloseFilter();
    }

    @Step("Фильтрация платёжных листов по рег.номеру ТС '{regNumber}' и дате выезда план. '{dateLeave}'")
    public void filterByVehicleRegNumberAndPlannedDateLeave(String regNumber, LocalDate dateLeave) {
        openFilter();
        resetIfPossible();

        setRegNumberValue(regNumber);

        CalendarHelper calendar = new CalendarHelper();
        click("Раскрываем календарь поля 'Выезд план'", By.xpath("//div[label[text()='Выезд план']]//button[@title='Select date']"));
        calendar.setDate(dateLeave);

        applyAndCloseFilter();
    }

    @Step("Нажимаем кнопку 'Сброс', если она активна")
    private void resetIfPossible() {
        By resetButtonLocator = By.xpath("//button[text()='Сброс']");
        if (isEnabled(resetButtonLocator)) {
            click(resetButtonLocator);
        }
    }


    @Step("Устанавливаем значение поля 'Рег. номер ТС' в '{regNumber}'")
    private void setRegNumberValue(String regNumber) {
        int trialsCount = 0;
        do {
            logDelimeter();
            logScreenshot("Попытка #" + trialsCount);
            click("Клик по полю 'Рег. номер ТС' для активации ввода (установки курсора)", By.xpath("//div[label[text()='Рег. номер ТС']]//div[contains(@class,'Select-placeholder')]"));
            logScreenshot();
            scrollIntoView(By.xpath("//label[text()='Рег. номер ТС']"));
            sleepMillis(500);
            logScreenshot();
            RobotHelper robotHelper = new RobotHelper();
            try {
                robotHelper.typeString(regNumber);
                logPassed("Ввод '" + regNumber + "' (Robot FW)");
            } catch (Exception e) {
                logException(e);
            }
            sleepMillis(2000);
            logScreenshot();
            robotHelper.pressEnter();
            logScreenshot();
            if (isDisplayedWithTimeout(By.xpath("//span[text()='" + regNumber + "' and contains(@class, 'Select-value-label')]"), 500)) {
                break;
            }
            robotHelper.pressEsc();
            robotHelper.pressEsc();
            robotHelper.pressEsc();
            trialsCount++;
        } while (trialsCount < 5);
        if (trialsCount == 5)
            Assert.fail("За 5 попыток так и не было выбрано значение в поле 'Рег. номер ТС'");
    }

    private void applyAndCloseFilter() {
        LoaderHelper loaderHelper = new LoaderHelper();

        click("Клик по кнопке 'Применить'", By.xpath("//button[@id='apply-filter']"));
        loaderHelper.waitLoaderDisappear();

        logScreenshot("Скриншот по применению фильтра");
        click("Закрываем фильтры", openCloseFilterButtonLocator);
    }

    private void openFilter() {
        click("Раскрываем фильтры", openCloseFilterButtonLocator);
        sleepMillis(500);
    }


    /*****************************************************************************************************************
     * WORK WITH TABLE
     ****************************************************************************************************************/

    @Step("Получение платёжного листа из таблицы по полю 'Выезд план' = '{dateLeavePlanned}'")
    public Waybill getItemByDateLeavePlanned(SpecialDateTime dateLeavePlanned) {
        ElementsCollection elementsTd = getElements("Поиск записи по полю 'Выезд план' = '" + dateLeavePlanned + "'", By.xpath("//tr[td/div[text()='" + dateLeavePlanned + "']]/td"));
        if (elementsTd == null || elementsTd.size() == 0) {
            logSkipped("ПЛ не найден.");
            return null;
        }
        ElementsCollection elementsHeader = getElements("Получаем заголовки столбцов", By.xpath("//div[contains(@class,'griddle-body')]//table/thead/tr/th"));
        if (elementsHeader.size() != elementsTd.size()) {
            Assert.fail("Не совпадает кол-во столбцов для записи n=" + elementsTd.size() + " и для заголовка n=" + elementsHeader.size());
        }

        Waybill waybill = new Waybill();
        for (int i = 0; i < elementsHeader.size(); i++) {
            String headerText = elementsHeader.get(i).getText();
            String tdText = elementsTd.get(i).getText();

            if (headerText.equals("Дата создания")) {
                if (isNotEmptyString(tdText))
                    waybill.withCreateDateTime(DateHelper.getFromDDMMYYYY_HHMM(tdText));
                continue;
            }
            if (headerText.equals("Водитель")) {
                waybill.withDriver(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Рег. номер ТС")) {
                if (isNotEmptyString(tdText))
                    waybill.withVehicleRegNumber(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Выезд план")) {
                if (isNotEmptyString(tdText))
                    waybill.withDateLeavePlanned(DateHelper.getFromDDMMYYYY_HHMM(tdText));
                continue;
            }
            if (headerText.equals("Возвращение факт")) {
                if (isNotEmptyString(tdText))
                    waybill.withDateReturnPlanned(DateHelper.getFromDDMMYYYY_HHMM(tdText));
                continue;
            }
            if (headerText.equals("Создан")) {
                if (isNotEmptyString(tdText))
                    waybill.withCreatedBy(tdText);
                continue;
            }
        }
        logScreenshot("ПЛ: " + waybill.toString());
        return waybill;
    }

    @Step("Нажимаем на кнопку '+ Создать'")
    public void pressCreateWaybillButton() {
        click(By.xpath("//button[@id='open-create-form']"));
    }
}
