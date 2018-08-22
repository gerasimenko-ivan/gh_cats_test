package ot.webtest.framework.kketshelpers.nsi.dataobject;

import io.qameta.allure.Step;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.helpers.AssertHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FuelConsumptionNorm {
    public LocalDate orderDate;
    public Special<String> operationName;
    public Special<String> fuelConsumptionMetrics;
    public Double normForSummer;
    public Double normForWinter;
    public Special<String> vehicleModel;
    public Special<String> vehicleChassisBrand;
    public Special<String> subdivision;


    public FuelConsumptionNorm withOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
        return this;
    }
    public FuelConsumptionNorm withOperationName(Special<String> operationName) {
        this.operationName = operationName;
        return this;
    }
    public FuelConsumptionNorm withFuelConsumptionMetrics(Special<String> fuelConsumptionMetrics) {
        this.fuelConsumptionMetrics = fuelConsumptionMetrics;
        return this;
    }
    public FuelConsumptionNorm withNormForSummer(Double normForSummer) {
        this.normForSummer = normForSummer;
        return this;
    }
    public FuelConsumptionNorm withNormForWinter(Double normForWinter) {
        this.normForWinter = normForWinter;
        return this;
    }
    public FuelConsumptionNorm withVehicleModel(Special<String> vehicleModel) {
        this.vehicleModel = vehicleModel;
        return this;
    }
    public FuelConsumptionNorm withVehicleChassisBrand(Special<String> vehicleChassisBrand) {
        this.vehicleChassisBrand = vehicleChassisBrand;
        return this;
    }
    public FuelConsumptionNorm withSubdivision(Special<String> subdivision) {
        this.subdivision = subdivision;
        return this;
    }

    @Override
    public String toString() {
        String objectDescription = "{";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.LL.yyyy");
        objectDescription += "orderDate: " + (orderDate == null ? "NULL" : orderDate.format(formatter)) + "; ";
        objectDescription += "operationName: " + (operationName == null ? "NULL" : operationName) + "; ";
        objectDescription += "fuelConsumptionMetrics: " + (fuelConsumptionMetrics == null ? "NULL" : fuelConsumptionMetrics) + "}";
        return objectDescription;
    }

    @Step("Проверка равенства норм расхода топлива фактической '{fuelConsumptionNormActual}' и ожидаемой '{fuelConsumptionNormExpected}'")
    public static void checkEquals(FuelConsumptionNorm fuelConsumptionNormActual, FuelConsumptionNorm fuelConsumptionNormExpected) {
        AssertHelper.assertEquals(fuelConsumptionNormActual.orderDate, fuelConsumptionNormExpected.orderDate, "Поле 'Дата приказа'");
        AssertHelper.assertEquals(fuelConsumptionNormActual.operationName, fuelConsumptionNormExpected.operationName, "Поле 'Операция'");
        AssertHelper.assertEquals(fuelConsumptionNormActual.fuelConsumptionMetrics, fuelConsumptionNormExpected.fuelConsumptionMetrics, "Поле 'Единица измерения'");
        AssertHelper.assertEquals(fuelConsumptionNormActual.normForSummer, fuelConsumptionNormExpected.normForSummer, "Поле 'Норма для летнего периода'");
        AssertHelper.assertEquals(fuelConsumptionNormActual.normForWinter, fuelConsumptionNormExpected.normForWinter, "Поле 'Норма для зимнего периода'");
        AssertHelper.assertEquals(fuelConsumptionNormActual.vehicleModel, fuelConsumptionNormExpected.vehicleModel, "Поле 'Модель ТС'");
        AssertHelper.assertEquals(fuelConsumptionNormActual.vehicleChassisBrand, fuelConsumptionNormExpected.vehicleChassisBrand, "Поле 'Марка шасси ТС'");
        AssertHelper.assertEquals(fuelConsumptionNormActual.subdivision, fuelConsumptionNormExpected.subdivision, "Поле 'Подразделение'");
    }
}
