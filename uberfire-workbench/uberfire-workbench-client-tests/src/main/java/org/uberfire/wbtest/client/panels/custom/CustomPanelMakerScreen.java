package org.uberfire.wbtest.client.panels.custom;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

@Dependent
@Named("org.uberfire.wbtest.client.panels.custom.CustomPanelMakerScreen")
public class CustomPanelMakerScreen extends AbstractTestScreenActivity {

    private final Panel panel = new FlowPanel();

    @Inject
    private PlaceManager placeManager;

    @Inject
    public CustomPanelMakerScreen( PlaceManager placeManager ) {
        super( placeManager );
    }

    @PostConstruct
    private void setup() {
        Button open = new Button("Open custom place");
        open.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {

                SimplePanel customContainer = new SimplePanel();
                customContainer.setPixelSize( 200, 200 );

                PopupPanel popup = new PopupPanel( true );
                popup.setWidget( customContainer );
                popup.setPopupPosition( 100, 100 );
                popup.show();

                DefaultPlaceRequest popupPlace = new DefaultPlaceRequest( ResizeTestScreenActivity.class.getName(), ImmutableMap.of( "debugId", "customPopup" ) );
                placeManager.goTo( popupPlace, customContainer );
            }
        } );

        Button closeWithPlaceManager = new Button("Close with PlaceManager");
        closeWithPlaceManager.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                placeManager.closePlace( ResizeTestScreenActivity.class.getName() );
            }
        } );

        panel.add( open );
        panel.add( closeWithPlaceManager );
//        panel.add( closeByRemovingFromDom );
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }

}
