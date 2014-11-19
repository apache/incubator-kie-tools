package org.uberfire.client.views.pfly.menu;

import org.gwtbootstrap3.client.ui.base.ComplexWidget;
import org.gwtbootstrap3.client.ui.constants.Styles;

import com.google.gwt.dom.client.Document;

/**
 * Goes inside the collapsible navbar container, and can contain a status menu (not implemented yet) and the {@link UserMenu}.
 */
public class UtilityNavbar extends ComplexWidget {

    public UtilityNavbar() {
        setElement( Document.get().createElement( "ul" ) );
        addStyleName( Styles.NAV );
        addStyleName( Styles.NAVBAR_NAV );
        addStyleName( "navbar-utility" );
    }

}
