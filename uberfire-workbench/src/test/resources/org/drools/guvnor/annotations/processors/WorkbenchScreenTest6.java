package org.drools.guvnor.annotations.processors;

import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchScreen(identifier = "test6")
public class WorkbenchScreenTest6 extends SimplePanel {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
