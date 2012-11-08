package org.uberfire.client.screens.dublicate;


import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "Dublicate")
public class SecondDublicateScreen {


    @WorkbenchPartTitle
    public String getName() {
        return "2";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return new Label("2");
    }
}
