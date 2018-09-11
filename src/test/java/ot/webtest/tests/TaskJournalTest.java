package ot.webtest.tests;

import io.qameta.allure.Description;
import org.testng.annotations.Test;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.helpers.StringHelper;
import ot.webtest.framework.kketshelpers.TestDataGenerator;
import ot.webtest.framework.kketshelpers.dataobjects.DecentralizedTask;
import ot.webtest.framework.kketshelpers.dataobjects.WorkOrderTask;
import ot.webtest.framework.testrail.TestRailCaseId;

import java.time.LocalDate;

import static ot.webtest.framework.helpers.AllureHelper.logBroken;
import static ot.webtest.framework.helpers.AllureHelper.logFailed;
import static ot.webtest.framework.helpers.AssertHelper.checkTrue;

public class TaskJournalTest extends TestBase {

    @Test(description = "01. Создание децентрализованного задания (Журнал заданий)")
    @TestRailCaseId(testCaseId = 1822)
    @Description("1. Создаётся децентрализованное задание (основные данные жёстко прописаны)\n"
            + "2. Делается сверка с записью в Журнале заданий.")
    public void taskJournalCreateDecentralizedTaskTest() {
        // data preparation
        TestDataGenerator testDataGenerator = new TestDataGenerator();
        DecentralizedTask decentralizedTask =
                testDataGenerator.getDecentralizedTaskItemWithSomeHardcodedFieldValuesForTaskCreation();

        // test
        bw.mainMenu().gotoTasks_TaskJournal();

        bw.taskJournal().pressCreateDecentralizedTask();
        DecentralizedTask taskSaved = bw.taskCreation().setAndSaveDecentralizedTask(decentralizedTask);
        bw.taskCreation().checkAndCloseMessageTaskCreatedSuccessfully();

        DecentralizedTask taskFromJournal =
                bw.taskJournal().getTaskByComment(decentralizedTask.comment);
        checkTrue(taskFromJournal != null, "Найдена запись по комментарию.", "НЕ Найдена запись по комментарию.");

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


    @Test(description = "03. Создание наряд-задания (Журнал заданий)")
    @TestRailCaseId(testCaseId = 1824)
    @Description("1. Создаётся наряд-задание (основные данные жёстко прописаны)\n"
            + "2. Делается сверка с записью в Журнале наряд-заданий.")
    public void taskJournalCreateWorkOrderTaskTest() {
        // data preparation
        WorkOrderTask workOrderTask =
                TestDataGenerator.getWorkOrderTaskWithSomeHardcodedFieldValuesForTaskCreation();

        // test
        bw.mainMenu().gotoTasks_WorkOrderTaskJournal();
        bw.workOrderJournal().pressCreateWorkOrder();

        WorkOrderTask workOrderTaskToCreate =
                bw.taskCreation().setAndSaveWorkOrderTask(workOrderTask);

        WorkOrderTask workOrderTaskFromJournal =
                bw.workOrderJournal().getWorkOrderByComment(workOrderTaskToCreate.comment);
        checkTrue(workOrderTaskFromJournal != null, "Найдена запись по комментарию.", "НЕ Найдена запись по комментарию.");

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
