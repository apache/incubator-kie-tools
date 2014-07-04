package org.uberfire.client.workbench.widgets.listbar;

public class ListbarPreferences {

    private boolean contextEnabled;

    public ListbarPreferences() {

    }

    public ListbarPreferences( boolean contextEnabled ) {
        this.contextEnabled = contextEnabled;
    }

    public boolean isContextEnabled() {
        return contextEnabled;
    }
}
