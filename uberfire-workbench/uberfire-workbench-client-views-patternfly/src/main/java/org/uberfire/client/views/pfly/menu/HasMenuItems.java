package org.uberfire.client.views.pfly.menu;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.uberfire.workbench.model.menu.MenuPosition;

/**
 * Widgets that can contain menu items implement this interface.
 */
public interface HasMenuItems extends IsWidget {

    /**
     * Adds a new menu item to the end of the current list of menu items at the given position.
     * @param position the position to append the menu item at. Not all menu containers support positioning; those that don't
     * will ignore this parameter. Null is always allowed and should be treated the same as CENTER by
     * position-aware containers.
     * @param menuContent the content that should appear in the given menu item. Should have an Anchor element as its only
     * direct child, or should be an {@link AnchorListItem} which is a convenient shorthand for an Anchor
     * inside a ListItem.
     */
    void addMenuItem( MenuPosition position, Widget menuContent );

}
