package ot.webtest.framework.kketshelpers.nsi.dataobject;

import java.util.Random;

public enum FuelConsumptionMetrics {
    LITERS_PER_KM ("л/км"),
    LITERS_PER_MOTORHOUR ("л/моточас"),
    LITERS_PER_LIFT ("л/подъем"),
    LITERS_PER_HOUR ("л/час");

    private final String name;

    FuelConsumptionMetrics(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public static FuelConsumptionMetrics getByName(String name) {
        for (FuelConsumptionMetrics fuelConsumptionMetric : FuelConsumptionMetrics.values()) {
            if (fuelConsumptionMetric.name.equals(name)) {
                return fuelConsumptionMetric;
            }
        }
        return null;
    }

    public String toString() {
        return this.name;
    }

    public static FuelConsumptionMetrics getRandom() {
        return values()[new Random().nextInt(FuelConsumptionMetrics.values().length)];
    }
}
