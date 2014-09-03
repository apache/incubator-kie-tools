package org.uberfire.wbtest.client.panels.custom;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

@Dependent
@Named("org.uberfire.wbtest.client.panels.custom.CustomPanelMakerScreen")
public class CustomPanelMakerScreen extends AbstractTestScreenActivity {

    private final Panel panel = new FlowPanel();
    private final Label liveCustomPanelInstances = new Label( "?" );
    private final Label totalCustomPanelInstances = new Label( "?" );

    /**
     * The most recent popup that was shown (by clicking the "Open custom place" button).
     */
    private PopupPanel popup;

    @Inject
    private PlaceManager placeManager;

    @Inject CustomPanelInstanceCounter instanceCounter;

    @Inject
    public CustomPanelMakerScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @PostConstruct
    private void setup() {
        Button open = new Button("Open custom place");
        open.ensureDebugId( "CustomPanelMakerScreen-open" );
        open.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {

                SimplePanel customContainer = new SimplePanel();
                customContainer.setPixelSize( 200, 200 );

                popup = new PopupPanel( false );
                popup.setWidget( customContainer );
                popup.setPopupPosition( 150, 150 );
                popup.show();

                ImmutableMap<String, String> params = ImmutableMap.of( "debugId", "" + instanceCounter.getCreationCount() );
                DefaultPlaceRequest popupPlace = new DefaultPlaceRequest( CustomPanelContentScreen.class.getName(), params );
                placeManager.goTo( popupPlace, customContainer );
            }
        } );

        Button closeWithPlaceManager = new Button("Close Latest with PlaceManager");
        closeWithPlaceManager.ensureDebugId( "CustomPanelMakerScreen-closeWithPlaceManager" );
        closeWithPlaceManager.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                ImmutableMap<String, String> params = ImmutableMap.of( "debugId", "" + ( instanceCounter.getCreationCount() - 1 ) );
                DefaultPlaceRequest place = new DefaultPlaceRequest( CustomPanelContentScreen.class.getName(), params );
                placeManager.closePlace( place );
            }
        } );

        Button closeByRemovingFromDom = new Button("Remove latest popup from DOM");
        closeByRemovingFromDom.ensureDebugId( "CustomPanelMakerScreen-closeByRemovingFromDom" );
        closeByRemovingFromDom.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( popup != null ) {
                    popup.hide();
                    popup = null;
                }
            }
        } );

        liveCustomPanelInstances.ensureDebugId( "CustomPanelMakerScreen-liveCustomPanelInstances" );
        totalCustomPanelInstances.ensureDebugId( "CustomPanelMakerScreen-totalCustomPanelInstances" );

        panel.add( open );
        panel.add( closeWithPlaceManager );
        panel.add( closeByRemovingFromDom );
        panel.add( liveCustomPanelInstances );
        panel.add( totalCustomPanelInstances );

        onInstanceCountChanged( instanceCounter );
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

    void onInstanceCountChanged( @Observes CustomPanelInstanceCounter instanceCounter ) {
        liveCustomPanelInstances.setText( "Live Instances: " + instanceCounter.getLiveInstances() );
        totalCustomPanelInstances.setText( "Total Instances: " + instanceCounter.getCreationCount() );
    }
}
