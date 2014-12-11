package org.uberfire.wbtest.client.panels.maximize;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@Named( "org.uberfire.wbtest.client.panels.maximize.MaximizeTestScreen" )
public class MaximizeTestScreen extends AbstractTestScreenActivity {

    @Inject WorkbenchLayout workbenchLayout;

    private ResizeFlowPanel panel = new ResizeFlowPanel() {
        @Override
        public void onResize() {
            // unfortunately we have to bake in this assumption to get the real size of the screen
            // (when we live inside the scroll panel, we can't be sized to fill the panel's display area).
            // the cast is a failsafe in case the assumption "parent is scroll panel" becomes wrong.
            ScrollPanel parent = (ScrollPanel) getParent();
            sizeLabel.setText( parent.getOffsetWidth() + "x" + parent.getOffsetHeight() );
            super.onResize();
        }
    };

    private Label sizeLabel = new Label( "size not initialized" );

    private String id;

    @Inject
    public MaximizeTestScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );

        id = place.getParameter( "debugId", "" );

        panel.clear();

        panel.getElement().setId( "MaximizeTestScreen-" + id );

        sizeLabel.getElement().setId( "MaximizeTestScreen-" + id + "-sizeLabel" );
        panel.add( sizeLabel );

        TextBox textBox = new TextBox();
        textBox.getElement().setId( "MaximizeTestScreen-" + id + "-textBox" );
        panel.add( textBox );

        Button dumpLayoutButton = new Button( "Dump Layout" );
        dumpLayoutButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                System.out.println( Layouts.getContainmentHierarchy( panel ) );
            }
        } );
        panel.add( dumpLayoutButton );

        Button forceResizeButton = new Button( "Force resize" );
        forceResizeButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                workbenchLayout.onResize();
            }
        } );
        panel.add( forceResizeButton );
    }

    @Override
    public String getTitle() {
        return "Maximize Test Screen " + id;
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
