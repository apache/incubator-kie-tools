package org.uberfire.client.views.bs2.menu;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.uberfire.client.menu.SplashScreenMenuPresenter;
import org.uberfire.client.menu.SplashScreenMenuPresenter.SplashScreenListEntry;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

@Dependent
public class SplashScreenMenuView extends Composite implements SplashScreenMenuPresenter.View {

    final Dropdown dropdown = new Dropdown();

    public SplashScreenMenuView() {
        dropdown.getTriggerWidget().setCaret( false );
        dropdown.setRightDropdown( true );
        dropdown.setIcon( IconType.QUESTION_SIGN );
        dropdown.ensureDebugId( "MenuSplashList-dropdown" );
        initWidget( dropdown );
    }

    @Override
    public void init( SplashScreenMenuPresenter presenter ) {
        // don't need presenter ref
    }

    @Override
    public void setSplashScreenList( List<SplashScreenListEntry> splashScreens ) {
        dropdown.clear();
        for ( final SplashScreenListEntry entry : splashScreens ) {
            dropdown.add( new NavLink( entry.getScreenName() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        entry.getShowCommand().execute();
                    }
                } );
            }} );
        }
        if ( dropdown.getMenuWiget().getWidgetCount() == 0 ) {
            NavLink emptyMenuPlaceholder = new NavLink( "-- none --" );
            emptyMenuPlaceholder.setDisabled( true );
            dropdown.add( emptyMenuPlaceholder );
        }
    }
}
