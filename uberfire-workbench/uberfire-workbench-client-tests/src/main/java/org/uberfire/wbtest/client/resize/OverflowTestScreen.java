package org.uberfire.wbtest.client.resize;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

@Dependent
@Named("org.uberfire.wbtest.client.resize.OverflowTestScreen")
public class OverflowTestScreen extends AbstractTestScreenActivity {

    private final Panel panel = new VerticalPanel();

    @Inject
    public OverflowTestScreen( PlaceManager placeManager ) {
        super( placeManager );

        Button dumpHierarchyButton = new Button( "Dump Layout Hierarchy to System.out" );
        dumpHierarchyButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                System.out.println( Layouts.getContainmentHierarchy( panel, true ) );
            }
        } );
        panel.add( dumpHierarchyButton );

        for ( int i = 0; i < 100; i++ ) {
            panel.add( new Label( "Filler row " + i ) );
        }
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }
}
