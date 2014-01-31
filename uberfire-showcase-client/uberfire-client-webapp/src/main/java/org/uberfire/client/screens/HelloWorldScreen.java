package org.uberfire.client.screens;

import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "HelloWorldScreen")
public class HelloWorldScreen {

    private static final String ORIGINAL_TEXT = "Hello UberFire!";

    private Label label = new Label( ORIGINAL_TEXT );

    @WorkbenchPartTitle
    public String getTitle() {
        return "Greetings";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return label;
    }
}