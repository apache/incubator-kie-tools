package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "test5")
public class WorkbenchScreenTest5 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
