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

    public void addWorkbenchPanel(final String title,
                                  final Position position,
                                  final Widget widget) {
        addWorkbenchPanel( title,
                           this.focusPanel,
                           position,
                           widget );
    }

    public void addWorkbenchPanel(final String title,
                                  final WorkbenchPanel target,
                                  final Position position,
                                  final Widget widget) {

        if ( position == Position.SELF ) {
            target.addTab( widget,
                           title );
            return;
        }

        final WorkbenchPanel wbp = new WorkbenchPanel( widget,
                                                       title );
        workbenchPanels.add( wbp );

        switch ( position ) {
            case NORTH :
                helperNorth.add( wbp,
                                 target );
                break;

            case SOUTH :
                helperSouth.add( wbp,
                                 target );
                break;

            case EAST :
                helperEast.add( wbp,
                                target );
                break;

            case WEST :
                helperWest.add( wbp,
                                target );
                break;
        }
    }

    public void removeWorkbenchPanel(final WorkbenchPanel target) {

        workbenchPanels.remove( target );

        //Find the position that needs to be deleted
        Position position = Position.NONE;
        final Widget parent = target.getParent().getParent().getParent();
        if ( parent instanceof HorizontalSplitterPanel ) {
            final HorizontalSplitterPanel hsp = (HorizontalSplitterPanel) parent;
            if ( target.equals( hsp.getWidget( Position.EAST ) ) ) {
                position = Position.EAST;
            } else if ( target.equals( hsp.getWidget( Position.WEST ) ) ) {
                position = Position.WEST;
            }
        } else if ( parent instanceof VerticalSplitterPanel ) {
            final VerticalSplitterPanel vsp = (VerticalSplitterPanel) parent;
            if ( target.equals( vsp.getWidget( Position.NORTH ) ) ) {
                position = Position.NORTH;
            } else if ( target.equals( vsp.getWidget( Position.SOUTH ) ) ) {
                position = Position.SOUTH;
            }
        }

        switch ( position ) {
            case NORTH :
                helperNorth.remove( target );
                break;

            case SOUTH :
                helperSouth.remove( target );
                break;

            case EAST :
                helperEast.remove( target );
                break;

            case WEST :
                helperWest.remove( target );
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
