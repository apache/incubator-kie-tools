package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.mvp.UberView;

@WorkbenchScreen(identifier = "test10")
public class WorkbenchScreenTest10 {

    @WorkbenchPartView
    public UberView<WorkbenchScreenTest10> getView() {
        return null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @WorkbenchMenu
    public String getMenuBar() {
        return "";
    }

}
