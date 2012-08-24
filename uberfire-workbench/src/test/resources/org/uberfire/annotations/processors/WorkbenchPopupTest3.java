package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

import com.google.gwt.user.client.ui.PopupPanel;
import org.uberfire.security.annotations.Roles;

@WorkbenchPopup(identifier = "test3")
@Roles({"ADMIN", "SUDO"})
public class WorkbenchPopupTest3 {

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

}
