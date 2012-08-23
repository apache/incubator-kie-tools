package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.security.annotations.AnyRole;

@WorkbenchScreen(identifier = "test4")
@AnyRole({"ADMIN", "SUDO"})
public class WorkbenchScreenTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
