package org.uberfire.client.screens.popup;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

/**
 *
 */
@WorkbenchPopup(identifier = "MyTestPopUp")
public class SimplePopUp {

    @WorkbenchPartTitle
    public String getTitle() {
        return "MyPopUp Title here";
    }

    @WorkbenchPartView
    public Widget getView() {
        return new Label( "my content here" );
    }

    @OnStart
    public void onStart() {
    }
}
