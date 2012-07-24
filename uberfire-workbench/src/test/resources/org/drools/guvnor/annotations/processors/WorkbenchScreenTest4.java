package org.drools.guvnor.annotations.processors;

import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "test4")
public class WorkbenchScreenTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
