package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchSplashScreen;
import org.uberfire.security.annotations.Roles;

@WorkbenchSplashScreen(identifier = "test4")
@Roles({"ADMIN", "SUDO"})
public class WorkbenchSplashScreenTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
