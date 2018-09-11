package ot.webtest.framework.kketshelpers.waybill;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.helpers.DateHelper;
import ot.webtest.framework.kketshelpers.CalendarHelper;
import ot.webtest.framework.kketshelpers.LoaderHelper;
import ot.webtest.framework.kketshelpers.dashboard.TaskCreationHelper;
import ot.webtest.framework.kketshelpers.dataobjects.DecentralizedTask;
import ot.webtest.framework.kketshelpers.dataobjects.Waybill;

import static ot.webtest.framework.helpers.AllureHelper.logScreenshot;
import static ot.webtest.framework.helpers.AllureHelper.logSkipped;

public class WaybillCreationHelper extends HelperBase {

    @Step("Устанавливаем значения путевого листа '{waybill}' и сохраняем его.")
    public Waybill setAndSaveWaybill(Waybill waybill) {
        CalendarHelper calendar = new CalendarHelper();
        LoaderHelper loader = new LoaderHelper();

        // {dateLeavePlanned - dateReturnPlanned} must include task period (or task period must be within Waybill dates)!!!
        String dateStartExternalDivXPath = "//div[label[text()='Выезд план.']]";
        calendar.setDateOrLogNoDataToSet(waybill.dateLeavePlanned, "Выезд план.", dateStartExternalDivXPath);
        calendar.setDateOrLogNoDataToSet(waybill.dateReturnPlanned, "Возвращение план.", "//div[label[text()='Возвращение план.']]");

        dropDownSelect(
                "Транспортное средство (поиск по рег. номер  ТС)",
                waybill.vehicle,
                By.xpath("//div[label[text()='Транспортное средство (поиск по рег. номер  ТС)']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Транспортное средство (поиск по рег. номер  ТС)']]//div[contains(@class,'Select-menu')]//span"));
        loader.waitLoaderDisappear();

        Special<String> driver = dropDownSelect(
                "Водитель (возможен поиск по табельному номеру)",
                waybill.driver,
                By.xpath("//div[label[text()='Водитель (возможен поиск по табельному номеру)']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Водитель (возможен поиск по табельному номеру)']]//div[contains(@class,'Select-menu')]//span"));
        waybill.withDriver(driver);

        scrollIntoView(By.xpath(dateStartExternalDivXPath));
        logScreenshot();
        DecentralizedTask decentralizedTask =
                createDecentralizedTaskInWaybill(waybill.decentralizedTaskToCreate);
        waybill.withDecentralizedTaskToCreate(decentralizedTask);

        logScreenshot();
        click("Клик по кнопке 'Сохранить'", By.xpath("//div[div//h4[text()='Создать новый путевой лист']]//button[text()='Сохранить']"));
        waybill.withCreateDateTime(DateHelper.getSpecialDateTimeNow());

        LoaderHelper loaderHelper = new LoaderHelper();
        loaderHelper.waitLoaderDisappear();

        checkAndCloseDataSuccessfullySavedMessageOptional();

        return waybill;
    }

    @Step("Проверка появления сообщения 'Данные успешно сохранены' (и его закрытие)")
    private void checkAndCloseDataSuccessfullySavedMessageOptional() {
        if (isDisplayedWithTimeout("Отображается ли сообщение 'Данные успешно сохранены'", By.xpath("//div[text()='Данные успешно сохранены' and @class='notification-message']"), 5000)) {
            clickIgnoreException(By.xpath("//div[div[text()='Данные успешно сохранены' and @class='notification-message']]/span[text()='×']"));
        }
    }

    @Step("Создание децентрализованного задания '{decentralizedTaskToCreate}' (в платёжном листе)")
    private DecentralizedTask createDecentralizedTaskInWaybill(DecentralizedTask decentralizedTaskToCreate) {
        if (decentralizedTaskToCreate != null) {
            click("Клик по кнопке 'Создать задание'", By.xpath("//button[text()='Создать задание']"));
            TaskCreationHelper taskCreationHelper = new TaskCreationHelper();
            DecentralizedTask decentralizedTask =
                    taskCreationHelper.setAndSaveDecentralizedTask(decentralizedTaskToCreate);
            taskCreationHelper.checkAndCloseMessageTaskCreatedSuccessfully();
            return decentralizedTask;
        } else {
            logSkipped("Нет данных для создания Задания (децентрализованного).");
            return null;
        }
    }
}
