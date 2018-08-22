package ot.webtest.framework.testrail.dataobject;

public enum TestRailStatus {
    PASSED (1),
    BLOCKED(2),
    //UNTESTED (3), // could not be used: ot.webtest.framework.testrail.APIException: TestRail API returned HTTP 400("Field :status_id uses an invalid status (Untested).")
    RETEST(4),
    FAILED (5);

    private final int statusId;

    TestRailStatus(int s) {
        statusId = s;
    }

    public boolean equalsStatusId(int otherStatusId) {
        // (otherStatusId == null) check is not needed because otherStatusId is never null
        return statusId == otherStatusId;
    }

    public int toInt() {
        return this.statusId;
    }
}
