package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchScreen(identifier = "test17")
public class WorkbenchScreenTest17 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTabTitle() {
        return null;
    }

    @WorkbenchPartTitle
    public IsWidget getTabWidget() {
        return null;
    }

}
