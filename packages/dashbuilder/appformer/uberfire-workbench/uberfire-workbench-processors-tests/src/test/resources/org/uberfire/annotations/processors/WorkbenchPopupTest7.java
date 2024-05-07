package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.PopupPanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.lifecycle.OnStartup;

@WorkbenchPopup(identifier = "test7")
public class WorkbenchPopupTest7 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

    @OnStartup
    public void onStartup() {
    }

}
