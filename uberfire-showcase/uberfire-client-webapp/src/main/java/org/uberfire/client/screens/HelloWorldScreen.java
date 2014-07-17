package org.uberfire.client.screens;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.uberfire.client.ShowcaseEntryPoint.DumpLayout;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.util.Layouts;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@WorkbenchScreen(identifier = "HelloWorldScreen")
public class HelloWorldScreen {

    private static final String ORIGINAL_TEXT = "Hello UberFire!";

    private final Label label = new Label( ORIGINAL_TEXT );

    @WorkbenchPartTitle
    public String getTitle() {
        return "Greetings";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return label;
    }

    public void dumpLayout( @Observes DumpLayout dl ) {
        System.out.println( "Dumping HelloWorldScreen hierarchy:" );
        System.out.println( Layouts.getContainmentHierarchy( label ) );
    }
}