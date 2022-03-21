package org.uberfire.client.docks.view.items;

import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

public abstract class AbstractSideDockItem extends AbstractDockItem {

    AbstractSideDockItem(UberfireDock dock) {
        super(dock);
    }

    void configureTooltip(Tooltip itemTooltip,
                          final String text) {
        itemTooltip.setTitle(text);
        itemTooltip.setContainer("body");
        if (this.getDock().getDockPosition() == UberfireDockPosition.EAST) {
            itemTooltip.setPlacement(Placement.LEFT);
        } else if (this.getDock().getDockPosition() == UberfireDockPosition.WEST) {
            itemTooltip.setPlacement(Placement.RIGHT);
        }
    }
}
