package org.uberfire.client.screens.duplicate;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@WorkbenchScreen(identifier = "Duplicate")
public class FirstDuplicateScreen {

    @WorkbenchPartTitle
    public String getName() {
        return "Duplicate - 1";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return new Label( "Duplicate screen - 1" );
    }
}
