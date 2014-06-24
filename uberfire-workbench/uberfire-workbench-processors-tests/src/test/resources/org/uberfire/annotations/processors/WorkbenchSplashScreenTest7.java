package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.SplashFilter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchSplashScreen;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchSplashScreen(identifier = "test7")
public class WorkbenchSplashScreenTest7 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @SplashFilter
    public SplashScreenFilter getFilter() {
        return null;
    }

    @OnStartup
    public void onStartup() {
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
    }
}
