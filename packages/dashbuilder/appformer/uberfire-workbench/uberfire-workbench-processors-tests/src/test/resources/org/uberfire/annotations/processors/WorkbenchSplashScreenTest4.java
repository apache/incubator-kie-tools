package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchSplashScreen;

@WorkbenchSplashScreen(identifier = "test4")
public class WorkbenchSplashScreenTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
