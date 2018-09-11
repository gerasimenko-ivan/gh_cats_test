package ot.webtest.dataobject;

import java.util.List;

public class DropDownResponse {
    public Special<String> selectedValue;
    public List<String> availableOptions;

    public DropDownResponse withSelectedValue (Special<String> selectedValue) {
        this.selectedValue = selectedValue;
        return this;
    }
    public DropDownResponse withAvailableOptions (List<String> availableOptions) {
        this.availableOptions = availableOptions;
        return this;
    }
}
