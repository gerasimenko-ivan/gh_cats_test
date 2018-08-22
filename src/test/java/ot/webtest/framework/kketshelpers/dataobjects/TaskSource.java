package ot.webtest.framework.kketshelpers.dataobjects;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum TaskSource {
    APPLICATION ("Заявка"),
    ROUTINE_WORK ("Регламентные работы"),
    FACKSOGRAM ("Факсограмма");

    private final String name;

    TaskSource(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public static TaskSource getByName(String name) {
        for (TaskSource taskSource : TaskSource.values()) {
            if (taskSource.name.equals(name)) {
                return taskSource;
            }
        }
        return null;
    }

    public static TaskSource getRandom() {
        return values()[new Random().nextInt(TaskSource.values().length)];
    }
    public static TaskSource getRandomWithoutFACKSOGRAM() {
        List<TaskSource> valuesForDecentr = Arrays.stream(TaskSource.values()).filter(v -> v != FACKSOGRAM).collect(Collectors.toList());
        return valuesForDecentr.get(new Random().nextInt(valuesForDecentr.size()));
    }
}
