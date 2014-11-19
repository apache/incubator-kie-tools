package org.uberfire.client.views.pfly.menu;

import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.uberfire.workbench.model.menu.MenuPosition;


public class Bs3NavPillsMenuBar extends NavPills implements HasMenuItems {


    @Override
    public void addMenuItem( MenuPosition ignored, AbstractListItem item ) {
        add( item );
    }

    @Override
    public int getMenuItemCount() {
        return getWidgetCount();
    }

}
