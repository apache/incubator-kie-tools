package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchSplashScreen;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.security.annotations.Roles;

@WorkbenchSplashScreen(identifier = "test5")
@Roles({"ADMIN", "SUDO"})
public class WorkbenchSplashScreenTest5 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
