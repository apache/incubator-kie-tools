package org.uberfire.wbtest.client.panels.custom;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Named( "org.uberfire.wbtest.client.panels.custom.CustomPanelContentScreen" )
public class CustomPanelContentScreen extends AbstractTestScreenActivity {

    private final FlowPanel panel = new FlowPanel();

    @Inject
    private CustomPanelInstanceCounter instanceCounter;

    @Inject
    private PlaceManager placeManager;

    @Inject
    public CustomPanelContentScreen( PlaceManager pm ) {
        super( pm );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );

        final String id = place.getParameter( "debugId", "" );
        panel.ensureDebugId( "CustomPanelContentScreen-" + id );

        Label label = new Label( "I'm in the custom widget! debugId=" + id );

        Button closeButton = new Button( "Close with PlaceManager" );
        closeButton.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                placeManager.closePlace( new DefaultPlaceRequest( CustomPanelContentScreen.class.getName(),
                                                                  ImmutableMap.<String, String>of( "debugId", id ) ) );
            }
        } );

        panel.add( label );
        panel.add( closeButton );
        instanceCounter.instanceCreated();
    }

    @Override
    public void onShutdown() {
        instanceCounter.instanceDestroyed();
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }
}
