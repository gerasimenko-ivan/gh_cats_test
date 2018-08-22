package ot.webtest.framework.testrail.dataobject;

public class TestRailResults {
    public String testRunId;
    public String testCaseId;
    public TestRailStatus statusId;
    public String comment;
    public String version;
    public String elapsed;
    public String defects;
    public String customStepResults;

    public TestRailResults withTestRunId(String testRunId) {
        this.testRunId = testRunId;
        return this;
    }

    public TestRailResults withTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
        return this;
    }

    public TestRailResults withStatusId(TestRailStatus statusId) {
        this.statusId = statusId;
        return this;
    }

    public TestRailResults withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public TestRailResults withVersion(String version) {
        this.version = version;
        return this;
    }

    public TestRailResults withElapsed(String elapsed) {
        this.elapsed = elapsed;
        return this;
    }

    /** A comma-separated list of defects to link to the test result
     * @param defects
     * @return
     */
    public TestRailResults withDefects(String defects) {
        this.defects = defects;
        return this;
    }

    public TestRailResults withCustomStepResults(String customStepResults) {
        this.customStepResults = customStepResults;
        return this;
    }
}
