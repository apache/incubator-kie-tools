package org.uberfire.client.workbench.panels.impl;

import static org.uberfire.client.workbench.panels.impl.AbstractDockingWorkbenchPanelView.*;

import java.util.Map.Entry;

import javax.enterprise.event.Event;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelView;
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
     * Forwards requests to existing child panels in case there is already a child panel in the requested position.
     * Otherwise behaves exactly like the superclass.
     */
    @Override
    public void addPanel( WorkbenchPanelPresenter newChild,
                          Position position ) {
        WorkbenchPanelPresenter existingChild = getPanels().get( position );
        if ( existingChild != null && newChild instanceof AbstractDockingWorkbenchPanelPresenter ) {
            int existingChildSize = initialWidthOrHeight( (CompassPosition) position, existingChild.getDefinition() );
            int newChildSize = initialWidthOrHeight( (CompassPosition) position, newChild.getDefinition() );

            removePanel( existingChild );
            super.addPanel( newChild, position );
            newChild.addPanel( existingChild, position );

            getPanelView().setChildSize( (DockingWorkbenchPanelView<?>) newChild.getPanelView(),
                                         newChildSize + existingChildSize );
        } else {
            super.addPanel( newChild, position );
        }
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

    @Override
    public DockingWorkbenchPanelView<P> getPanelView() {
        return (DockingWorkbenchPanelView<P>) super.getPanelView();
    }
}
