package ot.webtest.framework.testrail.dataobject;

public class Milestone {
    public Integer id;
    public String name;
    public Integer parentId;
    public Long startOn;
    public Boolean isStarted;
    public Long dueOn;
    public Boolean isCompleted;

    public Milestone withId(Integer id) {
        this.id = id;
        return this;
    }
    public Milestone withName(String name) {
        this.name = name;
        return this;
    }
    public Milestone withParentId(Integer parentId) {
        this.parentId = parentId;
        return this;
    }
    public Milestone withStartOn(Long startOn) {
        this.startOn = startOn;
        return this;
    }
    public Milestone withIsStarted(Boolean isStarted) {
        this.isStarted = isStarted;
        return this;
    }
    public Milestone withDueOn(Long dueOn) {
        this.dueOn = dueOn;
        return this;
    }
    public Milestone withIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
        return this;
    }

    @Override
    public String toString() {
        return "{ID: " + this.id + "; name: " + this.name + "}";
    }
}
