package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;

@WorkbenchScreen(identifier = "test11")
public class WorkbenchScreenTest11 {

    @WorkbenchPartView
    public UberView<WorkbenchScreenTest11> getView() {
        return null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @OnStartup
    public void onStartup() {
    }

}
