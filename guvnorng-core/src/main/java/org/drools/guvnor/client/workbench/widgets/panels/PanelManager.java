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

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.WorkbenchPart;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class PanelManager {

    private static PanelManager  INSTANCE        = new PanelManager();

    private final PanelHelper    helperNorth     = new PanelHelperNorth();
    private final PanelHelper    helperSouth     = new PanelHelperSouth();
    private final PanelHelper    helperEast      = new PanelHelperEast();
    private final PanelHelper    helperWest      = new PanelHelperWest();

    private WorkbenchPanel       focusPanel      = null;

    private List<WorkbenchPanel> workbenchPanels = new ArrayList<WorkbenchPanel>();

    private PanelManager() {
    }

    public static PanelManager getInstance() {
        return INSTANCE;
    }

    public void addWorkbenchPanel(final WorkbenchPart part,
                                  final Position position) {
        addWorkbenchPanel( part,
                           this.focusPanel,
                           position );
    }

    public void addWorkbenchPanel(final WorkbenchPart part,
                                  final WorkbenchPanel panel,
                                  final Position position) {

        if ( position == Position.SELF ) {
            panel.addTab( part );
            return;
        }

        workbenchPanels.add( panel );

        switch ( position ) {
            case NORTH :
                helperNorth.add( part,
                                 panel );
                break;

            case SOUTH :
                helperSouth.add( part,
                                 panel );
                break;

            case EAST :
                helperEast.add( part,
                                panel );
                break;

            case WEST :
                helperWest.add( part,
                                panel );
                break;
        }
    }

    public void removeWorkbenchPanel(final WorkbenchPanel panel) {

        workbenchPanels.remove( panel );

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
                break;

            case SOUTH :
                helperSouth.remove( panel );
                break;

            case EAST :
                helperEast.remove( panel );
                break;

            case WEST :
                helperWest.remove( panel );
                break;
        }
    }

    public void setFocus(final WorkbenchPanel panel) {
        if ( !this.workbenchPanels.contains( panel ) ) {
            this.workbenchPanels.add( panel );
        }
        for ( WorkbenchPanel wbp : this.workbenchPanels ) {
            wbp.setFocus( wbp == panel );
        }
        this.focusPanel = panel;
    }

}
