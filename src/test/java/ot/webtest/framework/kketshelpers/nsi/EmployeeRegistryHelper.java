package ot.webtest.framework.kketshelpers.nsi;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.dataobject.DropDownResponse;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.helpers.DateHelper;
import ot.webtest.framework.helpers.RegExpHelper;
import ot.webtest.framework.kketshelpers.CalendarHelper;
import ot.webtest.framework.kketshelpers.LoaderHelper;
import ot.webtest.framework.kketshelpers.dataobjects.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.StringHelper.isNotEmptyString;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class EmployeeRegistryHelper extends HelperBase {

    /*****************************************************************************************************************
     * LOCATORS
     ****************************************************************************************************************/

    By mainVehicleOpenDropDown_ButtonLocator = By.xpath("//div[label[text()='Основное ТС']]//span[contains(@class, 'Select-arrow-zone')]");
    By secondaryVehicleOpenDropDown_ButtonLocator = By.xpath("//div[label[text()='Вторичное ТС']]//span[contains(@class, 'Select-arrow-zone')]");

    // Срок действия специального удостоверения
    String specialLicenseExpireDate_FieldName = "Срок действия специального удостоверения";
    By specialLicenseExpireDate_InputLocator = By.xpath("//div[label[text()='Срок действия специального удостоверения']]/div/div/input");
    By specialLicenseExpireDate_CalendarOpenerLocator = By.xpath("//div[label[text()='Срок действия специального удостоверения']]/div/div/span/button[@aria-label='Select date']");

    // Срок действия водительского удостоверения
    String driverLicenseExpireDate_FieldName = "Срок действия водительского удостоверения";
    By driverLicenseExpireDate_InputLocator = By.xpath("//div[label[text()='Срок действия водительского удостоверения']]/div/div/input");
    By driverLicenseExpireDate_CalendarOpenerLocator = By.xpath("//div[label[text()='Срок действия водительского удостоверения']]/div/div/span/button[@aria-label='Select date']");


    /*****************************************************************************************************************
     * CREATION
     ****************************************************************************************************************/

    @Step("Создаём карточку сотрудника '{employee}'")
    public Employee createEmployeeCard(Employee employee) {
        CalendarHelper calendar = new CalendarHelper();

        pressCreateButton();

        setValueOrLogNoDataToSet("Фамилия", By.xpath("//div[label/span[text()='Фамилия']]/input"), employee.surname);
        setValueOrLogNoDataToSet("Имя", By.xpath("//div[label/span[text()='Имя']]/input"), employee.name);
        setValueOrLogNoDataToSet("Табельный номер", By.xpath("//div[label/span[text()='Табельный номер']]/input"), employee.personnelNumber);

        DropDownResponse positionDropDownResponse =
                selectPosition(employee.position);
        employee.withPosition(positionDropDownResponse.selectedValue);

        Special<String> subdevision = dropDownSelect(
                "Подразделение",
                employee.subdivision,
                By.xpath("//div[label[text()='Подразделение']]//span[contains(@class, 'Select-arrow-zone')]"),
                By.xpath("//div[label[text()='Подразделение']]//div[contains(@class,'Select-menu')]//span"));
        employee.withSubdivision(subdevision);

        setSpecialLicenceId_checkDataEnabling(employee.specialLicenseId);
        calendar.setDateOrLogNoDataToSet(
                employee.specialLicenseExpireDate,
                specialLicenseExpireDate_FieldName,
                specialLicenseExpireDate_CalendarOpenerLocator);

        setDriverLicenseId_checkDateEnabling(employee.driverLicenseId);
        calendar.setDateOrLogNoDataToSet(
                employee.driverLicenseExpireDate,
                driverLicenseExpireDate_FieldName,
                driverLicenseExpireDate_CalendarOpenerLocator);

        checkVehicleFieldsDisOrEnabled(employee.position);
        DropDownResponse mainVehicle = dropDownSelectGeneral(
                "Основное ТС",
                employee.mainVehicle,
                mainVehicleOpenDropDown_ButtonLocator,
                By.xpath("//div[label[text()='Основное ТС']]//div[contains(@class,'Select-menu')]/div[contains(@class,'Select-option')]//span"),
                null);
        if (mainVehicle != null) {
            employee.withMainVehicle(mainVehicle.selectedValue);
            checkVehicleNumbers(mainVehicle.availableOptions, employee.specialLicenseExpireDate, employee.driverLicenseExpireDate);
        }

        List<Special<String>> secondaryVehiclesSelected =
                selectSecondaryVehiclesAndCheckVehicleNumbers(employee.secondaryVehicles, employee.specialLicenseExpireDate, employee.driverLicenseExpireDate);
        employee.withSecondaryVehicles(secondaryVehiclesSelected);


        logScreenshot("Скриншот перед сохранением формы.");
        clickIgnoreException("Клик на кнопке 'Сохранить'", By.xpath("//button[text()='Сохранить']"));

        LoaderHelper loader = new LoaderHelper();
        loader.waitLoaderDisappear();

        return employee;
    }

    @Step("Проверка (не)активности полей 'Основное ТС' и 'Вторичное ТС'")
    private void checkVehicleFieldsDisOrEnabled(Special<String> position) {
        String cursor = $(mainVehicleOpenDropDown_ButtonLocator).getCssValue("cursor");
        boolean doesExistInputOfMainVehicle = doesExist(By.xpath("//div[label[text()='Основное ТС']]//input"), 0);
        if (position != null && (position.toString().equals("водитель") || position.toString().equals("машинист"))) {
            if (cursor.equals("pointer") && doesExistInputOfMainVehicle) {
                logPassed("PASSED: 'Основное ТС' - enabled, т.к. Должность = " + position.toString());
            } else {
                Assert.fail("FAILED: 'Основное ТС' - disabled, но ожидается enabled, т.к. Должность = " + position.toString());
            }
        } else {
            if (!cursor.equals("pointer") && !doesExistInputOfMainVehicle) {
                logPassed("PASSED: 'Основное ТС' - disabled, т.к. Должность != 'водитель' или 'машинист'");
            } else {
                Assert.fail("FAILED: 'Основное ТС' - enabled, но ожидается disabled, т.к. Должность != 'водитель' или 'машинист'");
            }
        }

        String cursorOfSecondaryVehicleDropDownOpener = $(secondaryVehicleOpenDropDown_ButtonLocator).getCssValue("cursor");
        boolean doesExistInputOfSecondaryVehicle = doesExist(By.xpath("//div[label[text()='Вторичное ТС']]//input"), 0);
        if (position != null && (position.toString().equals("водитель") || position.toString().equals("машинист"))) {
            if (cursorOfSecondaryVehicleDropDownOpener.equals("pointer") && doesExistInputOfSecondaryVehicle) {
                logPassed("PASSED: 'Вторичное ТС' - enabled, т.к. Должность = " + position.toString());
            } else {
                Assert.fail("FAILED: 'Вторичное ТС' - disabled, но ожидается enabled, т.к. Должность = " + position.toString());
            }
        } else {
            if (!cursorOfSecondaryVehicleDropDownOpener.equals("pointer") && !doesExistInputOfSecondaryVehicle) {
                logPassed("PASSED: 'Вторичное ТС' - disabled, т.к. Должность != 'водитель' или 'машинист'");
            } else {
                Assert.fail("FAILED: 'Вторичное ТС' - enabled, но ожидается disabled, т.к. Должность != 'водитель' или 'машинист'");
            }
        }
    }

    public DropDownResponse selectPosition(Special<String> position) {
        return dropDownSelectGeneral(
                "Должность",
                position,
                By.xpath("//div[label[text()='Должность']]//span[contains(@class, 'Select-arrow-zone')]"),
                null,
                "//div[label[text()='Должность']]//div[contains(@class,'Select-menu')]/div[contains(@class,'Select-option')][INDEX]/div/span");
    }

    @Step("Нажимаем на кнопке '+ Создать'")
    public void pressCreateButton() {
        click("Нажимаем на кнопке '+ Создать'", By.xpath("//button[@id='open-create-form']"));
    }

    @Step("Нажимаем на кнопке 'Отмена'")
    public void pressCloseCreationFormXButton() {
        click("Нажимаем на кнопке [×]", By.xpath("//div[h4[text()='Создание сотрудника']]/button/span[text()='×']"));
    }

    @Step("Проверка регистрационных номеров ТС на соответствие ТЗ")
    private void checkVehicleNumbers(List<String> vehicleDescriptions, LocalDate specialLicenseExpireDate, LocalDate driverLicenseExpireDate) {
        String pattern = "";
        if (specialLicenseExpireDate != null && LocalDate.now().atStartOfDay().toLocalDate().compareTo(specialLicenseExpireDate) < 0) {
            pattern = "(^[А-Я]{2}\\d{4}\\d{2,3}/)|(^\\d{4}[А-Я]{2}\\d{2,3}/)";
            logPassed("По дате ВУ не просрочено, проверяем формат '0000хх00(0)' или 'хх000000(0)'");
        }
        if (driverLicenseExpireDate != null && LocalDate.now().atStartOfDay().toLocalDate().compareTo(driverLicenseExpireDate) < 0) {
            if (!pattern.equals(""))
                pattern += "|";
            pattern += "(^[А-Я]\\d{3}[А-Я]{2}\\d{2,3}/)";
            logPassed("По дате СУ не просрочено, проверяем формат 'х000хх00(0)'");
        }
        for (String description : vehicleDescriptions) {
            if (RegExpHelper.getSubstring(pattern, description) == null) {
                Assert.fail("Значение '" + description + "' НЕ начинается с гос.номера требуемого формата.");
            }
        }
        logPassed("PASSED: Все значения соответствуют формату.");
    }

    @Step("Выбираем в выпадающем списке 'Вторичное ТС' следующие значения '{secondaryVehicles}'. Делаем проверку некоторых гос.номеров ТС на соответствие формату.")
    private List<Special<String>> selectSecondaryVehiclesAndCheckVehicleNumbers(List<Special<String>> secondaryVehicles, LocalDate specialLicenseExpireDate, LocalDate driverLicenseExpireDate) {
        if (secondaryVehicles == null) {
            logSkipped("Нет данных для ввода в поле 'Вторичное ТС'");
            return null;
        }
        By clearAllLocator = By.xpath("//div[label[text()='Вторичное ТС']]//span[@title='Clear all']/span[text()='×']");
        if (isDisplayedWithTimeout("Проверяем наличие 'x' (Clear all) в поле (если уже есть Вторичное ТС по умолчанию)", clearAllLocator, 1000)) {
            click("Очищаем поле 'Вторичное ТС'", clearAllLocator);
        }
        List<Special<String>> secondaryVehiclesSelected = new ArrayList<>();
        List<String> options = new ArrayList<>();
        for (int i = 0; i < secondaryVehicles.size(); i++) {
            DropDownResponse dropDownResponse = dropDownSelectGeneral(
                    "Бригада",
                    secondaryVehicles.get(i),
                    secondaryVehicleOpenDropDown_ButtonLocator,
                    By.xpath("//div[label[text()='Вторичное ТС']]//div[contains(@class,'Select-menu')]/div[contains(@class,'Select-option')]//span"),
                    null);
            secondaryVehiclesSelected.add(dropDownResponse.selectedValue);
            options.addAll(dropDownResponse.availableOptions);
        }

        checkVehicleNumbers(options, specialLicenseExpireDate, driverLicenseExpireDate);

        return secondaryVehiclesSelected;
    }

    @Step("Ввод в поле 'Водительское удостоверение' значения '{driverLicenseId}' и проверка активности соответствующей даты.")
    private void setDriverLicenseId_checkDateEnabling(String driverLicenseId) {
        checkDisabled(driverLicenseExpireDate_FieldName, driverLicenseExpireDate_InputLocator);
        checkDisabled("Кнопка раскрытия календаря <" + driverLicenseExpireDate_FieldName + ">", driverLicenseExpireDate_CalendarOpenerLocator);

        setValueOrLogNoDataToSet("Водительское удостоверение", By.xpath("//div[label/span[text()='Водительское удостоверение']]/input"), driverLicenseId);

        if (isNotEmptyString(driverLicenseId)) {
            checkEnabled(driverLicenseExpireDate_FieldName, driverLicenseExpireDate_InputLocator);
            checkEnabled("Кнопка раскрытия календаря <" + driverLicenseExpireDate_FieldName + ">", driverLicenseExpireDate_CalendarOpenerLocator);
        }
    }

    @Step("Ввод в поле 'Специальное удостоверение' значения '{specialLicenseId}' и проверка активности соответствующей даты.")
    private void setSpecialLicenceId_checkDataEnabling(String specialLicenseId) {
        checkDisabled(specialLicenseExpireDate_FieldName, specialLicenseExpireDate_InputLocator);
        checkDisabled("Кнопка раскрытия календаря <" + specialLicenseExpireDate_FieldName + ">", specialLicenseExpireDate_CalendarOpenerLocator);

        setValueOrLogNoDataToSet("Специальное удостоверение", By.xpath("//div[label/span[text()='Специальное удостоверение']]/input"), specialLicenseId);

        if (isNotEmptyString(specialLicenseId)) {
            checkEnabled(specialLicenseExpireDate_FieldName, specialLicenseExpireDate_InputLocator);
            checkEnabled("Кнопка раскрытия календаря <" + specialLicenseExpireDate_FieldName + ">", specialLicenseExpireDate_CalendarOpenerLocator);
        }
    }


    /*****************************************************************************************************************
     * FILTERING
     ****************************************************************************************************************/

    @Step("Фильтрация по табельному номеру '{personnelNumber}'")
    public void filterByPersonnelNumber(String personnelNumber) {
        By openCloseFilterButtonLocator = By.xpath("//button[@id='show-options-filter']");
        click("Раскрываем фильтры", openCloseFilterButtonLocator);
        sleepMillis(200);

        setValueOrLogNoDataToSet("Табельный номер", By.xpath("//div[label[text()='Табельный номер']]/div/input"), personnelNumber);

        click("Клик по кнопке 'Применить'", By.xpath("//button[@id='apply-filter']"));
        sleepMillis(200);
        logScreenshot("Скриншот по применению фильтра");
        click("Закрываем фильтры", openCloseFilterButtonLocator);
    }


    /*****************************************************************************************************************
     * WORK WITH TABLE
     ****************************************************************************************************************/

    @Step("Получение сотрудника из таблицы по полю 'Табельный номер' = '{personnelNumber}'")
    public Employee getRowByPersonnelNumber(String personnelNumber) {
        ElementsCollection elementsTd = getElements("Поиск записи по полю 'Табельный номер' = '" + personnelNumber + "'", By.xpath("//tr[td/div[text()='" + personnelNumber + "']]/td"));
        if (elementsTd == null || elementsTd.size() == 0) {
            logSkipped("Сотрудник не найден.");
            return null;
        }
        ElementsCollection elementsHeader = getElements("Получаем заголовки столбцов", By.xpath("//div[contains(@class,'griddle-body')]//table/thead/tr/th"));
        if (elementsHeader.size() != elementsTd.size()) {
            Assert.fail("Не совпадает кол-во столбцов для записи n=" + elementsTd.size() + " и для заголовка n=" + elementsHeader.size());
        }

        Employee employee = new Employee();
        for (int i = 0; i < elementsHeader.size(); i++) {
            String headerText = elementsHeader.get(i).getText();
            String tdText = elementsTd.get(i).getText();

            if (headerText.startsWith("Фамилия Имя Отчество")) {
                employee.withFio(tdText);
                continue;
            }
            if (headerText.equals("Табельный номер")) {
                employee.withPersonnelNumber(tdText);
                continue;
            }
            if (headerText.equals("Должность")) {
                employee.withPosition(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Основное ТС")) {
                if (isNotEmptyString(tdText))
                    employee.withMainVehicle(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Вторичное ТС")) {
                if (isNotEmptyString(tdText)) {
                    List<Special<String>> vehiclesNumbers =
                            Arrays.stream(tdText.split(", ")).map(x -> new Special<String>(x)).collect(Collectors.toList());
                    employee.withSecondaryVehicles(vehiclesNumbers);
                }
                continue;
            }
            if (headerText.equals("Водительское удостоверение")) {
                if (isNotEmptyString(tdText))
                    employee.withDriverLicenseId(tdText);
                continue;
            }
            if (headerText.equals("Срок действия водительского удостоверения")) {
                if (tdText != null && RegExpHelper.getSubstring("^\\d{2}\\.\\d{2}\\.\\d{4}$", tdText) != null)
                    employee.withDriverLicenseExpireDate(DateHelper.getFromDDMMYYYY(tdText));
                continue;
            }
            if (headerText.equals("Специальное удостоверение")) {
                if (isNotEmptyString(tdText))
                    employee.withSpecialLicenseId(tdText);
                continue;
            }
            if (headerText.equals("Срок действия специального удостоверения")) {
                if (tdText != null && RegExpHelper.getSubstring("^\\d{2}\\.\\d{2}\\.\\d{4}$", tdText) != null)
                    employee.withSpecialLicenseExpireDate(DateHelper.getFromDDMMYYYY(tdText));
                continue;
            }
            if (headerText.equals("Подразделение")) {
                if (isNotEmptyString(tdText))
                    employee.withSubdivision(new Special<>(tdText));
                continue;
            }
        }
        return employee;
    }
}
