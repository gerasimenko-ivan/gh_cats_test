package ot.webtest.framework;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import ot.webtest.dataobject.AppUnderTest;
import ot.webtest.dataobject.User;
import ot.webtest.framework.kketshelpers.LoaderHelper;
import ot.webtest.framework.kketshelpers.LoginHelper;
import ot.webtest.framework.kketshelpers.MainMenuHelper;
import ot.webtest.framework.kketshelpers.dashboard.DashboardHelper;
import ot.webtest.framework.kketshelpers.dashboard.TaskCreationHelper;
import ot.webtest.framework.kketshelpers.nsi.FuelCalculationOperationHelper;
import ot.webtest.framework.kketshelpers.nsi.FuelConsumptionNormHelper;
import ot.webtest.framework.kketshelpers.taskjournal.TaskJournalHelper;
import ot.webtest.framework.kketshelpers.taskjournal.WorkOrderJournalHelper;
import ot.webtest.tests.TestBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;


public class BrowserManager {
    public static final int GLOBAL_TIMEOUT = 30000;  // implicit timeout
    public static final int EXCEPTION_IGNORE_TIMEOUT = 15000;

    private HelperBase helperBase;

    private LoginHelper loginHelper;
    private LoaderHelper loaderHelper;
    private MainMenuHelper mainMenuHelper;
    private DashboardHelper dashboardHelper;

    private TaskCreationHelper taskCreationHelper;

    private TaskJournalHelper taskJournalHelper;
    private WorkOrderJournalHelper workOrderJournalHelper;

    private FuelCalculationOperationHelper fuelCalculationOperationHelper;
    private FuelConsumptionNormHelper fuelConsumptionNormHelper;

    public void init(User user, AppUnderTest appUnderTest) {
        logPassed(" >> Инициализация (Запуск браузера. Логирование версии. Логин)");

        Configuration.timeout = GLOBAL_TIMEOUT;

        // Инициализация хелперов
        helperBase = new HelperBase();

        loginHelper = new LoginHelper();
        loaderHelper = new LoaderHelper();
        mainMenuHelper = new MainMenuHelper();
        dashboardHelper = new DashboardHelper();

        taskCreationHelper = new TaskCreationHelper();

        taskJournalHelper = new TaskJournalHelper();
        workOrderJournalHelper = new WorkOrderJournalHelper();

        fuelCalculationOperationHelper = new FuelCalculationOperationHelper();
        fuelConsumptionNormHelper = new FuelConsumptionNormHelper();


        if (user.popupLogin != null && !user.popupLogin.equals("") && user.popupPassword != null && !user.popupPassword.equals("")) {
            helperBase().openUrl("https://" + user.popupLogin + ":" + user.popupPassword + "@" + appUnderTest.url.replace("https://", ""));
        } else {
            helperBase().openUrl(appUnderTest.url);
        }

        loginHelper().login(user.login, user.password);
        loginHelper().checkSuccessfulLogin();
        loader().waitLoaderDisappear();

        String etsVersion = getEtsVersion();
        TestBase.setTestedSoftwareVersion("V." + etsVersion);
    }

    public void reinit(User user, AppUnderTest appUnderTest) {
        terminate();
        if (user.popupLogin != null && !user.popupLogin.equals("") && user.popupPassword != null && !user.popupPassword.equals("")) {
            helperBase().openUrl("https://" + user.popupLogin + ":" + user.popupPassword + "@" + appUnderTest.url.replace("https://", ""));
        } else {
            helperBase().openUrl(appUnderTest.url);
        }
        // TODO: подумать как убрать ожидание - оно необходимо, т.к. страница не сразу (а как скоро???) в релоад отправляется...
        sleepMillis(1000);
        if (!loginHelper().isLoggedIn()) {
            loginHelper().login(user.login, user.password);
            loginHelper().checkSuccessfulLogin();
        } else {
            logScreenshot("Пользователь уже залогинен.");
        }

    }

    @Step("Получаем версию ЕТС")
    public String getEtsVersion() {
        String buildNumberPattern;
        Pattern buildPattern = Pattern.compile(buildNumberPattern = "^\\d+\\.\\d+.\\d+.\\d+$");

        String etsVersion =
                helperBase().getText(By.xpath("//span[starts-with(text(), 'Версия ')]"),
                        "Строка с номером сборки").replace("Версия ", "");

        if (!buildPattern.matcher(etsVersion).find()) {
            logFailed("Версия не соответствует шаблону '" + buildNumberPattern + "'");
        }

        return etsVersion;
    }

    public void terminate() {
        logInfo("закрываем окно браузера, на котором сейчас находится фокус веб-драйвера...");
        closeWebDriver();
    }

    @Step("Киляем все chromedriver.exe...")
    public void killChromeDriver() {
        BufferedReader stdInput = null;
        String runCommand = "taskkill /IM chromedriver.exe /F";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(runCommand, null, new File(System.getProperty("user.dir")));
        } catch (IOException e) {
            logException(e);
        }
        stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            logException(e);
        }
        List<String> programOut = new ArrayList<String>();
        String s = null;
        System.out.println("Result for command 'taskkill /IM chromedriver.exe /F'");
        try {
            while ((s = stdInput.readLine()) != null) {
                programOut.add(s);
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        programOut.add("Hello, World!");
        logPassed(programOut.size() + "");
        logPassedMessageBlock("Result for command 'killprocess /IM chromedriver.exe /F'", programOut);
    }


    public HelperBase helperBase() {
        return helperBase;
    }

    public LoginHelper loginHelper() {
        return loginHelper;
    }

    /*****************************************************************************************************************
     * НАВИГАЦИЯ
     ****************************************************************************************************************/

    public MainMenuHelper mainMenu() {
        return mainMenuHelper;
    }

    public LoaderHelper loader() {
        return loaderHelper;
    }

    public DashboardHelper dashboard() {
        return dashboardHelper;
    }

    /*****************************************************************************************************************
     * РАБОЧИЙ СТОЛ
     ****************************************************************************************************************/

    public TaskCreationHelper taskCreation() {
        return taskCreationHelper;
    }

    /*****************************************************************************************************************
     * МЕНЮ: ЗАДАНИЯ
     ****************************************************************************************************************/

    public TaskJournalHelper taskJournal() {
        return taskJournalHelper;
    }

    public WorkOrderJournalHelper workOrderJournal() {
        return workOrderJournalHelper;
    }

    /*****************************************************************************************************************
     * МЕНЮ: НСИ
     ****************************************************************************************************************/
    public FuelCalculationOperationHelper fuelCalculationOperation() {
        return fuelCalculationOperationHelper;
    }

    public FuelConsumptionNormHelper fuelConsumptionNorm() {
        return fuelConsumptionNormHelper;
    }
}
