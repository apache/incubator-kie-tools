package org.uberfire.client.screens;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.perspectives.SimplePerspective;

import com.google.gwt.user.client.ui.Label;

@WorkbenchScreen(identifier = "OwnedScreen", owningPerspective = SimplePerspective.class)
public class OwnedScreen {

    private final Label view = new Label("This screen is always displayed in the SimplePerspective.");

    @WorkbenchPartTitle
    public String getTitle() {
        return "Owned Screen";
    }

    @WorkbenchPartView
    public Label getView() {
        return view;
    }

}
