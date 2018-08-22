package ot.webtest.framework.kketshelpers.dataobjects;

import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.helpers.AssertHelper;

public abstract class Task {
    // DATA SEQUENCE IS CRUCIAL
    public Integer number;
    public Special<String> technologicalOperation;
    public Special<String> element;
    public Special<String> routeName;
    public SpecialDateTime dateStart;
    public SpecialDateTime dateEnd;
    public TaskSource taskSource;
    public String comment;
    public Special<String> subdivision;

    public Task withNumber (Integer number) {
        this.number = number;
        return this;
    }

    public Task withTechnologicalOperation (Special<String> technologicalOperation) {
        this.technologicalOperation = technologicalOperation;
        return this;
    }

    public Task withElement(Special<String> element) {
        this.element = element;
        return this;
    }

    public Task withRouteName(Special<String> routeName) {
        this.routeName = routeName;
        return this;
    }

    public Task withDateStart (SpecialDateTime dateStart) {
        this.dateStart = dateStart;
        return this;
    }

    public Task withDateEnd (SpecialDateTime dateEnd) {
        this.dateEnd = dateEnd;
        return this;
    }

    public Task withTaskSource (TaskSource taskSource) {
        this.taskSource = taskSource;
        return this;
    }

    public Task withComment (String comment) {
        this.comment = comment;
        return this;
    }

    public Task withSubdivision (Special<String> subdivision) {
        this.subdivision = subdivision;
        return this;
    }

    protected static void checkEquals(Task taskActual, Task taskExpected) {
        AssertHelper.assertEquals(taskActual.number, taskExpected.number, "Поле number");
        AssertHelper.assertEquals(taskActual.technologicalOperation, taskExpected.technologicalOperation, "Поле technologicalOperation");
        AssertHelper.assertEquals(taskActual.element, taskExpected.element, "Поле element");
        AssertHelper.assertEquals(taskActual.routeName, taskExpected.routeName, "Поле routeName");
        AssertHelper.assertEquals(taskActual.dateStart, taskExpected.dateStart, "Поле dateStart");
        AssertHelper.assertEquals(taskActual.dateEnd, taskExpected.dateEnd, "Поле dateEnd");
        AssertHelper.assertEquals(taskActual.taskSource, taskExpected.taskSource, "Поле taskSource");
        AssertHelper.assertEquals(taskActual.comment, taskExpected.comment, "Поле comment");
        AssertHelper.assertEquals(taskActual.subdivision, taskExpected.subdivision, "Поле subdivision");
    }

    @Override
    public String toString() {
        String info = "";
        info += "technologicalOperation: " + technologicalOperation + "; ";
        info += "element: " + element + "; ";
        info += "routeName: " + routeName;
        return "{" + info + "}";
    }
}
