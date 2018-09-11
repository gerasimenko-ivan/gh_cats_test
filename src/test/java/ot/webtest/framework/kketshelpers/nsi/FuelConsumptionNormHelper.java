package ot.webtest.framework.kketshelpers.nsi;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.helpers.AssertHelper;
import ot.webtest.framework.helpers.DateHelper;
import ot.webtest.framework.helpers.RobotHelper;
import ot.webtest.framework.helpers.StringHelper;
import ot.webtest.framework.kketshelpers.CalendarHelper;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelCalculationOperation;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelConsumptionNorm;

import java.time.LocalDate;

import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class FuelConsumptionNormHelper extends HelperBase {

    /*****************************************************************************************************************
     * Добавление нормы расхода топлива
     ****************************************************************************************************************/
    @Step("Клик на кнопке '+ Создать'")
    public void clickPlusCreate() {
        click(By.xpath("//button[@id='open-create-form']"));
    }

    @Step("Добавление нормы расхода топлива '{fuelConsumptionNorm}'")
    public FuelConsumptionNorm createFuelConsumptionNorm(FuelConsumptionNorm fuelConsumptionNorm) {
        clickPlusCreate();

        setOrderDate(fuelConsumptionNorm.orderDate);

        Special<String> operationName = dropDownSelect(
                "Операция (название)",
                fuelConsumptionNorm.operationName,
                By.xpath("//div[label[text()='Операция']]//span[contains(@class, 'Select-arrow-zone')]"),
                "//div[label[text()='Операция']]//div[contains(@class,'Select-menu')]/div[contains(@class,'Select-option')][INDEX]/div/span");
        fuelConsumptionNorm.withOperationName(operationName);

        fuelConsumptionNorm.withFuelConsumptionMetrics(getFuelConsumptionMetrics());

        setValue("Норма для летнего периода", By.xpath("//div[label/span[text()='Норма для летнего периода']]/input"), StringHelper.get(fuelConsumptionNorm.normForSummer, 3, "."));
        setValue("Норма для зимнего периода", By.xpath("//div[label/span[text()='Норма для зимнего периода']]/input"), StringHelper.get(fuelConsumptionNorm.normForWinter, 3, "."));

        Special<String> vehicleModel = dropDownSelect(
                "Модель ТС",
                fuelConsumptionNorm.vehicleModel,
                By.xpath("//div[label[text()='Модель ТС']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Модель ТС']]//div[contains(@class,'Select-menu')]//span"));
        fuelConsumptionNorm.withVehicleModel(vehicleModel);

        Special<String> vehicleChassisBrand = dropDownSelect(
                "Марка шасси",
                fuelConsumptionNorm.vehicleChassisBrand,
                By.xpath("//div[label[text()='Марка шасси']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Марка шасси']]//div[contains(@class,'Select-menu')]//span"));
        fuelConsumptionNorm.withVehicleChassisBrand(vehicleChassisBrand);

        Special<String> subdevision = dropDownSelect(
                "Подразделение",
                fuelConsumptionNorm.subdivision,
                By.xpath("//div[label[text()='Подразделение']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Подразделение']]//div[contains(@class,'Select-menu')]//span"));
        fuelConsumptionNorm.withSubdivision(subdevision);

        sleepMillis(300, "Ожидание, т.к. кнопка Сохранить сразу часто не реагирует на клик (хотя она активна)");

        logScreenshot("Скриншот перед сохранением формы.");
        clickSave();
        return fuelConsumptionNorm;
    }

    @Step("Получаем значение поля 'Единица измерения'")
    private Special<String> getFuelConsumptionMetrics() {
        By fuelConsumptionMetricsInputLocator = By.xpath("//div[label/span[text()='Единица измерения']]/input");
        String fuelConsumptionMetricsText = getAttribute(fuelConsumptionMetricsInputLocator, "value", "Единица измерения");
        boolean isEnabled = isEnabled(fuelConsumptionMetricsInputLocator);
        logPassed("{fuelConsumptionMetricsText: " + fuelConsumptionMetricsText + "; isEnabled: " + isEnabled + "}");
        AssertHelper.checkFalse(
                isEnabled,
                "Поле не активно и заполняется автоматически (в том числе и значением '-')",
                "Поле должно быть неактивно!!! (заполняется автоматически)");
        return new Special<>(fuelConsumptionMetricsText).withIsEditable(isEnabled);
    }

    @Step("Устанавливаем поле 'Дата приказа' = '{orderDate}'")
    private void setOrderDate(LocalDate orderDate) {
        CalendarHelper calendarHelper = new CalendarHelper();
        if (orderDate != null) {
            click("Раскрываем календарь установки 'Дата приказа'", By.xpath("//div[div/h4]//div[label[text()='Дата приказа']]//button[@aria-label='Select date']/span"));
            sleepMillis(300);
            calendarHelper.setDate(orderDate);
        } else {
            logSkipped("Нет данных для ввода в поле 'Дата приказа'");
        }
    }

    @Step("Клик на кнопке 'Сохранить'")
    public void clickSave() {
        click(By.xpath("//div[@id='modal-fuel-rate']//button[text()='Сохранить']"));
    }

    /*****************************************************************************************************************
     * РАБОТА С ФИЛЬТРОМ ОПЕРАЦИЙ
     ****************************************************************************************************************/

    @Step("Фильтрация операций по некоторым полям операции '{fuelConsumptionNorm}'")
    public void filterByItem(FuelConsumptionNorm fuelConsumptionNorm) {
        By openCloseFilterButtonLocator = By.xpath("//button[@id='show-options-filter']");
        click("Раскрываем фильтры", openCloseFilterButtonLocator);
        sleepMillis(200);

        setDateOnFilter(fuelConsumptionNorm.orderDate);

        setOperationNameOnFilter(fuelConsumptionNorm.operationName);

        setValueOrLogNoDataToSet(
                "Норма для летнего периода",
                By.xpath("//div[label[text()='Норма для летнего периода']]//input[@type='number']"),
                fuelConsumptionNorm.normForSummer == null ? null : StringHelper.get(fuelConsumptionNorm.normForSummer, 3, "."));

        logScreenshot("Форма фильтра перед применением.");
        click("Клик по кнопке 'Применить'", By.xpath("//button[@id='apply-filter']"));
        sleepMillis(200);
        logScreenshot("Скриншот по применению фильтра");
        click("Закрываем фильтры", openCloseFilterButtonLocator);
    }

    @Step("Устанавливаем на фильтре 'Операция (название)' = '{orderDate}'")
    private void setOperationNameOnFilter(Special<String> operationName) {
        if (operationName == null || operationName.getType() != Special.Type.SPECIFIC || operationName.getValue().equals("")) {
            logSkipped("Нет данных для ввода в поле 'Операция (название)' в фильтре.");
            return;
        }
        int trialsCount = 0;
        do {
            logDelimeter();
            logPassed(" Попытка#" + trialsCount);

            click("Клик по полю 'Операция' для активации ввода (установки курсора)", By.xpath("//div[label[text()='Операция']]//div[contains(@class,'Select-placeholder')]"));
            sleepMillis(200);
            RobotHelper robotHelper = new RobotHelper();
            try {
                robotHelper.typeString(operationName.toString());
            } catch (Exception e) {
                logException(e);
            }
            sleepMillis(1000);
            logScreenshot();
            robotHelper.pressEnter();
            logScreenshot();
            if (isDisplayedWithTimeout(By.xpath("//span[text()='" + operationName.toString() + "' and contains(@class, 'Select-value-label')]"), 500)) {
                break;
            }
            robotHelper.pressEsc();
            robotHelper.pressEsc();
            robotHelper.pressEsc();
            trialsCount++;
        } while (trialsCount < 5);
        if (trialsCount == 5)
            Assert.fail("За 5 попыток так и не было выбрано значение в поле 'Операция'");
    }

    @Step("Устанавливаем на фильтре 'Дату приказа' = '{orderDate}'")
    private void setDateOnFilter(LocalDate orderDate) {
        if (orderDate == null) {
            logSkipped("Нет данных для ввода в поле 'Дата приказа' в фильтре.");
            return;
        }
        CalendarHelper calendarHelper = new CalendarHelper();
        if (orderDate != null) {
            click("Раскрываем календарь установки 'Дата приказа'", By.xpath("//div[label[text()='Дата приказа']][contains(@class,'filter-row')]//button[@aria-label='Select date']/span"));
            sleepMillis(300);
            calendarHelper.setDate(orderDate);
        } else {
            logSkipped("Нет данных для ввода в поле 'Дата приказа'");
        }
    }

    /*****************************************************************************************************************
     * РАБОТА С ТАБЛИЦЕЙ ОПЕРАЦИЙ
     ****************************************************************************************************************/

    @Step("Получаем запись о норме расхода топлива из таблицы '{fuelConsumptionNorm}'")
    public FuelConsumptionNorm getFromTableByItem(FuelConsumptionNorm fuelConsumptionNorm) {
        if (fuelConsumptionNorm.operationName == null || fuelConsumptionNorm.operationName.getType() != Special.Type.SPECIFIC || fuelConsumptionNorm.operationName.getValue().equals("")) {
            Assert.fail("Нет данных для поиска по названию операции, поиск невозможен.");
        }
        if (fuelConsumptionNorm.orderDate == null) {
            Assert.fail("Нет данных для поиска по дате приказа, поиск невозможен.");
        }
        if (fuelConsumptionNorm.normForSummer == null && fuelConsumptionNorm.normForWinter == null) {
            Assert.fail("Нет данных для поиска по норме потребления (для летнего/зимнего времени - должно быть хотя бы одно значение), поиск невозможен.");
        }

        String operationName = fuelConsumptionNorm.operationName.toString().replace(" [спецоборудование]", "");
        String orderDate = StringHelper.getDevByDotsDDMMYYYY(fuelConsumptionNorm.orderDate);
        String normForSummerXPath = fuelConsumptionNorm.normForSummer != null ? "[td[text()='" + StringHelper.get(fuelConsumptionNorm.normForSummer, 3, ".") + "']]" : "";
        String normForWinterXPath = fuelConsumptionNorm.normForWinter != null ? "[td[text()='" + StringHelper.get(fuelConsumptionNorm.normForWinter, 3, ".") + "']]" : "";

        ElementsCollection elementsTd =
                getElements("Поиск записи о норме расхода топлива с датой приказа '" + orderDate + "', операцией '" + operationName + "' и нормой для летнего периода '" + normForSummerXPath + "'",
                By.xpath("//tr[td[text()='" + operationName + "']][td/div[text()='" + orderDate + "']]" + normForSummerXPath + normForWinterXPath + "/td"));
        if (elementsTd == null || elementsTd.size() == 0) {
            logSkipped("Норма расхода топлива не найдена.");
            logScreenshot();
            return null;
        }
        ElementsCollection elementsHeader = getElements("Получаем заголовки столбцов", By.xpath("//div[contains(@class,'griddle-body')]//table/thead/tr/th"));
        if (elementsHeader.size() != elementsTd.size()) {
            Assert.fail("Не совпадает кол-во столбцов для записи n=" + elementsTd.size() + " и для заголовка n=" + elementsHeader.size());
        }

        FuelConsumptionNorm fuelConsumptionNormFromTable = new FuelConsumptionNorm();
        for (int i = 0; i < elementsHeader.size(); i++) {
            String headerText = elementsHeader.get(i).getText();
            String tdText = elementsTd.get(i).getText();

            if (headerText.equals("Дата приказа")) {
                fuelConsumptionNormFromTable.withOrderDate(DateHelper.getFromDDMMYYYY(tdText));
                continue;
            }
            if (headerText.equals("Операция")) {
                fuelConsumptionNormFromTable.withOperationName(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Единица измерения")) {
                fuelConsumptionNormFromTable.withFuelConsumptionMetrics(new Special<>(tdText));
                continue;
            }

            if (headerText.equals("Норма для летнего периода")) {
                if (tdText == null || tdText.equals("")) {
                    logSkipped("Нет данных в столбце 'Норма для летнего периода'");
                    continue;
                }
                fuelConsumptionNormFromTable.withNormForSummer(Double.parseDouble(tdText));
                continue;
            }
            if (headerText.equals("Норма для зимнего периода")) {
                if (tdText == null || tdText.equals("")) {
                    logSkipped("Нет данных в столбце 'Норма для зимнего периода'");
                    continue;
                }
                fuelConsumptionNormFromTable.withNormForWinter(Double.parseDouble(tdText));
                continue;
            }

            if (headerText.equals("Модель ТС")) {
                fuelConsumptionNormFromTable.withVehicleModel(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Марка шасси ТС")) {
                fuelConsumptionNormFromTable.withVehicleChassisBrand(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Подразделение")) {
                fuelConsumptionNormFromTable.withSubdivision(new Special<>(tdText));
                continue;
            }
        }
        logScreenshot("Скриншот перед возвратом данных из таблицы");
        return fuelConsumptionNormFromTable;
    }
}
