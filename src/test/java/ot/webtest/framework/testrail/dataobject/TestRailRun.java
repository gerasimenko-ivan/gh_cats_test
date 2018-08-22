package ot.webtest.framework.testrail.dataobject;

import java.util.List;

public class TestRailRun {
    public String id;
    public String name;
    public String milestoneId;
    public List<String> caseIds;

    public TestRailRun withId(String id) {
        this.id = id;
        return this;
    }

    public TestRailRun withName(String name) {
        this.name = name;
        return this;
    }

    public TestRailRun withMilestoneId(String milestoneId) {
        this.milestoneId = milestoneId;
        return this;
    }

    public TestRailRun withTestCaseIds(List<String> testCaseIds) {
        this.caseIds = testCaseIds;
        return this;
    }

    public String getCaseIdsForRequest() {
        return  "[" + String.join(", ", caseIds) + "]";
    }

    @Override
    public String toString() {
        return "{id: " + (this.id == null ? "null" : this.id) + "; name: " + (this.name == null ? "null" : this.name) + "}";
    }
}
