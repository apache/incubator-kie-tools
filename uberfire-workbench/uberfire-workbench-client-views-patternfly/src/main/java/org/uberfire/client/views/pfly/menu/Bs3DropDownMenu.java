package org.uberfire.client.views.pfly.menu;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Span;
import org.uberfire.workbench.model.menu.MenuPosition;

/**
 * A container for menu items. The contents are initially hidden. They appear when the title text is clicked.
 */
public class Bs3DropDownMenu extends AnchorListItem implements HasMenuItems {

    DropDownMenu items = new DropDownMenu();

    public Bs3DropDownMenu(String text) {
        anchor.setText( text );
        anchor.setDataToggle( Toggle.DROPDOWN );

        Span caret = new Span();
        caret.addStyleName( Styles.CARET );
        anchor.add( caret );

        add( items );
    }

    @Override
    public void addMenuItem( MenuPosition ignored, AbstractListItem listItem ) {
        items.add( listItem );
    }

    @Override
    public int getMenuItemCount() {
        return items.getWidgetCount();
    }

}
