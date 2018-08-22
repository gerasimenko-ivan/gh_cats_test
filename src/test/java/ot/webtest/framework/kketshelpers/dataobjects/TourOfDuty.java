package ot.webtest.framework.kketshelpers.dataobjects;

import ot.webtest.dataobject.Special;

// НАРЯД-ЗАДАНИЕ
public class TourOfDuty extends Task {

    public Special<String> brigadier;


    public TourOfDuty withBrigadier(Special<String> brigadier) {
        this.brigadier = brigadier;
        return this;
    }
}
