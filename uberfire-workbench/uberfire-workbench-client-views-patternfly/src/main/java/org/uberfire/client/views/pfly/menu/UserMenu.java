package org.uberfire.client.views.pfly.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.User.StandardUserProperties;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

/**
 * Provides the menu that appears in the top right corner of the screen. Shows the current user's name and provides a
 * menu that allows logging out.
 */
@ApplicationScoped
public class UserMenu extends ListItem {


    public class Builder implements CustomMenuBuilder {

        @Override
        public void push( CustomMenuBuilder element ) {
            throw new UnsupportedOperationException( "Not implemented." );
        }

        @Override
        public MenuItem build() {
            return new BaseMenuCustom<UserMenu>() {

                @Override
                public UserMenu build() {
                    return IOC.getBeanManager().lookupBean( UserMenu.class ).getInstance();
                }

                @Override
                public void accept( MenuVisitor visitor ) {
                    visitor.visit( this );
                }
            };
        }

    }

    @Inject
    private User user;

    private final DropDownMenu menu = new DropDownMenu();

    @PostConstruct
    private void setup() {
        AnchorButton mainContent = new AnchorButton( ButtonType.DEFAULT );

        // we want AnchorButton for its magic dropdown Caret feature, not its buttony styling
        mainContent.removeStyleName( Styles.BTN );
        mainContent.removeStyleName( ButtonType.DEFAULT.getCssName() );

        Span userIcon = new Span();
        userIcon.addStyleName( "pficon" );
        userIcon.addStyleName( "pficon-user" );

        mainContent.add( userIcon );
        mainContent.add( new Text( formattedUsername() ) );

        // this has to be done last because it causes the dropdown caret to be appended to the content.
        mainContent.setDataToggle( Toggle.DROPDOWN );

        addStyleName( Styles.DROPDOWN );
        add( mainContent );
        add( menu );
    }

    /**
     * Returns the Bootstrap 3 DropDownMenu that appears when this widget is clicked. Can be used for adding, removing,
     * or modifying the existing menu structure.
     *
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
        StringBuilder sb = new StringBuilder();
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
}
