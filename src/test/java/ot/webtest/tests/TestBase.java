package ot.webtest.tests;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.Description;
import org.reflections.Reflections;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import ot.webtest.dataobject.AppUnderTest;
import ot.webtest.dataobject.User;
import ot.webtest.framework.BrowserManager;
import ot.webtest.framework.helpers.JsonDataReader;
import ot.webtest.framework.listeners.ConfigurationListener;
import ot.webtest.framework.listeners.TestListener;
import ot.webtest.framework.testrail.TestRailCaseId;
import ot.webtest.framework.testrail.TestRailHelper;
import ot.webtest.framework.testrail.dataobject.TestRailCredentials;
import ot.webtest.framework.testrail.dataobject.TestRailStatus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.AllureHelper.logBoldDelimeter;
import static ot.webtest.framework.helpers.AllureHelper.logFailed;


@Listeners({TestListener.class, ConfigurationListener.class})
public class TestBase {

    protected static final BrowserManager bw = new BrowserManager();

    private static String testrailProjectId = "20";
    private static String testRunId;
    private static String testedSoftwareVersion;
    public static ITestContext testContext;
    protected static TestRailStatus testStatus;

    protected static User user;
    protected static AppUnderTest appUnderTest;

    public static void setTestedSoftwareVersion(String testedSoftwareVersion) {
        TestBase.testedSoftwareVersion = testedSoftwareVersion;
    }

    public static String getTestedSoftwareVersion() {
        return testedSoftwareVersion;
    }

    public static String getTestRunId() {
        return testRunId;
    }

    @Parameters({"testdatafile"})
    @BeforeSuite
    @TestRailCaseId(testCaseId = 1753)
    public void initBrowser(ITestContext testContext, @Optional String testdatafile) {

        JsonDataReader connectionSettingsReader = new JsonDataReader();
        String machineName = TestRailHelper.getMachineName();
        if (machineName.equals("qtp3") || machineName.equals("WIN-AT9EB98UPOU")) {
            System.out.println("Working from machine <" + machineName + "> using proxy");
            connectionSettingsReader.getJsonObjectFromFile(System.getProperty("user.dir") + "\\test-data\\connection-settings.json");
        } else {
            System.out.println("Working from machine <" + machineName + "> NO proxy");
            connectionSettingsReader.getJsonObjectFromFile(System.getProperty("user.dir") + "\\test-data\\connection-settings-no-proxy.json");
        }

        /* These settings does not implement to frameworks used (Selenide & Testrail)
        System.setProperty("http.proxyHost", "http://some.proxy.addr");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("http.proxySet", "true");*/

        TestRailHelper.setCredentials(
                new TestRailCredentials()
                        .withUsername(connectionSettingsReader.getValueByKey("test-rail-username"))
                        .withPassword(connectionSettingsReader.getValueByKey("test-rail-password"))
                        .withRailsEngineUrl(connectionSettingsReader.getValueByKey("test-rail-url")));
        TestBase.testContext = testContext;

        Configuration.browser = "chrome";

        String proxyHost = connectionSettingsReader.getValueByKey("proxy-host");
        if (proxyHost != null && !proxyHost.equals("")) {
            String proxyPort = connectionSettingsReader.getValueByKey("proxy-port");

            // set proxy for Selenide
            System.setProperty("wdm.proxy", "http://" + proxyHost + ":" + proxyPort);

            // set proxy for TestRail
            TestRailHelper.setProxy(proxyHost, Integer.parseInt(proxyPort));
        }


        JsonDataReader testDataReader = new JsonDataReader();
        if (testdatafile != null && !testdatafile.equals("")) {
            testDataReader.getJsonObjectFromFile(System.getProperty("user.dir") + "\\test-data\\" + testdatafile);
        } else {
            // DEBUGGING MODE // DEBUGGING MODE // DEBUGGING MODE // DEBUGGING MODE // DEBUGGING MODE // DEBUGGING MODE //
            logFailed("PROGRAMM IS RUNNING IN DEBUGGING MODE!!!");
            testDataReader.getJsonObjectFromFile(System.getProperty("user.dir") + "\\test-data\\test2-env.json");
            // DEBUGGING MODE // DEBUGGING MODE // DEBUGGING MODE // DEBUGGING MODE // DEBUGGING MODE // DEBUGGING MODE //
        }


        user = new User()
                .withLogin(testDataReader.getValueByKey("login"))
                .withPassword(testDataReader.getValueByKey("password"))
                .withPopupLogin(testDataReader.getValueByKey("popup-login"))
                .withPopupPassword(testDataReader.getValueByKey("popup-password"))
                .withSurnameNM(testDataReader.getValueByKey("userSurnameNM"));

        appUnderTest = new AppUnderTest()
                .withUrl(testDataReader.getValueByKey("ets-url"));

        bw.init(user, appUnderTest);

        if (getTestedSoftwareVersion() != null) {
            TestBase.testRunId = TestRailHelper.getTestRunIdAndSetMilestone(testrailProjectId, getTestedSoftwareVersion());
        }
    }

    @AfterSuite
    public void terminateBrowser() {
        bw.terminate();
        // this part is commented because we does not have sprints & build number is unpredictable and does not mean anything
        //TestRailHelper.setParentMilestones(testrailProjectId);
        TestRailHelper.completeMilestonesDueDateFourWeeksAgo(testrailProjectId);
        TestRailHelper.completeMilestonesStartedFourWeeksAgo(testrailProjectId);
        //TestRailHelper.closeTestRunsOlderThanFourWeeks(testrailProjectId);
        bw.killChromeDriver();
    }

    @Test(description = "Замечания по автотестам")
    @Description("Замечания по автотестам")
    @TestRailCaseId(testCaseId = 1969)
    public void testsInfo() {
        logBoldDelimeter();
        logPassed(" >> ТЕСТ: 07. Создание карточки сотрудника (водитель/машинист) - NsiTest.createDriverOrMachinistEmployeeCardTest()");
        logPassed(" >> ТЕСТ: 08. Создание карточки сотрудника (НЕ водитель/машинист) - NsiTest.createNotDriverOrMachinistEmployeeCardTest()");
        logFailed("BUG: Поле 'Табельный номер' не сохраняет лидирующие нули!");

        Assert.fail("В тестах присутствуют отклонения от желаемого варианта проверок.");
    }

    @BeforeMethod
    @BeforeClass
    public void reinitAppOnFailure() {
        if (testStatus == TestRailStatus.FAILED) {
            logBroken("/!\\ Перезапуск браузера после падения теста /!\\");
            bw.reinit(user, appUnderTest);
            testStatus = TestRailStatus.PASSED;
        } else {
            logPassed("Предыдущий тест не упал. Перезапуск приложения не нужен");
        }
    }

    /**
     * Получаем все значения testCaseId у методов класса TestBase и всех его прямых наследников
     *
     * @param classOfTestBase - TestBase.class
     * @return - список всех testCaseId в проекте
     * @author Gerasimenko I.S.
     */
    public static List<String> getTestCaseIds(Class<TestBase> classOfTestBase) {
        List<String> testCaseIds;

        // Annotations of TestBase
        testCaseIds = new ArrayList<>(getTestRailCaseIdsOfClass(classOfTestBase));

        // Annotations of TestBase child classes
        Set<Class<? extends TestBase>> subclassesOfTestBase =
                new Reflections("ot.webtest.tests", classOfTestBase).getSubTypesOf(TestBase.class);
        for (Object testBaseSubclass : subclassesOfTestBase.toArray()) {
            testCaseIds.addAll(getTestRailCaseIdsOfClass((Class<TestBase>) testBaseSubclass));
        }
        return testCaseIds;
    }

    private static List<String> getTestRailCaseIdsOfClass(Class<TestBase> classOfTestBase) {
        List<String> testCaseIds = new ArrayList<>();
        for (Method method : classOfTestBase.getDeclaredMethods()) {
            if (method.isAnnotationPresent(TestRailCaseId.class)) {
                TestRailCaseId annotation = method.getAnnotation(TestRailCaseId.class);
                testCaseIds.add(String.valueOf(annotation.testCaseId()));
            }
        }
        return testCaseIds;
    }
}
