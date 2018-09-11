package ot.webtest.framework.kketshelpers.nsi;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.helpers.RobotHelper;
import ot.webtest.framework.kketshelpers.LoaderHelper;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelCalculationOperation;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelConsumptionMetrics;

import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class FuelCalculationOperationHelper extends HelperBase {
    // работа на вкладке 'НСИ --> Показатели для расчета --> Операции для расчета топлива'

    /*****************************************************************************************************************
     * Добавление операции для расчёта топлива
     ****************************************************************************************************************/
    @Step("Клик на кнопке '+ Создать'")
    public void clickPlusCreate() {
        click(By.xpath("//button[@id='open-create-form']"));
    }

    @Step("Добавление операции для расчёта топлива '{fuelCalculationOperation}'")
    public void createOperationForFuelCalculation(FuelCalculationOperation fuelCalculationOperation) {
        clickPlusCreate();

        setValueOrLogNoDataToSet("Операция (название)", By.xpath("//div[label/span[text()='Операция']]/input"), fuelCalculationOperation.operationName);

        dropDownSelect(
                "Единица измерения",
                fuelCalculationOperation.fuelConsumptionMetrics == null ? null : new Special<String>(fuelCalculationOperation.fuelConsumptionMetrics.toString()),
                By.xpath("//div[@id='modal-fuel-operation']//div[label[text()='Единица измерения']]//span[contains(@class,'Select-arrow-zone')]/span"),
                By.xpath("//div[label[text()='Единица измерения']]//div[@class='Select-menu']//span"));

        setCheckBoxOrLogNoDataToSet("Без учета пробега", By.xpath("//div[label[text()='Без учета пробега']]/input"), fuelCalculationOperation.isWithoutMileageRecording);

        setCheckBoxOrLogNoDataToSet("Для спецоборудования", By.xpath("//div[label[text()='Для спецоборудования']]/input"), fuelCalculationOperation.isForSpecialEquipment);

        logScreenshot("Скриншот перед сохранением формы.");
        clickSave();

        LoaderHelper loader = new LoaderHelper();
        loader.waitLoaderDisappear();
    }

    @Step("Клик на кнопке 'Сохранить'")
    public void clickSave() {
        click(By.xpath("//div[@id='modal-fuel-operation']//button[text()='Сохранить']"));
    }

    /*****************************************************************************************************************
     * РАБОТА С ТАБЛИЦЕЙ ОПЕРАЦИЙ
     ****************************************************************************************************************/

    @Step("Фильтрация по имени операции '{operationName}'")
    public void filterByOperationName(String operationName) {
        By openCloseFilterButtonLocator = By.xpath("//button[@id='show-options-filter']");
        click("Раскрываем фильтры", openCloseFilterButtonLocator);
        logScreenshot();
        sleepMillis(200);
        int trialsCount = 0;
        do {
            logDelimeter();
            logPassed("Попытка #" + trialsCount);
            logScreenshot();
            click("Клик по полю 'Операция' для активации ввода (установки курсора)", By.xpath("//div[label[text()='Операция']]//div[contains(@class,'Select-placeholder')]"));
            logScreenshot();
            sleepMillis(200);
            logScreenshot();
            RobotHelper robotHelper = new RobotHelper();
            try {
                robotHelper.typeString(operationName);
                logScreenshot();
                robotHelper.pressEnter();
            } catch (Exception e) {
                logException(e);
            }
            logScreenshot();
            if (isDisplayedWithTimeout(By.xpath("//span[text()='" + operationName + "' and contains(@class, 'Select-value-label')]"), 500)) {
                break;
            }
            trialsCount++;
        } while (trialsCount < 5);
        if (trialsCount == 5)
            Assert.fail("За 5 попыток так и не было выбрано значение в поле 'Операция'");

        logDelimeter();
        click("Клик по кнопке 'Применить'", By.xpath("//button[@id='apply-filter']"));
        sleepMillis(200);
        logScreenshot("Скриншот по применению фильтра");
        click("Закрываем фильтры", openCloseFilterButtonLocator);
    }

    @Step("Получение операции для расчёта топлива из таблицы по полю операция (название) = '{operationName}'")
    public FuelCalculationOperation getOperationForFuelCalculationFromTableByName(String operationName) {
        ElementsCollection elementsTd = getElements("Поиск записи об операции с названием '" + operationName + "'", By.xpath("//tr[td[text()='" + operationName + "']]/td"));
        if (elementsTd == null || elementsTd.size() == 0) {
            logSkipped("Операция не найдена.");
            return null;
        }
        ElementsCollection elementsHeader = getElements("Получаем заголовки столбцов", By.xpath("//div[contains(@class,'griddle-body')]//table/thead/tr/th"));
        if (elementsHeader.size() != elementsTd.size()) {
            Assert.fail("Не совпадает кол-во столбцов для записи n=" + elementsTd.size() + " и для заголовка n=" + elementsHeader.size());
        }

        FuelCalculationOperation fuelCalculationOperation = new FuelCalculationOperation();
        for (int i = 0; i < elementsHeader.size(); i++) {
            String headerText = elementsHeader.get(i).getText();
            String tdText = elementsTd.get(i).getText();

            if (headerText.equals("Операция")) {
                fuelCalculationOperation.withOperationName(tdText);
                continue;
            }
            if (headerText.equals("Единица измерения")) {
                fuelCalculationOperation.withFuelConsumptionMetrics(FuelConsumptionMetrics.getByName(tdText));
                continue;
            }
            if (headerText.equals("Без учета пробега")) {
                boolean isSelected = elementsTd.get(i).findElement(By.xpath("./div/input")).isSelected();
                fuelCalculationOperation.withIsWithoutMileageRecording(isSelected);
                continue;
            }
            if (headerText.equals("Для спецоборудования")) {
                boolean isSelected = elementsTd.get(i).findElement(By.xpath("./div/input")).isSelected();
                fuelCalculationOperation.withIsForSpecialEquipment(isSelected);
                continue;
            }
        }
        return fuelCalculationOperation;
    }
}
