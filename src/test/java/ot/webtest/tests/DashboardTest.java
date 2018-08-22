package ot.webtest.tests;

import io.qameta.allure.Description;
import org.testng.annotations.Test;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.helpers.StringHelper;
import ot.webtest.framework.kketshelpers.TestDataGenerator;
import ot.webtest.framework.kketshelpers.dataobjects.DecentralizedTask;
import ot.webtest.framework.kketshelpers.dataobjects.WorkOrderTask;
import ot.webtest.framework.testrail.TestRailCaseId;

import static ot.webtest.framework.helpers.AllureHelper.logBroken;
import static ot.webtest.framework.helpers.AllureHelper.logFailed;

public class DashboardTest extends TestBase {

    @Test(description = "Создание децентрализованного задания (Рабочий стол)")
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

        logFailed("Приходится прописывать жёсткий, а не случайный Маршрут, т.к. есть маршрут с дублями пробелов '1-2   ПМ   ПРАВИЛЬНЫЙ , Проезжая часть', а он валит Селенид");
        DecentralizedTask taskSaved = bw.taskCreation().setAndSaveDecentralizedTask(decentralizedTask);
        bw.taskCreation().checkAndCloseMessageTaskCreatedSuccessfully();

        bw.mainMenu().gotoTasks_TaskJournal();
        DecentralizedTask taskFromJournal =
                bw.taskJournal().getTaskByComment(decentralizedTask.comment);

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


    @Test(description = "Создание наряд-задания (Рабочий стол)")
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
}
