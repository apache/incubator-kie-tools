package org.uberfire.client.workbench.panels.impl;

import java.util.Map.Entry;

import javax.enterprise.event.Event;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

/**
 * Implements the behaviour for panel presenters that support adding child panels in {@link CompassPosition} positions.
 */
public abstract class AbstractDockingWorkbenchPanelPresenter<P extends AbstractWorkbenchPanelPresenter<P>>
extends AbstractWorkbenchPanelPresenter<P>  {

    public AbstractDockingWorkbenchPanelPresenter( WorkbenchPanelView<P> view,
                                                   PerspectiveManager perspectiveManager,
                                                   Event<MaximizePlaceEvent> maximizePanelEvent ) {
        super( view,
               perspectiveManager,
               maximizePanelEvent );
    }

    /**
     * Splices out this panel from the panel tree, preserving all child panels by reparenting them to our parent panel.
     * Their Position in the parent panel will be the same as it was in this panel.
     */
    private void spliceOutOfHierarchy() {
        final WorkbenchPanelPresenter parent = getParent();

        parent.removePanel( this );

        for ( Entry<Position, WorkbenchPanelPresenter> child : getPanels().entrySet() ) {
            Position childPosition = child.getKey();
            WorkbenchPanelPresenter childPresenter = child.getValue();

            removePanel( childPresenter );
            parent.addPanel( childPresenter, childPosition );
        }
    }

    @Override
    public boolean removePart( PartDefinition part ) {
        if ( super.removePart( part ) ) {
            final PanelDefinition panelDef = getDefinition();

            // if we are not the root and we have become empty, we remove ourselves from the panel hierarchy,
            // preserving all child panels
            if ( panelDef.getParts().isEmpty() && getParent() != null ) {
                spliceOutOfHierarchy();
            }
            return true;
        }
        return false;
    }

}
