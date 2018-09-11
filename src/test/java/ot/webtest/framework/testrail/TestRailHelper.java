package ot.webtest.framework.testrail;

import io.qameta.allure.Step;
import io.qameta.allure.model.StepResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestResult;
import ot.webtest.framework.testrail.dataobject.*;
import ot.webtest.tests.TestBase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillisSilent;

public class TestRailHelper {

    static String allureStepMark = "ALLURE_STEP_MARK_";
    static String defectIdMark = "DEFECT_ID_MARK_";
    static TestRailCredentials credentials;

    public static void setCredentials(TestRailCredentials credentials) {
        TestRailHelper.credentials = credentials;
    }

    public static void setProxy(String proxyHost, Integer proxyPort) {
        credentials
                .withProxyHost(proxyHost)
                .withProxyPort(proxyPort);
    }

    private static void sendResults(TestRailResults testRailResults, TestRailCredentials credentials) throws IOException, APIException {

        APIClient client =
                new APIClient(
                        credentials.railsEngineUrl,
                        credentials.username,
                        credentials.password,
                        credentials.proxyHost,
                        credentials.proxyPort);

        Map data = new HashMap();
        data.put("status_id", Integer.toString(testRailResults.statusId.toInt()));
        data.put("comment", testRailResults.comment);
        if (testRailResults.version != null && !testRailResults.version.isEmpty()) {
            data.put("version", testRailResults.version);
        }
        if (testRailResults.elapsed != null && !testRailResults.elapsed.isEmpty()) {
            data.put("elapsed", testRailResults.elapsed);
        }
        if (testRailResults.defects != null && !testRailResults.defects.isEmpty()) {
            data.put("defects", testRailResults.defects);
        }
        // data.put("assignedto_id", assignedto_id);    // возможно назначение дефекта на тестировщика
        if (testRailResults.customStepResults != null && !testRailResults.customStepResults.isEmpty()) {
            data.put("custom_step_results", testRailResults.customStepResults);
        }

        client.sendPost("add_result_for_case/" + testRailResults.testRunId + "/" + testRailResults.testCaseId, data );
    }

    public static void parseResultsAndSendToTestRail(ITestResult testResult, TestRailStatus testRailStatus) {
        String testRunId = TestBase.getTestRunId();
        String testCaseId = getTestCaseIdFromTestResult(testResult);

        if (testRunId == null || testCaseId == null) {
            logPassed("/!\\ ДАННЫЕ для метода '" + testResult.getName() + "' НЕ ОТПРАВЛЕНЫ В TestRail. Не заданы testRunId и(или) testCaseId /!\\");
            return;
        }

        //////////////////////////// Getting allure steps part ////////////////////////////
        Set<String> attributeNames = testResult.getTestContext().getAttributeNames();
        Map<Long, String> allureStepsMap_Key_Value = getMap_KeyVSAllureStep(testResult, attributeNames);

        ArrayList<Long> allureSteps_allKeys = new ArrayList<>(allureStepsMap_Key_Value.keySet());
        ArrayList<String> orderedListOfSteps = getOrderedListOfAllureTopLevelSteps(allureStepsMap_Key_Value, allureSteps_allKeys);

        String commentSteps = getAllureStepsAsTestRailTable(orderedListOfSteps);

        // remove all allure steps from test context
        removeAllAllureStepsFromTestContext(testResult);

        //////////////////////////// Getting defect part ////////////////////////////
        attributeNames = testResult.getTestContext().getAttributeNames();
        String defectId = null;
        for (String attrName : attributeNames) {
            if (attrName != null && attrName.contains(defectIdMark)) {
                defectId = testResult.getTestContext().getAttribute(attrName).toString();
                testResult.getTestContext().removeAttribute(attrName);
                break;
            }
        }

        // TODO: TestRailHelper.addTestCaseIdToTestRun(TestBase.getTestRunIdAndSetMilestone(), testCaseId)
        // adds case id if it is not in test run

        //////////////////////////// Send results ////////////////////////////
        try {
            TestRailHelper.sendResults(
                    new TestRailResults()
                            .withTestRunId(TestBase.getTestRunId())
                            .withTestCaseId(testCaseId)
                            .withStatusId(testRailStatus)
                            .withVersion(TestBase.getTestedSoftwareVersion())
                            .withDefects(defectId)
                            .withComment("Тест выполнен с машины <" + getMachineName() + ">. Статус проставлен автотестом." + commentSteps)
                    , credentials);
        } catch (IOException e) {
            logException(e);
            e.printStackTrace();
        } catch (APIException e) {
            logException(e);
            e.printStackTrace();
        }
    }

    public static void removeAllAllureStepsFromTestContext(ITestResult testResult) {
        Set<String> attributeNames = testResult.getTestContext().getAttributeNames();
        Map<Long, String> allureStepsMap_Key_Value = getMap_KeyVSAllureStep(testResult, attributeNames);

        ArrayList<Long> allureSteps_allKeys = new ArrayList<>(allureStepsMap_Key_Value.keySet());
        for (Long longKey : allureSteps_allKeys) {
            testResult.getTestContext().removeAttribute(allureStepMark + longKey);
        }
    }

    private static String getAllureStepsAsTestRailTable(ArrayList<String> orderedListOfSteps) {
        int j = 1;
        String commentSteps = "\n|||:Шаг|:Действие|:Статус|:Завершён";
        for (String singleStep : orderedListOfSteps) {
            String stepInfo_Action_Status_EndTime = singleStep.replace("\"", "'").replace("\\", ")");  // '\' is prohibited in JSON
            commentSteps += "\n||" + j + stepInfo_Action_Status_EndTime;
            ++j;
        }
        return commentSteps;
    }

    private static ArrayList<String> getOrderedListOfAllureTopLevelSteps(Map<Long, String> allureStepsMap_Key_Value, ArrayList<Long> allureSteps_allKeys) {
        Collections.sort(allureSteps_allKeys);
        ArrayList<String> orderedListOfSteps = new ArrayList<>();
        Pattern patternOfStepsMark = Pattern.compile(stepsMarkPattern());
        for (Long key : allureSteps_allKeys) {
            // get count of steps inside current step
            Matcher stepValueMatcher = patternOfStepsMark.matcher(allureStepsMap_Key_Value.get(key));
            int stepsCount = 0;
            if (stepValueMatcher.find()) {
                stepsCount = Integer.parseInt(stepValueMatcher.group(0).replace(stepsMarkString(), ""));
            }
            // delete all substeps from ordered list
            for (int k = 0; k < stepsCount; k++) {
                if (orderedListOfSteps.size() > 0) {
                    orderedListOfSteps.remove(orderedListOfSteps.size() - 1);
                }
            }
            // add current step to ordered list without 'substeps count'
            orderedListOfSteps.add(stepValueMatcher.replaceAll(""));
        }

        return orderedListOfSteps;
    }

    private static Map<Long, String> getMap_KeyVSAllureStep(ITestResult testResult, Set<String> attributeNames) {
        Map<Long, String> allureSteps_Key_Value = new HashMap<Long, String>(){};
        for (String name : attributeNames) {
            if (name != null && name.contains(allureStepMark)) {
                allureSteps_Key_Value.put(Long.parseLong(name.replace(allureStepMark, "")), testResult.getTestContext().getAttribute(name).toString());
            }
        }
        return allureSteps_Key_Value;
    }


    public static void addStepResultToTestContext(StepResult result) {
        Object attribute = TestBase.testContext.getAttribute(allureStepMark + System.currentTimeMillis());
        if(attribute != null) {
            sleepMillisSilent(1);   // some actions happen in the same millisecond, we need to separate them
        }
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String resultStatus = result.getStatus().name();
        String stepInfo = "|" + result.getName()
                + "|" + (resultStatus.equals("PASSED") ? "V" : resultStatus)
                + "|" + dateFormat.format(date)
                + stepsMarkString() + (result.getSteps() != null ? result.getSteps().size() : 0);
        TestBase.testContext.setAttribute(allureStepMark + System.currentTimeMillis(), stepInfo);
    }

    private static String stepsMarkString() { return " >> steps: "; }
    private static String stepsMarkPattern() { return "\\s>>\\ssteps: (\\d)+"; }

    private static String getTestCaseIdFromTestResult(ITestResult testResult) {
        String testCaseId = null;
        Method testMethod = testResult.getMethod().getConstructorOrMethod().getMethod();
        if (testMethod.isAnnotationPresent(TestRailCaseId.class))
        {
            TestRailCaseId useAsTestName = testMethod.getAnnotation(TestRailCaseId.class);
            testCaseId = Integer.toString(useAsTestName.testCaseId());
        }
        return testCaseId;
    }

    public static void addDefectToTestContext(String defect) {
        TestBase.testContext.setAttribute(defectIdMark + System.currentTimeMillis(), defect);
    }

    public static void cleanTestContextFromTestRailInfo() {
        ArrayList<String> allKeysWithMarks = new ArrayList<>();
        for (String attrName : TestBase.testContext.getAttributeNames()) {
            if (attrName != null && (attrName.contains(allureStepMark) || attrName.contains(defectIdMark))) {
                allKeysWithMarks.add(attrName);
            }
        }

        for (String attrName : allKeysWithMarks) {
            TestBase.testContext.removeAttribute(attrName);
        }
    }

    @Step("Получаем ИД тест-рана для текущих версий (Создаем майлстоун/тест-ран, если их нет).")
    public static String getTestRunIdAndSetMilestone(String projectId, String milestoneName) {
        String milestoneId = null;
        String testRunName = milestoneName + " автотесты";

        logPassed("Поиск тест-рана с именем '" + testRunName + "'...");
        JSONArray arrayOfTestRuns = getAllTestRuns(projectId, false);
        for (Object testRunObj : arrayOfTestRuns) {
            JSONObject testRun = (JSONObject)testRunObj;
            if (testRun.get("name").equals(testRunName)) {
                String testRunId = testRun.get("id").toString();
                logPassed("Тест-ран найден с ID = " + testRunId);
                return testRunId;
            }
        }

        logPassed("Поиск майлстоуна с именем '" + milestoneName + "'...");
        JSONArray arrayOfMilestones = getAllMilestones(projectId, false);
        for (Object milestoneObj : arrayOfMilestones) {
            JSONObject milestone =  (JSONObject)milestoneObj;
            if (milestone.get("name").equals(milestoneName)) {
                milestoneId = milestone.get("id").toString();
                logPassed("Майлстоун найден с ID = " + milestoneId);
                break;
            }
        }

        if (milestoneId == null) {
            milestoneId = addMilestone(projectId, milestoneName);
        }

        //updateMilestone(milestoneId, true, null, null); // нет смысла стартовать майлстоун - ЭТО ЗАТРЁТ ДАТУ СТАРТА(((

        TestRailRun testRailRun =
                new TestRailRun()
                        .withName(testRunName)
                        .withMilestoneId(milestoneId)
                        .withTestCaseIds(TestBase.getTestCaseIds(TestBase.class));
        String testRunId = addTestRun(projectId, testRailRun);
        return testRunId;
    }

    private static JSONArray getAllMilestones(String projectId, Boolean isCompleted) { // - index.php?/api/v2/get_milestones/11&is_completed=0
        APIClient client =
                new APIClient(
                        credentials.railsEngineUrl,
                        credentials.username,
                        credentials.password,
                        credentials.proxyHost,
                        credentials.proxyPort);

        String isCompletedParam = "";
        if (isCompleted != null) {
            isCompletedParam = "&is_completed=" + (isCompleted ? "1" : "0");
        }

        JSONArray milestones = null;
        try {
            milestones = (JSONArray)client.sendGet("get_milestones/" + projectId + isCompletedParam);
        } catch (IOException e) {
            logException(e);
            e.printStackTrace();
        } catch (APIException e) {
            logException(e);
            e.printStackTrace();
        }

        return milestones;
    }

    @Step("Создаём майлстоун '{milestoneName}' (с сегодняшней датой начала) в проекте с ID={projectId}...")
    private static String addMilestone(String projectId, String milestoneName) {
        APIClient client =
                new APIClient(
                        credentials.railsEngineUrl,
                        credentials.username,
                        credentials.password,
                        credentials.proxyHost,
                        credentials.proxyPort);

        Map data = new HashMap();
        data.put("name", milestoneName);
        data.put("start_on", String.valueOf(System.currentTimeMillis() / 1000L));

        JSONObject newMilestone = null;
        try {
            newMilestone = (JSONObject) client.sendPost("add_milestone/" + projectId, data);
        } catch (IOException e) {
            logException(e);
            e.printStackTrace();
        } catch (APIException e) {
            logException(e);
            e.printStackTrace();
        }
        if (newMilestone != null) {
            String id = newMilestone.get("id").toString();
            logPassed("Майлстоун успешно создан с ID = " + id);
            return id;
        } else {
            logFailed("Майлстоун НЕ создан.");
            Assert.fail("Майлстоун НЕ создан.");
            return "";
        }
    }

    @Step("Обновляем майлстоун {milestone}")
    private static JSONObject updateMilestone(Milestone milestone) {
        if (milestone.id == null)
            throw new NullPointerException("milestone.id is null; milestone can not be updated");
        APIClient client =
                new APIClient(
                        credentials.railsEngineUrl,
                        credentials.username,
                        credentials.password,
                        credentials.proxyHost,
                        credentials.proxyPort);

        Map data = new HashMap();
        if (milestone.isStarted != null)
            data.put("is_started", milestone.isStarted ? "true" : "false");
        if (milestone.isCompleted != null)
            data.put("is_completed", milestone.isCompleted ? "true" : "false");
        if (milestone.parentId != null)
            data.put("parent_id", String.valueOf(milestone.parentId));
        if (milestone.dueOn != null)
            data.put("due_on", String.valueOf(milestone.dueOn));

        JSONObject updatedMilestone = null;
        try {
            updatedMilestone = (JSONObject) client.sendPost("update_milestone/" + milestone.id, data);
        } catch (IOException e) {
            logException(e);
            e.printStackTrace();
        } catch (APIException e) {
            logException(e);
            e.printStackTrace();
        }

        if (updatedMilestone != null) {
            logPassed("Майлстоун успешно обновлён");
            return updatedMilestone;
        } else {
            logFailed("Майлстоун НЕ создан.");
            Assert.fail("Майлстоун НЕ создан.");
            return updatedMilestone;
        }
    }

    @Step("Завершаем майлстоуны с DUE-датой более 4-х недель")
    public static void completeMilestonesDueDateFourWeeksAgo(String projectId) {
        JSONArray allMilestones = getAllMilestones(projectId, false);
        for (Object milestoneObj : allMilestones) {
            JSONObject milestoneJSON =  (JSONObject)milestoneObj;
            if (milestoneJSON.has("due_on") && !milestoneJSON.get("due_on").toString().equals("null") &&
                    Long.valueOf(milestoneJSON.get("due_on").toString()) < (System.currentTimeMillis() / 1000L) - 60*60*24*28) {  // older than 28 day
                Integer milestoneId = Integer.valueOf(milestoneJSON.get("id").toString());
                Milestone milestone =
                        new Milestone()
                                .withId(milestoneId)
                                .withIsCompleted(true);
                JSONObject jsonObject = updateMilestone(milestone);
                if (jsonObject != null && milestoneJSON.has("name")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.valueOf(milestoneJSON.get("due_on").toString())*1000L);
                    int mYear = calendar.get(Calendar.YEAR);
                    int mMonth = calendar.get(Calendar.MONTH);
                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    logPassed("Успешно обновлён майлстоун '" + milestoneJSON.get("name").toString() + "' due_on = " + mDay + "." + mMonth + "." + mYear);
                }
            }
        }
    }

    @Step("Завершаем майлстоуны с датой старта более 4-х недель")
    public static void completeMilestonesStartedFourWeeksAgo(String projectId) {
        JSONArray allMilestones = getAllMilestones(projectId, false);
        for (Object milestoneObj : allMilestones) {
            JSONObject milestoneJSON =  (JSONObject)milestoneObj;
            if (milestoneJSON.has("start_on") && !milestoneJSON.get("start_on").toString().equals("null") &&
                    Long.valueOf(milestoneJSON.get("start_on").toString()) < (System.currentTimeMillis() / 1000L) - 60*60*24*28) {  // older than 28 day
                Integer milestoneId = Integer.valueOf(milestoneJSON.get("id").toString());
                Milestone milestone =
                        new Milestone()
                                .withId(milestoneId)
                                .withIsCompleted(true)
                                .withDueOn(System.currentTimeMillis() / 1000L);
                JSONObject jsonObject = updateMilestone(milestone);
                if (jsonObject != null && milestoneJSON.has("name")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.valueOf(milestoneJSON.get("start_on").toString())*1000L);
                    int mYear = calendar.get(Calendar.YEAR);
                    int mMonth = calendar.get(Calendar.MONTH);
                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    logPassed("Успешно обновлён майлстоун '" + milestoneJSON.get("name").toString() + "' start_on = " + mDay + "." + mMonth + "." + mYear);
                }
            }
        }
    }

    @Step("Устанавливаем родительские майлстоуны 'Sprint|Спринт' (для майлстоунов с due_on добавляем майлстоуны со start_on, если start_on укладывается в двухнедельный период до due_on)")
    public static void setParentMilestones(String projectId) {
        JSONArray allMilestones = getAllMilestones(projectId, false);
        List<long[]> parentMilestones = new ArrayList<>();
        List<long[]> childMilestones = new ArrayList<>();
        for (Object milestoneObj : allMilestones) {
            JSONObject milestone = (JSONObject) milestoneObj;
            if (milestone.has("due_on") && !milestone.get("due_on").toString().equals("null") &&
                    milestone.has("name") && !milestone.get("name").toString().equals("null") &&
                    (milestone.get("name").toString().toLowerCase().contains("sprint") || milestone.get("name").toString().toLowerCase().contains("спринт"))) {
                parentMilestones.add(new long[]{
                        Long.valueOf(milestone.get("id").toString()),
                        Long.valueOf(milestone.get("due_on").toString()) - 60*60*24*13, // predicted milestone start date (2 weeks)
                        Long.valueOf(milestone.get("due_on").toString())
                });
                continue;
            }
            if (milestone.has("start_on") && !milestone.get("start_on").toString().equals("null")) {
                childMilestones.add(new long[]{
                        Long.valueOf(milestone.get("id").toString()),
                        Long.valueOf(milestone.get("start_on").toString())
                });
            }
        }
        for (long[] childMilestone : childMilestones) {
            for (long[] parentMilestone : parentMilestones) {
                if (parentMilestone[1] < childMilestone[1] && (childMilestone[1]/(60*60*24)) <= (parentMilestone[2]/(60*60*24))) {
                    Milestone milestone =
                            new Milestone()
                                    .withId(Math.toIntExact(childMilestone[0]))
                                    .withParentId(Math.toIntExact(parentMilestone[0]));
                    updateMilestone(milestone);
                    break;
                }
            }
        }
    }

    private static JSONArray getAllTestRuns(String projectId, Boolean isCompleted) {
        APIClient client =
                new APIClient(
                        credentials.railsEngineUrl,
                        credentials.username,
                        credentials.password,
                        credentials.proxyHost,
                        credentials.proxyPort);

        String isCompletedParam = "";
        if (isCompleted != null) {
            isCompletedParam = "&is_completed=" + (isCompleted ? "1" : "0");
        }

        JSONArray testRuns = null;
        try {
            testRuns = (JSONArray)client.sendGet("get_runs/" + projectId + isCompletedParam);
        } catch (IOException e) {
            logException(e);
            e.printStackTrace();
        } catch (APIException e) {
            logException(e);
            e.printStackTrace();
        }

        return testRuns;
    }


    @Step("Создаём тест-ран '{1}' в проекте с ID={0}...")
    private static String addTestRun(String projectId, TestRailRun testRailRun) {
        APIClient client =
                new APIClient(
                        credentials.railsEngineUrl,
                        credentials.username,
                        credentials.password,
                        credentials.proxyHost,
                        credentials.proxyPort);

        Map data = new HashMap();
        data.put("name", testRailRun.name);
        data.put("milestone_id", testRailRun.milestoneId);
        data.put("include_all", "false");
        data.put("case_ids", testRailRun.getCaseIdsForRequest());
        JSONObject newTestRun = null;
        try {
            newTestRun = (JSONObject) client.sendPost("add_run/" + projectId, data);
        } catch (IOException e) {
            logException(e);
            e.printStackTrace();
        } catch (APIException e) {
            logException(e);
            e.printStackTrace();
        }
        if (newTestRun != null) {
            String id = newTestRun.get("id").toString();
            logPassed("Тест-ран успешно создан с ID = " + id);
            return id;
        } else {
            logFailed("Тест-ран НЕ создан.");
            Assert.fail("Тест-ран НЕ создан.");
            return "";
        }
    }

    public static String getMachineName() {
        String hostname = "Unknown";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex)
        {
            logException(ex);
            ex.printStackTrace();
        }

        return hostname;
    }
}
