package ot.webtest.framework.kketshelpers.dashboard;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.kketshelpers.CalendarHelper;
import ot.webtest.framework.kketshelpers.LoaderHelper;
import ot.webtest.framework.kketshelpers.dataobjects.DecentralizedTask;
import ot.webtest.framework.kketshelpers.dataobjects.TaskSource;
import ot.webtest.framework.kketshelpers.dataobjects.WorkOrderTask;

import java.util.ArrayList;
import java.util.List;

import static ot.webtest.framework.helpers.AllureHelper.logPassed;
import static ot.webtest.framework.helpers.AllureHelper.logScreenshot;
import static ot.webtest.framework.helpers.AllureHelper.logSkipped;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class TaskCreationHelper extends HelperBase {

    /*****************************************************************************************************************
     * ДЕЦЕНТРАЛИЗОВАННОЕ ЗАДАНИЕ
     ****************************************************************************************************************/

    @Step("Создаём децентрализованное задание {decentralizedTask}")
    public DecentralizedTask setAndSaveDecentralizedTask(DecentralizedTask decentralizedTask) {

        selectTechnologicalOperation(decentralizedTask.technologicalOperation);
        selectElement(decentralizedTask.element);
        selectSubdivision(decentralizedTask.subdivision);

        setDateStart(decentralizedTask.dateStart);
        setDurationHours(decentralizedTask.durationHours);
        setDateEnd(decentralizedTask.dateEnd);

        if (decentralizedTask.isTaskForColumn != null) {
            setCheckBox("Создать задания на колонну", By.xpath("//div[label[text()='Создать задания на колонну']]/input"), decentralizedTask.isTaskForColumn);
        } else {
            logSkipped("Нет данных для ввода в поле 'Создать задания на колонну'");
        }

        int vehicleCount;
        if (decentralizedTask.isTaskForColumn != null && decentralizedTask.isTaskForColumn == true) {
            vehicleCount = 3;
            logPassed("Т.к. создаётся задание на колонну, то будет выбрано 3 ТС...");
        } else {
            vehicleCount = 1;
        }
        for (int i = 0; i < vehicleCount; i++) {
            logPassed("Добавляем ТС #" + i);
            dropDownSelect(
                    "Транспортное средство",
                    decentralizedTask.vehicle,
                    By.xpath("//div[label[text()='Транспортное средство']]//span[contains(@class, 'Select-arrow-zone')]"),
                    "//div[label[text()='Транспортное средство']]//div[@class='Select-menu']/div[@class='Select-option'][INDEX]/div/div");
        }

        Special<String> routeName = selectRouteName(decentralizedTask.routeName);
        decentralizedTask.withRouteName(routeName);

        logScreenshot("Скриншот перед прокруткой к нижней части формы.");

        if (decentralizedTask.isTaskForColumn != null && decentralizedTask.isTaskForColumn == true) {
            Assert.fail("Выбор путевого листа для колонны не реализован (Он должен быть после сохранения задания по отдельности для каждого ТС)");
        } else {
            dropDownSelect(
                    "Форма ПЛ (путевого листа)",
                    decentralizedTask.waybillStatus == null ? null : new Special<>(decentralizedTask.waybillStatus.toString()),
                    By.xpath("//div[@id='assign-to-waybill']//span[contains(@class, 'Select-arrow-zone')]"),
                    By.xpath("//div[@id='assign-to-waybill']//div[@class='Select-menu']//span"));
        }

        String passesCount = decentralizedTask.passesCount == null ? null : String.valueOf(decentralizedTask.passesCount);
        setValueOrLogNoDataToSet("Кол-во циклов", By.xpath("//div[label/span[text()='Кол-во циклов']]/input"), passesCount);

        selectTaskSource(decentralizedTask.taskSource);
        setComment(decentralizedTask.comment);

        logScreenshot("Скриншот перед сохранением формы.");
        click("Клик по кнопке 'Сохранить'", By.xpath("//button[text()='Сохранить']"));
        return decentralizedTask;

        /*
            div(id="technical-operation-id")
                div(class="Select has-error is-focused is-open is-searchable Select--single")
                    div(class="Select-menu-outer")
                        div(class="Select-menu" id="react-select-2--list")
                            div/div/div/span{Blabla}
                            div/div/div/span{Blabla}
                            div/div/div/span{Blabla}
                            div
                                div
                                    span
             */

        /*
            *
                label (class='' id='car-id') = Транспортное средство
                div (class='' id='car-id')
                    div (class='Select has-error is-focused is-open is-searchable Select--single' id='') =
                        div (class='Select-control' id='') =
                        div (class='Select-menu-outer' id='') =
                            div(id="react-select-5--list" class="Select-menu")
                                div(id="react-select-5--option-183" class="Select-option")
                                    div(id="52991")
                                        div(text="0514НМ77 [ПФС-0,75/МТЗ - 82.1/ТУ]")
            * */
    }

    private void setComment(String comment) {
        setValueOrLogNoDataToSet("Комментарий", By.xpath("//div[label/span[text()='Комментарий']]/input"), comment);
    }

    private void selectTaskSource(TaskSource taskSource) {
        dropDownSelect(
                "Источник получения задания",
                taskSource == null ? null : new Special<>(taskSource.toString()),
                By.xpath("//div[label[text()='Источник получения задания']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Источник получения задания']]//div[@class='Select-menu']//span"));
    }

    @Step("В выпадающем списке 'Маршрут' выбираем опцию '{routeName}'")
    private Special<String> selectRouteName(Special<String> routeName) {
        Special<String> routeNameSelected =
                dropDownSelect(
                        "Маршрут",
                        routeName,
                        By.xpath("//div[label[text()='Маршрут']]//span[contains(@class, 'Select-arrow-zone')]"),
                        By.xpath("//div[label[text()='Маршрут']]//div[@class='Select-menu']//span"));

        LoaderHelper loader = new LoaderHelper();
        loader.waitLoaderDisappear();
        return routeNameSelected;
    }

    @Step("Устанавливаем 'Временя выполнения. Окончание.' в значение '{dateEnd}'")
    private void setDateEnd(SpecialDateTime dateEnd) {
        CalendarHelper calendarHelper1 = new CalendarHelper();
        if (dateEnd != null && dateEnd.date != null) {
            // @id='date_end' for Decentralized || @id='plan-date-end' for
            click("Раскрываем календарь установки 'Временя выполнения. Окончание.'", By.xpath("//div[@id='date_end' or @id='plan-date-end']//button[@aria-label='Select date']"));
            sleepMillis(300);
            calendarHelper1.setDate(dateEnd.date);
            dropDownSelect(
                    "Временя выполнения. Окончание [часы:минуты].",
                    new Special<>(dateEnd.getTime24()),
                    By.xpath("//div[@id='date_end' or @id='plan-date-end']//button[@aria-label='Select time']"),
                    By.xpath("//ul[@id='date_end_listbox' or @id='plan-date-end_listbox']/li"));
        } else {
            logSkipped("Нет данных для ввода в поле 'Временя выполнения. Окончание.'");
        }
    }

    @Step("Устанавливаем 'Продолжительность задания, ч.' в значение '{durationHours}'")
    private void setDurationHours(Integer durationHours) {
        if (durationHours != null) {
            Assert.fail("Не реализован ввод через список 'Продолжительность задания, ч.', т.к. если там и будет баг, то его можно обойти непосредственно вводом в поле 'Время окончания'");
        } else {
            logSkipped("Нет данных для ввода в поле 'Продолжительность задания, ч.'");
        }
    }

    @Step("Устанавливаем 'Временя выполнения. Начало.' в значение '{dateStart}'")
    private void setDateStart(SpecialDateTime dateStart) {
        CalendarHelper calendarHelper = new CalendarHelper();
        if (dateStart != null && dateStart.date != null) {
            // @id='date-start' for Decentralized || @id='plan-date-start' for
            click("Раскрываем календарь установки 'Временя выполнения. Начало.'", By.xpath("//div[@id='date-start' or @id='plan-date-start']//button[@aria-label='Select date']"));
            sleepMillis(300);
            calendarHelper.setDate(dateStart.date);
            dropDownSelect(
                    "Временя выполнения. Начало [часы:минуты].",
                    new Special<>(dateStart.getTime24()),
                    By.xpath("//div[@id='date-start' or @id='plan-date-start']//button[@aria-label='Select time']"),
                    By.xpath("//ul[@id='date-start_listbox' or @id='plan-date-start_listbox']/li"));
        } else {
            logSkipped("Нет данных для ввода в поле 'Временя выполнения. Начало.'");
        }
    }

    private void selectSubdivision(Special<String> subdivision) {
        dropDownSelect(
                "Подразделение",
                subdivision,
                By.xpath("//div[label[text()='Подразделение']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Подразделение']]//div[@class='Select-menu']//span"));
    }

    @Step("В выпадающем списке 'Элемент' выбираем опцию '{element}'")
    private void selectElement(Special<String> element) {
        dropDownSelect(
                "Элемент",
                element,
                By.xpath("//div[label[text()='Элемент']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Элемент']]//div[@class='Select-menu']//span"));
        LoaderHelper loader = new LoaderHelper();
        loader.waitLoaderDisappear();
    }

    private void selectTechnologicalOperation(Special<String> technologicalOperation) {
        dropDownSelect(
                "Технологическая операция",
                technologicalOperation,
                By.xpath("//div[label[text()='Технологическая операция']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Технологическая операция']]//div[@class='Select-menu']//span"));
    }

    @Step("Проверка появления сообщения 'Задание создано успешно.' (и его закрытие)")
    public void checkAndCloseMessageTaskCreatedSuccessfully() {
        checkAppear(By.xpath("//div[text()='Задание создано успешно.' and @class='notification-message']"));
        click(By.xpath("//div[div[text()='Задание создано успешно.' and @class='notification-message']]/span[text()='×']"));
    }


    /*****************************************************************************************************************
     * НАРЯД-ЗАДАНИЕ
     ****************************************************************************************************************/

    @Step("Создаём наряд-задание {workOrderTask}")
    public WorkOrderTask setAndSaveWorkOrderTask(WorkOrderTask workOrderTask) {

        selectTechnologicalOperation(workOrderTask.technologicalOperation);
        selectElement(workOrderTask.element);
        selectSubdivision(workOrderTask.subdivision);

        setDateStart(workOrderTask.dateStart);
        setDateEnd(workOrderTask.dateEnd);

        Special<String> brigadier = selectBrigadier(workOrderTask.brigadier);
        workOrderTask.withBrigadier(brigadier);
        List<Special<String>> brigade = selectBrigade(workOrderTask.brigade);
        workOrderTask.withBrigade(brigade);

        selectTaskSource(workOrderTask.taskSource);
        setComment(workOrderTask.comment);

        // Задание на ТС - ???
        logSkipped("Не заполняется поле 'Задание на ТС', т.к. не видел ещё чтобы там было како-либо значение для выбора.");

        Special<String> routeName = selectRouteName(workOrderTask.routeName);
        workOrderTask.withRouteName(routeName);

        logScreenshot("Скриншот перед сохранением формы.");
        click("Клик по кнопке 'Сохранить'", By.xpath("//button[text()='Сохранить']"));

        LoaderHelper loaderHelper = new LoaderHelper();
        loaderHelper.waitLoaderDisappear();

        return workOrderTask;
    }

    @Step("Выбираем бригаду '{brigade}'")
    private List<Special<String>> selectBrigade(List<Special<String>> brigade) {
        By clearAllLocator = By.xpath("//div[label[text()='Бригада']]//span[@title='Clear all']/span[text()='×']");
        if (isDisplayedWithTimeout("Проверяем наличие 'x' (Clear all) в поле (если уже есть бригада по умолчанию)", clearAllLocator, 1000)) {
            click("Очищаем поле 'Бригада'", clearAllLocator);
        }
        List<Special<String>> brigadeSelected = new ArrayList<>();
        for (int i = 0; i < brigade.size(); i++) {
            Special<String> worker = dropDownSelect(
                    "Бригада",
                    brigade.get(i),
                    By.xpath("//div[label[text()='Бригада']]//span[contains(@class, 'Select-arrow-zone')]"),
                    "//div[label[text()='Бригада']]//div[@class='Select-menu']/div[@class='Select-option'][INDEX]/div/span");
            brigadeSelected.add(worker);
        }
        return brigadeSelected;
    }

    private Special<String> selectBrigadier(Special<String> brigadier) {
        return dropDownSelect(
                "Бригадир",
                brigadier,
                By.xpath("//div[label[text()='Бригадир']]//span[contains(@class, 'Select-arrow-zone')]"),
                "//div[label[text()='Бригадир']]//div[@class='Select-menu']/div[@class='Select-option'][INDEX]/div/span");
    }
}
