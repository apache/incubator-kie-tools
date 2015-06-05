package org.uberfire.client.screens.popup;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

/**
 * An example popup
 */
@WorkbenchPopup(identifier = "MyTestPopUp")
public class SimplePopUp {

    @Inject
    private PlaceManager placeManager;

    private PlaceRequest place;

    private final VerticalPanel view = new VerticalPanel();
    private Paragraph p;

    @PostConstruct
    public void setup() {
        p = new Paragraph("Cool PopUp!");
        view.add( p );
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
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
