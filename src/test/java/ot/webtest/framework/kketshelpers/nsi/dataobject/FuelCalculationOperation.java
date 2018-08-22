package ot.webtest.framework.kketshelpers.nsi.dataobject;

import io.qameta.allure.Step;
import org.testng.Assert;
import ot.webtest.framework.helpers.AssertHelper;

public class FuelCalculationOperation {
    public String operationName;
    public FuelConsumptionMetrics fuelConsumptionMetrics;
    public Boolean isWithoutMileageRecording;
    public Boolean isForSpecialEquipment;

    public FuelCalculationOperation withOperationName (String operationName) {
        this.operationName = operationName;
        return this;
    }
    public FuelCalculationOperation withFuelConsumptionMetrics (FuelConsumptionMetrics fuelConsumptionMetrics) {
        this.fuelConsumptionMetrics = fuelConsumptionMetrics;
        return this;
    }
    public FuelCalculationOperation withIsWithoutMileageRecording (Boolean isWithoutMileageRecording) {
        this.isWithoutMileageRecording = isWithoutMileageRecording;
        return this;
    }
    public FuelCalculationOperation withIsForSpecialEquipment (Boolean isForSpecialEquipment) {
        this.isForSpecialEquipment = isForSpecialEquipment;
        return this;
    }

    @Override
    public String toString() {
        String str = "";
        if (operationName != null) {
            str += "operationName: " + operationName + "; ";
        } else {
            str += "operationName: NULL; ";
        }
        if (fuelConsumptionMetrics != null) {
            str += "fuelConsumptionMetrics: " + fuelConsumptionMetrics.toString();
        } else {
            str += "fuelConsumptionMetrics: NULL";
        }
        return "{" + str + "}";
    }

    @Step("Проверка равенства операций фактической '{fuelCalculationOperationActual}' и ожидаемой '{fuelCalculationOperationExpected}'")
    public static void checkEquals(FuelCalculationOperation fuelCalculationOperationActual, FuelCalculationOperation fuelCalculationOperationExpected) {
        if (fuelCalculationOperationActual == null) {
            Assert.fail("Фактическое значение не может быть NULL");
        }
        if (fuelCalculationOperationExpected == null) {
            Assert.fail("Ожидаемое значение не может быть NULL");
        }
        AssertHelper.assertEquals(fuelCalculationOperationActual.operationName, fuelCalculationOperationExpected.operationName, "Поле 'Операция (название)'");
        AssertHelper.assertEquals(fuelCalculationOperationActual.fuelConsumptionMetrics, fuelCalculationOperationExpected.fuelConsumptionMetrics, "Поле 'Единица измерения'");
        AssertHelper.assertEquals(fuelCalculationOperationActual.isWithoutMileageRecording, fuelCalculationOperationExpected.isWithoutMileageRecording, "Поле 'Без учета пробега'");
        AssertHelper.assertEquals(fuelCalculationOperationActual.isForSpecialEquipment, fuelCalculationOperationExpected.isForSpecialEquipment, "Поле 'Для спецоборудования'");
    }
}
