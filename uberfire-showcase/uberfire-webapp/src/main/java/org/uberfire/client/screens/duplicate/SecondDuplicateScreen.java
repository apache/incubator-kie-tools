package org.uberfire.client.screens.duplicate;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "Duplicate")
public class SecondDuplicateScreen {

    @WorkbenchPartTitle
    public String getName() {
        return "Duplicate - 2";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return new Label( "Duplicate screen - 2" );
    }
}
