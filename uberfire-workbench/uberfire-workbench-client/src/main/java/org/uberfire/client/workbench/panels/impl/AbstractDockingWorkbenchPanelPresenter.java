/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.panels.impl;

import static org.uberfire.client.util.Layouts.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelPresenter;
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
extends AbstractWorkbenchPanelPresenter<P> implements DockingWorkbenchPanelPresenter {

    public AbstractDockingWorkbenchPanelPresenter( WorkbenchPanelView<P> view,
                                                   PerspectiveManager perspectiveManager ) {
        super( view,
               perspectiveManager );
    }

    /**
     * Forwards requests to existing child panels in case there is already a child panel in the requested position.
     * Otherwise behaves exactly like the superclass.
     */
    @Override
    public void addPanel( WorkbenchPanelPresenter newChild,
                          Position position ) {
        if ( getParent() instanceof DockingWorkbenchPanelPresenter ) {
            DockingWorkbenchPanelPresenter dockingParent = (DockingWorkbenchPanelPresenter) getParent();
            if ( dockingParent.getPanels().get( position ) == this ) {
                dockingParent.setChildSize( this,
                                            widthOrDefault( newChild.getDefinition() ) + widthOrDefault( getDefinition() ),
                                            heightOrDefault( newChild.getDefinition() ) + heightOrDefault( getDefinition() ) );
            }
        }
        WorkbenchPanelPresenter existingChild = getPanels().get( position );
        if ( existingChild != null && newChild instanceof AbstractDockingWorkbenchPanelPresenter ) {
            int existingChildSize = widthOrHeight( (CompassPosition) position, existingChild.getDefinition() );
            int newChildSize = widthOrHeight( (CompassPosition) position, newChild.getDefinition() );

            removePanel( existingChild );
            super.addPanel( newChild, position );
            newChild.addPanel( existingChild, position );

            getPanelView().setChildSize( newChild.getPanelView(),
                                         newChildSize + existingChildSize );
        } else {
            super.addPanel( newChild, position );
        }
    }

    /**
     * Checks for existing child panels of the panel to be removed, and reparents them to this panel in the position of
     * the child panel. Once the child panels are safely out of the way, the actual panel removal is done by a call to
     * super.removePanel().
     */
    @Override
    public boolean removePanel( WorkbenchPanelPresenter child ) {
        if ( child instanceof AbstractDockingWorkbenchPanelPresenter ) {

            Position removedPosition = positionOf( child );
            if ( removedPosition == null ) {
                return false;
            }

            List<AbstractDockingWorkbenchPanelPresenter<?>> rescuedOrphans = new ArrayList<AbstractDockingWorkbenchPanelPresenter<?>>();
            AbstractDockingWorkbenchPanelPresenter<?> dockingChild = (AbstractDockingWorkbenchPanelPresenter<?>) child;
            for ( Map.Entry<Position, WorkbenchPanelPresenter> entry : dockingChild.getPanels().entrySet() ) {
                dockingChild.removeWithoutOrphanRescue( entry.getValue() );
                rescuedOrphans.add( (AbstractDockingWorkbenchPanelPresenter<?>) entry.getValue() );
                // TODO multiple off-axis orphans need special treatment
                // for example: if the NORTH panel has EAST and WEST children, we need to take the EAST one as a direct child
                // to the NORTH of us, and reparent the other to the WEST of that one (or vice-versa)
                // on the other hand, if there was only one EAST or WEST child, we can just stick it in as our new NORTH child
            }
            super.removePanel( dockingChild );
            for ( AbstractDockingWorkbenchPanelPresenter<?> rescued : rescuedOrphans ) {
                addPanel( rescued, removedPosition );
            }

            return true;

        } else {
            return super.removePanel( child );
        }
    }

    /**
     * Removes the given child panel without modifying child attachments at all. This is used d
     * @param child
     */
    private boolean removeWithoutOrphanRescue( WorkbenchPanelPresenter child ) {
        return super.removePanel( child );
    }

    @Override
    public boolean setChildSize( WorkbenchPanelPresenter child,
                                 Integer pixelWidth,
                                 Integer pixelHeight ) {
        for ( Map.Entry<Position, WorkbenchPanelPresenter> e : getPanels().entrySet() ) {
            if ( e.getValue() == child ) {
                int size;
                if ( e.getKey() == CompassPosition.NORTH || e.getKey() == CompassPosition.SOUTH ) {
                    if ( pixelHeight == null ) {
                        return false;
                    }
                    size = pixelHeight + nestedPanelHeights( child );
                } else if ( e.getKey() == CompassPosition.EAST || e.getKey() == CompassPosition.WEST ) {
                    if ( pixelWidth == null ) {
                        return false;
                    }
                    size = pixelWidth + nestedPanelWidths( child );
                } else {
                    throw new AssertionError( "Unexpected child position: " + e.getKey() );
                }

                getPanelView().setChildSize( child.getPanelView(), size );
                return true;
            }
        }
        return false;
    }

    private int nestedPanelHeights( WorkbenchPanelPresenter child ) {
        int totalHeight = 0;
        WorkbenchPanelPresenter northChild = child.getPanels().get( CompassPosition.NORTH );
        if ( northChild != null ) {
            totalHeight += northChild.getDefinition().getHeight();
            totalHeight += nestedPanelHeights( northChild );
        }
        WorkbenchPanelPresenter southChild = child.getPanels().get( CompassPosition.SOUTH );
        if ( southChild != null ) {
            totalHeight += southChild.getDefinition().getHeight();
            totalHeight += nestedPanelHeights( southChild );
        }
        return totalHeight;
    }

    private int nestedPanelWidths( WorkbenchPanelPresenter child ) {
        int totalWidth = 0;
        WorkbenchPanelPresenter westChild = child.getPanels().get( CompassPosition.WEST );
        if ( westChild != null ) {
            totalWidth += westChild.getDefinition().getWidth();
            totalWidth += nestedPanelWidths( westChild );
        }
        WorkbenchPanelPresenter eastChild = child.getPanels().get( CompassPosition.EAST );
        if ( eastChild != null ) {
            totalWidth += eastChild.getDefinition().getWidth();
            totalWidth += nestedPanelWidths( eastChild );
        }
        return totalWidth;
    }

    @Inject
    private PanelManager panelManager;

    @Override
    public boolean removePart( PartDefinition part ) {
        if ( super.removePart( part ) ) {
            final PanelDefinition panelDef = getDefinition();

            // if we are not the root and we have become empty, we remove ourselves from the panel hierarchy,
            // preserving all child panels
            if ( panelDef.getParts().isEmpty() && getParent() != null ) {
                panelManager.removeWorkbenchPanel( this.getDefinition() );
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
