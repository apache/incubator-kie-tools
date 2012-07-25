package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

import com.google.gwt.user.client.ui.PopupPanel;

@WorkbenchPopup(identifier = "test5")
public class WorkbenchPopupTest5 extends PopupPanel {

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

}
