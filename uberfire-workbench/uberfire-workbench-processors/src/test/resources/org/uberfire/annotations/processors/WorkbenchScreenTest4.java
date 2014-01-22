package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.security.annotations.Roles;

@WorkbenchScreen(identifier = "test4")
@Roles({"ADMIN", "SUDO"})
public class WorkbenchScreenTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
