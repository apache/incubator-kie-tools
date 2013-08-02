package org.uberfire.annotations.processors;

import org.uberfire.lifecycle.OnOpen;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

import com.google.gwt.user.client.ui.PopupPanel;

@WorkbenchPopup(identifier = "test6")
public class WorkbenchPopupTest6 {

    @OnOpen
    public void onOpen() {
        //Do nothing
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

}
