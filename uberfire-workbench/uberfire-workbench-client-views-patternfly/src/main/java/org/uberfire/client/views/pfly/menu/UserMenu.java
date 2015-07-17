package org.uberfire.client.views.pfly.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.User.StandardUserProperties;
import org.jboss.errai.security.shared.service.AuthenticationService;

/**
 * Provides the menu that appears in the top right corner of the screen. Shows the current user's name and provides a
 * menu that allows logging out.
 */
@ApplicationScoped
public class UserMenu extends UtilityMenu {


    @Inject
    private User user;

    @Inject
    private Caller<AuthenticationService> authService;

    private final DropDownMenu menu = new DropDownMenu();

    @PostConstruct
    private void setup() {
        anchor.addStyleName( Styles.DROPDOWN_TOGGLE );
        anchor.setDataToggle( Toggle.DROPDOWN );
        addStyleName( Styles.DROPDOWN_TOGGLE );

        final Span userIcon = new Span();
        userIcon.addStyleName( "pficon" );
        userIcon.addStyleName( "pficon-user" );

        anchor.add( userIcon );
        anchor.add( new Text( formattedUsername() ) );
        final Span caret = new Span();
        caret.addStyleName( Styles.CARET );
        anchor.add( caret );

        addStyleName( Styles.DROPDOWN );
        add( anchor );

        final AnchorListItem logout = new AnchorListItem( "Logout" );
        logout.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                logout();
            }
        } );
        menu.add( logout );
        add( menu );
    }

    /**
     * Returns the Bootstrap 3 DropDownMenu that appears when this widget is clicked. Can be used for adding, removing,
     * or modifying the existing menu structure.
     * @return the low-level widgets that make up the existing menu
     */
    public DropDownMenu getMenu() {
        return menu;
    }

    // TODO non-bs3-specific way of appending menus using the UberFire menu model
    // public void addMenus(Menus menus) {
    //   (use a MenuVisitor to append more menus)
    // }

    /**
     * Tries to return the user's first and/or last names. If neither is available, returns the user's ID instead.
     */
    private String formattedUsername() {
        final StringBuilder sb = new StringBuilder();
        if ( user.getProperty( StandardUserProperties.FIRST_NAME ) != null ) {
            sb.append( user.getProperty( StandardUserProperties.FIRST_NAME ) );
        }
        if ( user.getProperty( StandardUserProperties.LAST_NAME ) != null ) {
            sb.append( " " );
            sb.append( user.getProperty( StandardUserProperties.LAST_NAME ) );
        }
        if ( sb.length() == 0 ) {
            sb.append( user.getIdentifier() );
        }
        return sb.toString();
    }

    /**
     * Logout user
     */
    public void logout() {
        authService.call().logout();
    }
}
