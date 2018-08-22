package ot.webtest.framework.helpers;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

import static ot.webtest.framework.testrail.TestRailHelper.addStepResultToTestContext;

public class AllureListener implements StepLifecycleListener {

    @Override
    public void beforeStepStart(StepResult result) {
    }

    @Override
    public void afterStepStart(StepResult result) {
    }

    @Override
    public void beforeStepUpdate(StepResult result) {
    }

    @Override
    public void afterStepUpdate(StepResult result) {
    }

    @Override
    public void beforeStepStop(StepResult result) {
    }

    @Override
    public void afterStepStop(StepResult result) {
        addStepResultToTestContext(result);
    }

}
