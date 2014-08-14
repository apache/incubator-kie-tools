package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.util.Layouts;

import com.google.gwt.user.client.ui.FlowPanel;

@WorkbenchPerspective( identifier = "ExampleDeclarativePerspective", isTransient = true )
public class ExampleDeclarativePerspective extends FlowPanel {

    @Inject
    @WorkbenchPanel( parts = "HomeScreen" )
    FlowPanel theOnlyPanel;

    @PostConstruct
    void doLayout() {
        Layouts.setToFillParent( theOnlyPanel );
        add( theOnlyPanel );
    }
}
