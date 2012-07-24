package org.drools.guvnor.annotations.processors;

import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchScreen(identifier = "test3")
public class WorkbenchScreenTest3 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }
    
}
