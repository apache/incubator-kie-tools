package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "test27", preferredHeight = -200)
public class WorkbenchScreenTest27 extends SimplePanel {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}