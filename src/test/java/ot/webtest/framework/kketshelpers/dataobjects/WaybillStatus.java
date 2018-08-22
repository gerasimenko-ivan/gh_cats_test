package ot.webtest.framework.kketshelpers.dataobjects;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum WaybillStatus {
    ADD_TO_ACTIVE_WAIBILL ("Добавить в активный ПЛ"),
    ADD_TO_SKETCH_OF_WAIBILL ("Добавить в черновик ПЛ"),
    CREATE_SKETCH_OF_WAIBILL ("Создать черновик ПЛ");

    private final String name;

    WaybillStatus(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public static WaybillStatus getRandom() {
        return values()[new Random().nextInt(WaybillStatus.values().length)];
    }

    public static WaybillStatus getRandomWithoutAddingToActive() {
        List<WaybillStatus> waybillStatuses = Arrays.stream(WaybillStatus.values()).filter(x -> x != ADD_TO_ACTIVE_WAIBILL).collect(Collectors.toList());
        return waybillStatuses.get(new Random().nextInt(waybillStatuses.size()));
    }
}
