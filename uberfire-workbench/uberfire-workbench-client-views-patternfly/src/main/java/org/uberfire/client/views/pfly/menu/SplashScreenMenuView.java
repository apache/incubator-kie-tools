package org.uberfire.client.views.pfly.menu;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.uberfire.client.menu.SplashScreenMenuPresenter;
import org.uberfire.client.menu.SplashScreenMenuPresenter.SplashScreenListEntry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

@Dependent
public class SplashScreenMenuView extends Composite implements SplashScreenMenuPresenter.View {

    final DropDownMenu dropdown = new DropDownMenu();

    public SplashScreenMenuView() {
        DropDown container = new DropDown();
        container.setPull( Pull.RIGHT );
        container.ensureDebugId( "MenuSplashList-dropdown" );

        Button button = new Button();
        button.setIcon( IconType.QUESTION );

        container.add( button );
        container.add( dropdown );
        initWidget( container );
    }

    @Override
    public void init( SplashScreenMenuPresenter presenter ) {
        // don't need presenter ref
    }

    @Override
    public void setSplashScreenList( List<SplashScreenListEntry> splashScreens ) {
        dropdown.clear();
        for ( final SplashScreenListEntry entry : splashScreens ) {
            AnchorListItem item = new AnchorListItem( entry.getScreenName() );
            item.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    entry.getShowCommand().execute();
                }
            } );
            dropdown.add( item );
        }
        if ( dropdown.getWidgetCount() == 0 ) {
            dropdown.add( new DropDownHeader( "-- none --" ) );
        }
    }
}
