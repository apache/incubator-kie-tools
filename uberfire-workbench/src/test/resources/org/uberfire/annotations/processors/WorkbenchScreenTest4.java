package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "test4")
public class WorkbenchScreenTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
