package org.uberfire.client.workbench.widgets.listbar;

public class ListbarPreferences {

    private boolean contextEnabled;

    private boolean hideTitleDropDownOnSingleElement = true;

    public ListbarPreferences() {

    }

    public ListbarPreferences( boolean contextEnabled ) {
        this.contextEnabled = contextEnabled;
    }

    public boolean isContextEnabled() {
        return contextEnabled;
    }

    public boolean isHideTitleDropDownOnSingleElement() {
        return hideTitleDropDownOnSingleElement;
    }

    public void setHideTitleDropDownOnSingleElement( boolean hideTitleDropDownOnSingleElement ) {
        this.hideTitleDropDownOnSingleElement = hideTitleDropDownOnSingleElement;
    }
}
