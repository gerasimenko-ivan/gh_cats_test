package ot.webtest.framework.kketshelpers.dataobjects;

import io.qameta.allure.Step;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.helpers.AssertHelper;

import java.util.List;

public class WorkOrderTask extends Task {
    public Special<String> brigadier;
    public List<Special<String>> brigade;

    public WorkOrderTask withBrigadier (Special<String> brigadier) {
        this.brigadier = brigadier;
        return this;
    }
    public WorkOrderTask withBrigade (List<Special<String>> brigade) {
        this.brigade = brigade;
        return this;
    }

    @Step("Проверка равенства наряд-заданий фактического '{workOrderTaskActual}' и ожидаемого '{workOrderTaskExpected}'")
    public static void checkEquals(WorkOrderTask workOrderTaskActual, WorkOrderTask workOrderTaskExpected) {
        Task.checkEquals(workOrderTaskActual, workOrderTaskExpected);

        AssertHelper.assertEquals(workOrderTaskActual.brigadier, workOrderTaskExpected.brigadier, "Поле brigadier");
        AssertHelper.assertListEqualsWithSort(workOrderTaskActual.brigade, workOrderTaskExpected.brigade, "Поле brigade");
    }
}
