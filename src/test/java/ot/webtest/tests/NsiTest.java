package ot.webtest.tests;

import io.qameta.allure.Description;
import org.testng.annotations.Test;
import ot.webtest.dataobject.DropDownResponse;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.helpers.RandomDataGenerator;
import ot.webtest.framework.helpers.StringHelper;
import ot.webtest.framework.kketshelpers.TestDataGenerator;
import ot.webtest.framework.kketshelpers.dataobjects.Employee;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelCalculationOperation;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelConsumptionMetrics;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelConsumptionNorm;
import ot.webtest.framework.testrail.TestRailCaseId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ot.webtest.framework.helpers.AllureHelper.logDelimeter;
import static ot.webtest.framework.helpers.AllureHelper.logFailed;
import static ot.webtest.framework.helpers.AllureHelper.logPassed;
import static ot.webtest.framework.helpers.AssertHelper.checkTrue;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class NsiTest extends TestBase {

    @Test(description = "07. Создание карточки сотрудника (водитель/машинист)")
    @TestRailCaseId(testCaseId = 1829)
    @Description("1. Создаётся карточка сотрудника\n"
            + "2. Фильтруется по полю 'Табельный номер'\n"
            + "3. Проверяется наличие карточки в таблице 'Реестр сотрудников'")
    public void createDriverOrMachinistEmployeeCardTest() {
        // data preparation
        Employee employee = TestDataGenerator.getEmployeeForCreationOfDriverOrMechanistCard();

        // test
        bw.mainMenu().gotoNSI_EmployeesRegistry();
        logFailed("BUG: Поле 'Табельный номер' не сохраняет лидирующие нули!");
        Employee employeeToBeSaved = bw.employeeRegistry().createEmployeeCard(employee);

        bw.employeeRegistry().filterByPersonnelNumber(employee.personnelNumber);
        Employee employeeActual =
                bw.employeeRegistry().getRowByPersonnelNumber(employee.personnelNumber);
        checkTrue(employeeActual != null, "Найдена запись по табельному номеру.", "НЕ Найдена запись по табельному номеру.");

        // data preparation
        employeeToBeSaved.withFio(StringHelper.fioToSingleString(employeeToBeSaved.surname, employee.name, employee.middleName));
        employeeToBeSaved.withSurname(null).withName(null).withMiddleName(null);
        employeeToBeSaved.leaveOnlyRegNumberForVehicle();
        // checking
        Employee.checkEquals(employeeActual, employeeToBeSaved);
    }


    @Test(description = "08. Создание карточки сотрудника (НЕ водитель/машинист)")
    @TestRailCaseId(testCaseId = 1830)
    @Description("1. Создаётся карточка сотрудника\n"
            + "2. Фильтруется по полю 'Табельный номер'\n"
            + "3. Проверяется наличие карточки в таблице 'Реестр сотрудников'")
    public void createNotDriverOrMachinistEmployeeCardTest() {
        // data preparation
        RandomDataGenerator rnd = new RandomDataGenerator();
        Employee employee = TestDataGenerator.getEmployeeNoVehicles();

        // test
        bw.mainMenu().gotoNSI_EmployeesRegistry();

        logDelimeter();
        logPassed("Получаем Должность (НЕ водитель/машинист)");
        bw.employeeRegistry().pressCreateButton();
        DropDownResponse positionDropDownResponse =
                bw.employeeRegistry().selectPosition(Special.RANDOM());
        bw.employeeRegistry().pressCloseCreationFormXButton();
        positionDropDownResponse.availableOptions.remove("водитель");
        positionDropDownResponse.availableOptions.remove("машинист");
        rnd.sortList(positionDropDownResponse.availableOptions);
        employee.withPosition(new Special<>(positionDropDownResponse.availableOptions.get(0)));
        logDelimeter();

        logFailed("BUG: Поле 'Табельный номер' не сохраняет лидирующие нули!");
        Employee employeeToBeSaved = bw.employeeRegistry().createEmployeeCard(employee);

        bw.employeeRegistry().filterByPersonnelNumber(employee.personnelNumber);
        Employee employeeActual =
                bw.employeeRegistry().getRowByPersonnelNumber(employee.personnelNumber);
        checkTrue(employeeActual != null, "Найдена запись по табельному номеру.", "НЕ Найдена запись по табельному номеру.");

        // data preparation
        employeeToBeSaved.withFio(StringHelper.fioToSingleString(employeeToBeSaved.surname, employee.name, employee.middleName));
        employeeToBeSaved.withSurname(null).withName(null).withMiddleName(null);
        employeeToBeSaved.leaveOnlyRegNumberForVehicle();
        // checking
        Employee.checkEquals(employeeActual, employeeToBeSaved);
    }


    @Test(description = "09. Поиск Технологических операций (ТО) в Реестре ТО")
    //@TestRailCaseId(testCaseId = 1831)
    @Description("Проверяем работу различных полей фильтра.")
    public void filteringTechnologicalOperationsTest() {
        bw.mainMenu().gotoNSI_TechnologicalOperationsRegistry();

        //bw.techOperRegistry().
        sleepMillis(5000);
    }


    @Test(description = "10. Создание карточки 'Операция для расчета топлива'")
    @TestRailCaseId(testCaseId = 1961)
    @Description("1. Создаётся карточка 'Операция для расчета топлива'\n"
            + "2. Фильтруется по названию\n"
            + "3. Сравниваются фактическая и ожидаемая карточки")
    public void createCardFuelCalculationOperationTest() {
        // data preparation
        RandomDataGenerator rnd = new RandomDataGenerator();
        FuelCalculationOperation fuelCalculationOperation =
                new FuelCalculationOperation()
                        .withOperationName("TEST " + rnd.getCyrillicWords(5, 15, 1 + rnd.nextInt(3)))
                        .withFuelConsumptionMetrics(FuelConsumptionMetrics.getRandom())
                        .withIsWithoutMileageRecording(rnd.getBooleanNoNull())
                        .withIsForSpecialEquipment(rnd.getBooleanNoNull());

        // test
        bw.mainMenu().gotoNSI_MetrixForCalculations_FuelCalculationOperation();
        bw.fuelCalculationOperation().createOperationForFuelCalculation(fuelCalculationOperation);

        bw.fuelCalculationOperation().filterByOperationName(fuelCalculationOperation.operationName);
        FuelCalculationOperation fuelCalculationOperationSaved =
                bw.fuelCalculationOperation().getOperationForFuelCalculationFromTableByName(fuelCalculationOperation.operationName);
        checkTrue(fuelCalculationOperationSaved != null, "Найдена запись по названию операции.", "НЕ Найдена запись по названию операции.");

        FuelCalculationOperation.checkEquals(fuelCalculationOperationSaved, fuelCalculationOperation);
    }

    @Test(description = "11. Создание карточки 'Норма расхода топлива'")
    @TestRailCaseId(testCaseId = 1960)
    @Description("1. Создаётся карточка 'Норма расхода топлива'\n"
            + "2. Фильтруется по названию\n"
            + "3. Сравниваются фактическая и ожидаемая карточки")
    public void createCardFuelConsumptionNormTest() {
        // data preparation
        RandomDataGenerator rnd = new RandomDataGenerator();
        FuelConsumptionNorm fuelConsumptionNorm =
                new FuelConsumptionNorm()
                        .withOrderDate(rnd.getDate(LocalDate.now().plusDays(-1000), LocalDate.now().plusDays(1000)))
                        .withOperationName(Special.RANDOM())
                        .withFuelConsumptionMetrics(Special.RANDOM())
                        .withNormForSummer(rnd.nextDouble(100000, 3))
                        .withNormForWinter(rnd.nextDouble(100000, 3))
                        .withVehicleModel(Special.RANDOM())
                        .withVehicleChassisBrand(Special.RANDOM())
                        .withSubdivision(Special.RANDOM());

        // test
        bw.mainMenu().gotoNSI_NormativeIndicators_FuelConsumptionNorms();
        FuelConsumptionNorm fuelConsumptionNormToCreate =
                bw.fuelConsumptionNorm().createFuelConsumptionNorm(fuelConsumptionNorm);

        // preparation before comparison & filtering - ' [спецоборудование]' flag is not displayed in table & not used in filters
        fuelConsumptionNormToCreate
                .withOperationName(new Special<>(fuelConsumptionNormToCreate.operationName.toString().replace(" [спецоборудование]", "")));

        // test
        bw.fuelConsumptionNorm().filterByItem(fuelConsumptionNormToCreate);
        FuelConsumptionNorm fuelConsumptionNormActual =
                bw.fuelConsumptionNorm().getFromTableByItem(fuelConsumptionNormToCreate);
        checkTrue(fuelConsumptionNormActual != null, "Найдена запись.", "Запись НЕ найдена.");

        // comparison
        FuelConsumptionNorm.checkEquals(fuelConsumptionNormActual, fuelConsumptionNormToCreate);
    }
}
