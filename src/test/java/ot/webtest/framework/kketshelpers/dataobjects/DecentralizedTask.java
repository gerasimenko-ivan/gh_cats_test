package ot.webtest.framework.kketshelpers.dataobjects;

import io.qameta.allure.Step;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.helpers.AssertHelper;

public class DecentralizedTask extends Task {

    public Boolean isTaskForColumn;
    public Special<String> vehicle;
    public String vehicleRegNumber;
    public String vehicleType;
    public Integer passesCount;
    public WaybillStatus waybillStatus;
    public Integer durationHours;


    public DecentralizedTask withIsTaskForColumn (Boolean isTaskForColumn) {
        this.isTaskForColumn = isTaskForColumn;
        return this;
    }

    public DecentralizedTask withVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
        return this;
    }

    public DecentralizedTask withVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
        return this;
    }

    public DecentralizedTask withVehicle(Special<String> vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public DecentralizedTask withPassesCount (Integer passesCount) {
        this.passesCount = passesCount;
        return this;
    }

    public DecentralizedTask withWaybillStatus(WaybillStatus waybillStatus) {
        this.waybillStatus = waybillStatus;
        return this;
    }

    public DecentralizedTask withDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Step("Проверка равенства заданий фактического '{decentralizedTaskActual}' и ожидаемого '{decentralizedTaskExpected}'")
    public static void checkEquals(DecentralizedTask decentralizedTaskActual, DecentralizedTask decentralizedTaskExpected) {
        Task.checkEquals(decentralizedTaskActual, decentralizedTaskExpected);

        AssertHelper.assertEquals(decentralizedTaskActual.isTaskForColumn, decentralizedTaskExpected.isTaskForColumn, "Поле isTaskForColumn");
        AssertHelper.assertEquals(decentralizedTaskActual.vehicle, decentralizedTaskExpected.vehicle, "Поле vehicle");
        AssertHelper.assertEquals(decentralizedTaskActual.vehicleRegNumber, decentralizedTaskExpected.vehicleRegNumber, "Поле vehicleRegNumber");
        AssertHelper.assertEquals(decentralizedTaskActual.vehicleType, decentralizedTaskExpected.vehicleType, "Поле vehicleType");
        AssertHelper.assertEquals(decentralizedTaskActual.passesCount, decentralizedTaskExpected.passesCount, "Поле passesCount");
        AssertHelper.assertEquals(decentralizedTaskActual.waybillStatus, decentralizedTaskExpected.waybillStatus, "Поле waybillStatus");
        AssertHelper.assertEquals(decentralizedTaskActual.durationHours, decentralizedTaskExpected.durationHours, "Поле durationHours");
    }
}
