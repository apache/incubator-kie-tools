package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.PopupPanel;

@WorkbenchPopup(identifier = "test9")
public class WorkbenchPopupTest9 {

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

    @OnStart
    public void onStart() {
    }

    @OnStart
    public void onStart(final PlaceRequest place) {
    }

}
