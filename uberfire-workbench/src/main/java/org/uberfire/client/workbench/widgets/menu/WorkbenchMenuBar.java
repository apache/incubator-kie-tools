package org.uberfire.client.workbench.widgets.menu;

import org.uberfire.workbench.model.menu.BrandMenuItem;
import org.uberfire.workbench.model.menu.Menus;

/**
 *
 */
public interface WorkbenchMenuBar {

    void setBrandMenuItem( final BrandMenuItem brandMenuItem );

    void addMenus( final Menus menus );

    void clear();

}
