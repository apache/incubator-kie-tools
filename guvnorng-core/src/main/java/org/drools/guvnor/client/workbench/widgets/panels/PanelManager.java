/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.workbench.widgets.panels;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.guvnor.client.workbench.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.WorkbenchPart;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class PanelManager {

    private static PanelManager INSTANCE        = new PanelManager();

    private final PanelHelper   helperNorth     = new PanelHelperNorth();
    private final PanelHelper   helperSouth     = new PanelHelperSouth();
    private final PanelHelper   helperEast      = new PanelHelperEast();
    private final PanelHelper   helperWest      = new PanelHelperWest();

    private WorkbenchPanel      focusPanel      = null;

    private Set<WorkbenchPanel> workbenchPanels = new HashSet<WorkbenchPanel>();

    private PanelManager() {
    }

    public static PanelManager getInstance() {
        return INSTANCE;
    }

    public void addWorkbenchPanel(final WorkbenchPart part,
                                  final Position position) {
        assertFocusPanel();
        addWorkbenchPanel( part,
                           this.focusPanel,
                           position );
    }

    public void addWorkbenchPanel(final WorkbenchPart part,
                                  final WorkbenchPanel targetPanel,
                                  final Position position) {

        workbenchPanels.add( targetPanel );

        if ( position == Position.SELF ) {
            targetPanel.addTab( part );
            return;
        }

        final WorkbenchPanel newPanel = new WorkbenchPanel( part );
        workbenchPanels.add( newPanel );

        switch ( position ) {
            case NORTH :
                helperNorth.add( newPanel,
                                 targetPanel );
                break;

            case SOUTH :
                helperSouth.add( newPanel,
                                 targetPanel );
                break;

            case EAST :
                helperEast.add( newPanel,
                                targetPanel );
                break;

            case WEST :
                helperWest.add( newPanel,
                                targetPanel );
                break;
        }
        setFocus( newPanel );
    }

    public void removeWorkbenchPanel(final WorkbenchPanel panel) {

        //Find the position that needs to be deleted
        Position position = Position.NONE;
        final Widget parent = panel.getParent().getParent().getParent();
        if ( parent instanceof HorizontalSplitterPanel ) {
            final HorizontalSplitterPanel hsp = (HorizontalSplitterPanel) parent;
            if ( panel.equals( hsp.getWidget( Position.EAST ) ) ) {
                position = Position.EAST;
            } else if ( panel.equals( hsp.getWidget( Position.WEST ) ) ) {
                position = Position.WEST;
            }
        } else if ( parent instanceof VerticalSplitterPanel ) {
            final VerticalSplitterPanel vsp = (VerticalSplitterPanel) parent;
            if ( panel.equals( vsp.getWidget( Position.NORTH ) ) ) {
                position = Position.NORTH;
            } else if ( panel.equals( vsp.getWidget( Position.SOUTH ) ) ) {
                position = Position.SOUTH;
            }
        }

        switch ( position ) {
            case NORTH :
                helperNorth.remove( panel );
                workbenchPanels.remove( panel );
                break;

            case SOUTH :
                helperSouth.remove( panel );
                workbenchPanels.remove( panel );
                break;

            case EAST :
                helperEast.remove( panel );
                workbenchPanels.remove( panel );
                break;

            case WEST :
                helperWest.remove( panel );
                workbenchPanels.remove( panel );
                break;
        }

        if ( this.focusPanel == panel ) {
            this.focusPanel = null;
            assertFocusPanel();
            setFocus( this.focusPanel );
        }
    }

    public void setFocus(final WorkbenchPanel panel) {
        for ( WorkbenchPanel wbp : this.workbenchPanels ) {
            wbp.setFocus( wbp == panel );
        }
        this.focusPanel = panel;
    }

    private void assertFocusPanel() {
        if ( this.focusPanel == null ) {
            final Iterator<WorkbenchPanel> iterator = this.workbenchPanels.iterator();
            if ( iterator.hasNext() ) {
                this.focusPanel = iterator.next();
            }
        }
        if ( this.focusPanel == null ) {
            throw new UnsupportedOperationException( "Unable to find a root WorkbenchPanel." );
        }
    }

    public void removeWorkbenchPart(WorkbenchPart workbenchPart) {
        for ( WorkbenchPanel workbenchPanel : workbenchPanels ) {
            if ( workbenchPanel.contains( workbenchPart ) ) {
                workbenchPanel.remove( workbenchPart );
                return;
            }
        }
    }

}
