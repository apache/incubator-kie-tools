package org.uberfire.client.screens.popup;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

/**
 *
 */
@WorkbenchPopup(identifier = "MyTestPopUp")
public class SimplePopUp {

    @Inject
    public Event<BeforeClosePlaceEvent> beforeClosePlaceEvent;

    private PlaceRequest place;

    private VerticalPanel view = new VerticalPanel();
    private Label l;
    private TextBox t;
    private Button b;

    @PostConstruct
    public void setup() {
        l = new Label( "Click to close" );
        t = new TextBox();
        b = new Button( "Close" );
        b.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                beforeClosePlaceEvent.fire( new BeforeClosePlaceEvent( place ) );
            }
        } );
        view.add( l );
        view.add( t );
        view.add( b );
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
        b.setFocus( true );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "MyPopUp Title here";
    }

    @WorkbenchPartView
    public Widget getView() {
        return view;
    }

}
