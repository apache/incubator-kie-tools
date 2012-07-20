package org.drools.guvnor.client.editors.jbpm;

import com.google.gwt.user.client.ui.Label;

public class HeaderLabel extends Label {

    public HeaderLabel(String string) {
        super(string);
        applyStyle(false);
    }

    public HeaderLabel(String string, boolean invert) {
        super(string);
        applyStyle(invert);
    }

    private void applyStyle(boolean invert) {
        if (invert)
            this.setStyleName("bpm-label-header-invert");
        else
            this.setStyleName("bpm-label-header");
    }
}
