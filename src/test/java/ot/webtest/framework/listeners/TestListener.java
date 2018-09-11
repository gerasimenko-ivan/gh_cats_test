package ot.webtest.framework.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import ot.webtest.framework.testrail.dataobject.TestRailStatus;
import ot.webtest.tests.TestBase;

import static ot.webtest.framework.helpers.AllureHelper.logFailScreenshot;
import static ot.webtest.framework.helpers.AllureHelper.logInfo;
import static ot.webtest.framework.testrail.TestRailHelper.cleanTestContextFromTestRailInfo;
import static ot.webtest.framework.testrail.TestRailHelper.parseResultsAndSendToTestRail;

public class TestListener extends TestBase implements ITestListener {
    @Override
    public void onTestStart(ITestResult iTestResult) {
        cleanTestContextFromTestRailInfo();
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        testStatus = TestRailStatus.PASSED;
        parseResultsAndSendToTestRail(iTestResult, TestRailStatus.PASSED);
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        testStatus = TestRailStatus.FAILED;
        logFailScreenshot("ON TEST FAILURE >> " + iTestResult.getThrowable().getMessage().replace("\n", " - "));
        parseResultsAndSendToTestRail(iTestResult, TestRailStatus.FAILED);
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        testStatus = TestRailStatus.BLOCKED;
        logInfo("onTestSkipped - возможно стоит добавить обработку этого метода");

        parseResultsAndSendToTestRail(iTestResult, TestRailStatus.BLOCKED);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        logInfo("onTestFailedButWithinSuccessPercentage - возможно стоит добавить обработку этого метода");
    }

    @Override
    public void onStart(ITestContext iTestContext) {

    }

    @Override
    public void onFinish(ITestContext iTestContext) {

    }

}
