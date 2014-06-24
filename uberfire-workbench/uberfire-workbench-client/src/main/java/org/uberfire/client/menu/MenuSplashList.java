package org.uberfire.client.menu;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;

@ApplicationScoped
public class MenuSplashList extends Composite {

    @Inject
    private PlaceManager placeManager;

    final Dropdown content = new Dropdown() {{
        trigger.setCaret( false );
        setRightDropdown( true );
        setIcon( IconType.QUESTION_SIGN );
    }};

    public MenuSplashList() {
        initWidget( content );
    }

    void onNewSplash( @Observes NewSplashScreenActiveEvent event ) {
        content.clear();
        for ( final SplashScreenActivity active : placeManager.getActiveSplashScreens() ) {
            content.add( new NavLink( active.getTitle() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        active.forceShow();
                    }
                } );
            }} );
        }
        if ( content.getMenuWiget().getWidgetCount() == 0 ) {
            content.add( new NavLink( "-- none --" ) {{
                setDisabled( true );
            }} );
        }
    }
}
