package ot.webtest.tests;

import io.qameta.allure.Description;
import org.testng.annotations.Test;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.helpers.RandomDataGenerator;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelCalculationOperation;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelConsumptionMetrics;
import ot.webtest.framework.kketshelpers.nsi.dataobject.FuelConsumptionNorm;
import ot.webtest.framework.testrail.TestRailCaseId;

import java.time.LocalDate;

public class NsiTest extends TestBase {

    @Test(description = "Создание карточки 'Операция для расчета топлива'")
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

        FuelCalculationOperation.checkEquals(fuelCalculationOperationSaved, fuelCalculationOperation);
    }

    @Test(description = "Создание карточки 'Норма расхода топлива'")
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

        bw.fuelConsumptionNorm().filterByItem(fuelConsumptionNormToCreate);
        FuelConsumptionNorm fuelConsumptionNormActual =
                bw.fuelConsumptionNorm().getFromTableByItem(fuelConsumptionNormToCreate);

        // preparation before comparison - ' [спецоборудование]' flag is not displayed in table
        fuelConsumptionNormToCreate
                .withOperationName(new Special<>(fuelConsumptionNormToCreate.operationName.toString().replace(" [спецоборудование]", "")));
        // comparison
        FuelConsumptionNorm.checkEquals(fuelConsumptionNormActual, fuelConsumptionNormToCreate);
    }
}
