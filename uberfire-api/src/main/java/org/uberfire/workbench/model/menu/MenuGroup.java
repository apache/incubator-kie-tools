package org.uberfire.workbench.model.menu;

import java.util.List;

/**
 * A menu item that has child items nested under it.
 */
public interface MenuGroup
        extends MenuItem {

    public List<MenuItem> getItems();

}
