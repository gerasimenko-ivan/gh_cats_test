package ot.webtest.framework.listeners;

import org.testng.IConfigurationListener;
import org.testng.ITestResult;
import ot.webtest.framework.testrail.dataobject.TestRailStatus;
import ot.webtest.tests.TestBase;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static ot.webtest.framework.testrail.TestRailHelper.parseResultsAndSendToTestRail;

public class ConfigurationListener extends TestBase implements IConfigurationListener {

    @Override
    public void onConfigurationSuccess(ITestResult iTestResult) {
        parseResultsAndSendToTestRail(iTestResult, TestRailStatus.PASSED);
    }

    @Override
    public void onConfigurationFailure(ITestResult iTestResult) {
        parseResultsAndSendToTestRail(iTestResult, TestRailStatus.FAILED);

        // логирование для аллюр из ConfigurationListener не попадает в отчёт, поэтому его здесь и нет
        // метод выполняется в случае падения в методе с аннотацией @BeforeSuite
        // а так как в этом случае нет перехода к методу с аннотацией @AfterSuite
        // то закрываем браузер здесь
        closeWebDriver();
    }

    @Override
    public void onConfigurationSkip(ITestResult iTestResult) {
        parseResultsAndSendToTestRail(iTestResult, TestRailStatus.BLOCKED);
    }
}
