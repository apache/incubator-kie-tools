package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchSplashScreen;

@WorkbenchSplashScreen(identifier = "test3")
public class WorkbenchSplashScreenTest3 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }
    
}
