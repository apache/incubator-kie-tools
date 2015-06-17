package org.uberfire.client.views.pfly.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.User.StandardUserProperties;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.client.menu.AuthFilterMenuVisitor;
import org.uberfire.client.workbench.widgets.menu.HasMenus;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

/**
 * Provides the menu that appears in the top right corner of the screen. Shows the current user's name and provides a
 * menu that allows logging out and switch the workbench menu view.
 */
@ApplicationScoped
public class UserMenu extends AnchorListItem implements MenuFactory.CustomMenuBuilder, HasMenus, HasMenuItems {

    @Inject
    private User user;

    @Inject
    private Caller<AuthenticationService> authService;

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private WorkbenchMenuBar menubar;

    private final DropDownMenu menu = new DropDownMenu();

    @PostConstruct
    protected void setup() {
        anchor.addStyleName( Styles.DROPDOWN_TOGGLE );
        anchor.setDataToggle( Toggle.DROPDOWN );

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

        add( menu );
        setupLogout();
    }

    protected void setupLogout() {
        final AnchorListItem logout = new AnchorListItem( "Logout" );
        logout.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                logout();
            }
        } );
        menu.add( logout );
    }

    @Override
    public void addMenus( final Menus menus ) {
        menus.accept( new AuthFilterMenuVisitor( authzManager, user, new DropdownMenuVisitor( this ) ) );
    }

    @Override
    public void addMenuItem( final MenuPosition position, final Widget menuContent ) {
        //Always add new option on top
        menu.insert( menuContent, 0 );
    }

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

    @Override
    public void push( MenuFactory.CustomMenuBuilder element ) {

    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {

            @Override
            public IsWidget build() {
                return IOC.getBeanManager().lookupBean( UserMenu.class ).getInstance();
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }

}
