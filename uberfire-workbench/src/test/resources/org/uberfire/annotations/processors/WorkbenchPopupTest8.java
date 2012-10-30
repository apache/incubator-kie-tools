package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.PopupPanel;

@WorkbenchPopup(identifier = "test8")
public class WorkbenchPopupTest8 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

    @OnStart
    public void onStart(final PlaceRequest place) {
    }

}
