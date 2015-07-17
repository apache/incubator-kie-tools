package org.uberfire.client.views.pfly.menu;

import com.google.gwt.dom.client.Document;
import org.gwtbootstrap3.client.ui.base.ComplexWidget;
import org.gwtbootstrap3.client.ui.constants.Styles;

import javax.enterprise.context.ApplicationScoped;

/**
 * Goes inside the collapsible navbar container, and can contain a status menu (not implemented yet) and the {@link UserMenu}.
 */
@ApplicationScoped
public class UtilityNavbar extends ComplexWidget {

    public UtilityNavbar() {
        setElement( Document.get().createElement( "ul" ) );
        addStyleName( Styles.NAV );
        addStyleName( Styles.NAVBAR_NAV );
        addStyleName( "navbar-utility" );
    }

}
