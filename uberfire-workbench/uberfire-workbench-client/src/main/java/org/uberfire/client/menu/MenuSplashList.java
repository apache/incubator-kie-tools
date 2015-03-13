package org.uberfire.client.menu;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

/**
 * A drop-down style widget that contains an up-to-date list of the available splash screens. Each currently-displayed
 * part, plus the current perspective itself, may have a splash screen associated with it. Splash screens show up in
 * this widget's drop-down list even if they are not currently visible. The only requirement is that the part they are
 * associated with is currently in the workbench.
 * <p>
 * When the user clicks on one of the items in the drop down list, the corresponding splash screen will be displayed via
 * its {@link SplashScreenActivity#forceShow()} method.
 */
@ApplicationScoped
public class MenuSplashList extends Composite {

    @Inject
    private PlaceManager placeManager;

    final Dropdown dropdown = new Dropdown();

    public MenuSplashList() {
        dropdown.getTriggerWidget().setCaret( false );
        dropdown.setRightDropdown( true );
        dropdown.setIcon( IconType.QUESTION_SIGN );
        dropdown.setTitle( WorkbenchConstants.INSTANCE.showSplashHelp() );
        dropdown.ensureDebugId( "MenuSplashList-dropdown" );
        initWidget( dropdown );
    }

    void onNewSplash( @Observes NewSplashScreenActiveEvent event ) {
        dropdown.clear();
        for ( final SplashScreenActivity active : placeManager.getActiveSplashScreens() ) {
            dropdown.add( new NavLink( active.getTitle() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        active.forceShow();
                    }
                } );
            }} );
        }
        if ( dropdown.getMenuWiget().getWidgetCount() == 0 ) {
            dropdown.add( new NavLink( "-- none --" ) {{
                setDisabled( true );
            }} );
        }
    }
}
