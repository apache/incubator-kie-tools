package org.uberfire.client.screens.dublicate;


import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@WorkbenchScreen(identifier = "Dublicate")
public class FirstDublicateScreen {


    @WorkbenchPartTitle
    public String getName() {
        return "1";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return new Label("1");
    }
}
