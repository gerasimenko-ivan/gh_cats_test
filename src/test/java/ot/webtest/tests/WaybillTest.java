package ot.webtest.tests;

import io.qameta.allure.Description;
import org.testng.annotations.Test;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.helpers.RegExpHelper;
import ot.webtest.framework.helpers.StringHelper;
import ot.webtest.framework.kketshelpers.TestDataGenerator;
import ot.webtest.framework.kketshelpers.dataobjects.Waybill;
import ot.webtest.framework.testrail.TestRailCaseId;

public class WaybillTest extends TestBase {

    @Test(description = "05. Создание Путевого листа (ПЛ) (меню Путевые листы) с созданием задания в ПЛ")
    @TestRailCaseId(testCaseId = 1826)
    @Description("1. Создаётся ПЛ\n"
            + "2. Делается сверка с записью в Журнале ПЛ.")
    public void createWaybillTest() {
// data preparation
        Waybill waybill = TestDataGenerator.getWaybill();

        // test
        bw.mainMenu().gotoWaybills();
        bw.waybillJournal().pressCreateWaybillButton();

        bw.waybillCreation().setAndSaveWaybill(waybill);

        bw.mainMenu().gotoWaybills();
        bw.waybillJournal().filterByVehicleRegNumberAndPlannedDateLeave("1488НВ77", waybill.dateLeavePlanned.date);

        Waybill waybillActual =
                bw.waybillJournal().getItemByDateLeavePlanned(waybill.dateLeavePlanned);


        // data preparation
        Special<String> fioOfSurname_NM_format = new Special<>(StringHelper.fioToSurname_XX(RegExpHelper.getSubstring("[А-Яа-яЁё]+\\s[А-Яа-яЁё]+(\\s[А-Яа-яЁё]+)?", waybill.driver.toString())));
        waybill
                .withCreatedBy(user.surnameNM)
                .withDriver(fioOfSurname_NM_format)
                .withVehicleRegNumber(new Special<>("1488НВ77"))
                .withVehicle(null);

        // check
        Waybill.checkEquals(waybillActual, waybill);
    }
}
