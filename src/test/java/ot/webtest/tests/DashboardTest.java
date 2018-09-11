package ot.webtest.tests;

import io.qameta.allure.Description;
import org.testng.annotations.Test;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.helpers.RandomDataGenerator;
import ot.webtest.framework.helpers.RegExpHelper;
import ot.webtest.framework.helpers.StringHelper;
import ot.webtest.framework.kketshelpers.TestDataGenerator;
import ot.webtest.framework.kketshelpers.dataobjects.DecentralizedTask;
import ot.webtest.framework.kketshelpers.dataobjects.Waybill;
import ot.webtest.framework.kketshelpers.dataobjects.WorkOrderTask;
import ot.webtest.framework.testrail.TestRailCaseId;

import java.time.LocalDate;
import java.util.Calendar;

import static ot.webtest.framework.helpers.AllureHelper.logBroken;
import static ot.webtest.framework.helpers.AllureHelper.logFailed;
import static ot.webtest.framework.helpers.AssertHelper.checkTrue;

public class DashboardTest extends TestBase {

    @Test(description = "02. Создание децентрализованного задания (Рабочий стол)")
    @TestRailCaseId(testCaseId = 1823)
    @Description("1. Создаётся децентрализованное задание (основные данные жёстко прописаны)\n"
            + "2. Делается сверка с записью в Журнале заданий.")
    public void dashboardCreateDecentralizedTaskTest() {
        // data preparation
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        DecentralizedTask decentralizedTask =
                testDataGenerator.getDecentralizedTaskItemWithSomeHardcodedFieldValuesForTaskCreation();

        // test
        bw.mainMenu().gotoDashboard();
        bw.dashboard().managementCreateDecentralizedTask();

        DecentralizedTask taskSaved = bw.taskCreation().setAndSaveDecentralizedTask(decentralizedTask);
        bw.taskCreation().checkAndCloseMessageTaskCreatedSuccessfully();

        bw.mainMenu().gotoTasks_TaskJournal();
        DecentralizedTask taskFromJournal =
                bw.taskJournal().getTaskByComment(decentralizedTask.comment);
        checkTrue(taskFromJournal != null, "Найдена запись.", "Запись НЕ найдена.");

        // preparation before comparison
        taskSaved.withWaybillStatus(null).withNumber(taskFromJournal.number); // can't check WaybillStatus & number of task
        if (taskSaved.vehicle.toString().contains(taskFromJournal.vehicleRegNumber) && taskSaved.vehicle.toString().contains(taskFromJournal.vehicleType)) {
            // we can only compare Vehicle by vehicleType & vehicleRegNumber. And they are equal
            taskSaved.withVehicle(null).withVehicleType(taskFromJournal.vehicleType).withVehicleRegNumber(taskFromJournal.vehicleRegNumber);
        } else {
            // & not equal in this case
            taskFromJournal.withVehicle(new Special<>("[" + taskFromJournal.vehicleRegNumber + " [..." + taskFromJournal.vehicleType + "]]"));
        }
        // comparison
        DecentralizedTask.checkEquals(taskFromJournal, taskSaved);
    }


    @Test(description = "04. Создание наряд-задания (Рабочий стол)")
    @TestRailCaseId(testCaseId = 1825)
    @Description("1. Создаётся наряд-задание (основные данные жёстко прописаны)\n"
            + "2. Делается сверка с записью в Журнале наряд-заданий.")
    public void dashboardCreateWorkOrderTaskTest() {
        // data preparation
        WorkOrderTask workOrderTask =
                TestDataGenerator.getWorkOrderTaskWithSomeHardcodedFieldValuesForTaskCreation();

        // test
        bw.mainMenu().gotoDashboard();
        bw.dashboard().managementCreateWorkOrderTask();

        WorkOrderTask workOrderTaskToCreate =
                bw.taskCreation().setAndSaveWorkOrderTask(workOrderTask);

        bw.mainMenu().gotoTasks_WorkOrderTaskJournal();
        WorkOrderTask workOrderTaskFromJournal =
                bw.workOrderJournal().getWorkOrderByComment(workOrderTaskToCreate.comment);
        checkTrue(workOrderTaskFromJournal != null, "Найдена запись.", "Запись НЕ найдена.");

        // preparation before comparison
        // TODO: check of brigade successful saving
        logBroken("Не проверяется состав бригады (он не выводится в таблице / можно сделать проверку через повторное открытие наряд-задания)");
        workOrderTaskToCreate
                .withBrigade(null)
                .withBrigadier(new Special<>(StringHelper.fioToSurname_XX(workOrderTaskToCreate.brigadier.toString()))) // fio in table is of a type [Торокулов М.М.]
                .withNumber(workOrderTaskFromJournal.number); // we don't know number before creation
        // comparison
        WorkOrderTask.checkEquals(workOrderTaskFromJournal, workOrderTaskToCreate);
    }


    @Test(description = "06. Создание Путевого листа (ПЛ) (Рабочий стол) с созданием задания в ПЛ")
    @TestRailCaseId(testCaseId = 1827)
    @Description("1. Создаётся ПЛ\n"
            + "2. Делается сверка с записью в Журнале ПЛ.")
    public void dashboardCreateWaybillTest() {

        // data preparation
        Waybill waybill = TestDataGenerator.getWaybill();

        // test
        bw.mainMenu().gotoDashboard();
        bw.dashboard().managementCreateWaybillTask();

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
