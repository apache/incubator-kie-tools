package org.uberfire.ext.wires.client;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Label;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

/**
 * Created by Cristiano Nicolai.
 */
@Dependent
@WorkbenchScreen( identifier = "SimpleDockScreen" )
public class SimpleDockScreen {

    final Label label = new Label( "Docks Content" );

    @WorkbenchPartTitle
    public String getTitle() {
        return "Simple Dock Screen";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return label;
    }

}
