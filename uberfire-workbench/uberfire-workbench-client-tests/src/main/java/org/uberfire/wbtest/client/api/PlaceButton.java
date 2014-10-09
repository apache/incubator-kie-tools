package org.uberfire.wbtest.client.api;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;

/**
 * Convenient wrapper for a button that goes to a particular place when clicked.
 */
public class PlaceButton extends Composite {

    private final Button button = new Button();

    public PlaceButton( final PlaceManager placeManager, final DefaultPlaceRequest goTo ) {
        checkNotNull( "placeManager", placeManager );
        checkNotNull( "goTo", goTo );

        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                placeManager.goTo( goTo );
            }
        } );
        button.setText( goTo.toString() );
        initWidget( button );
    }

}
