package org.uberfire.client.workbench.widgets.menu;

/**
 *
 */
public interface WorkbenchMenuBar {

    void setBrandMenuItem( final BrandMenuItem brandMenuItem );

    void aggregateWorkbenchMenus( final Menus menus );

    void aggregatePerspectiveMenus( final Menus menus );

    void clearWorkbenchMenus();

    void clearPerspectiveMenus();

}
