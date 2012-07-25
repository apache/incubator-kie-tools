package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

import com.google.gwt.user.client.ui.PopupPanel;

@WorkbenchPopup(identifier = "test3")
public class WorkbenchPopupTest3 {

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

}
