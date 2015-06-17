package org.uberfire.client.views.pfly.menu;

import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.uberfire.client.menu.SplashScreenMenuPresenter;
import org.uberfire.client.menu.SplashScreenMenuPresenter.SplashScreenListEntry;

@Dependent
public class SplashScreenMenuView extends AnchorListItem implements SplashScreenMenuPresenter.View {

    final DropDownMenu dropdown = new DropDownMenu();

    public SplashScreenMenuView() {
        addStyleName(Styles.DROPDOWN_TOGGLE);
        ensureDebugId("MenuSplashList-dropdown");

        anchor.addStyleName(Styles.DROPDOWN_TOGGLE);
        anchor.setDataToggle(Toggle.DROPDOWN);
        anchor.setIcon(IconType.QUESTION);

        add(dropdown);
    }

    @Override
    public void init( SplashScreenMenuPresenter presenter ) {
        // don't need presenter ref
    }

    @Override
    public void setSplashScreenList( final List<SplashScreenListEntry> splashScreens ) {
        dropdown.clear();
        for ( final SplashScreenListEntry entry : splashScreens ) {
            final AnchorListItem item = new AnchorListItem( entry.getScreenName() );
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
