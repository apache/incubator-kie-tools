package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;

@WorkbenchScreen(identifier = "test13")
public class WorkbenchScreenTest13 {

    @WorkbenchPartView
    public UberView<WorkbenchScreenTest13> getView() {
        return null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @OnStart
    public void onStart() {
    }

    @OnStart
    public void onStart(final PlaceRequest place) {
    }

}
