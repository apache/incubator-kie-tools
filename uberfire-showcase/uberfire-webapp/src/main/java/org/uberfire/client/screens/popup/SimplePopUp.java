package org.uberfire.client.screens.popup;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

/**
 *
 */
@WorkbenchPopup(identifier = "MyTestPopUp")
public class SimplePopUp {

    @Inject
    public Event<BeforeClosePlaceEvent> beforeClosePlaceEvent;

    private PlaceRequest place;

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "MyPopUp Title here";
    }

    @WorkbenchPartView
    public Widget getView() {
        Button b = new Button( "Close" );
        b.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                beforeClosePlaceEvent.fire( new BeforeClosePlaceEvent( place ) );
            }
        } );
        return b;
    }

}
