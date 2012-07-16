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
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.guvnor.client.workbench.BeanFactory;
import org.drools.guvnor.client.workbench.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.WorkbenchPart;
import org.drools.guvnor.client.workbench.annotations.WorkbenchPosition;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartDroppedEvent;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
@ApplicationScoped
public class PanelManager {

    @Inject
    @WorkbenchPosition(position = Position.NORTH)
    private PanelHelper                       helperNorth;

    @Inject
    @WorkbenchPosition(position = Position.SOUTH)
    private PanelHelper                       helperSouth;

    @Inject
    @WorkbenchPosition(position = Position.EAST)
    private PanelHelper                       helperEast;

    @Inject
    @WorkbenchPosition(position = Position.WEST)
    private PanelHelper                       helperWest;

    @Inject
    private BeanFactory                       factory;

    @Inject
    private Event<WorkbenchPanelOnFocusEvent> workbenchPanelOnFocusEvent;

    private WorkbenchPanel                    focusPanel      = null;

    private WorkbenchPanel                    rootPanel       = null;

    private Set<WorkbenchPanel>               workbenchPanels = new HashSet<WorkbenchPanel>();

    public void setRoot(final WorkbenchPanel panel) {
        this.rootPanel = panel;
        workbenchPanels.add( panel );
        scheduleResize( panel );
        setFocus( panel );
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

        WorkbenchPanel newPanel = factory.newWorkbenchPanel( part );

        switch ( position ) {
            case SELF :
                newPanel = targetPanel;
                newPanel.addTab( part );
                break;

            case ROOT :
                newPanel = rootPanel;
                newPanel.addTab( part );
                break;

            case NORTH :
                workbenchPanels.add( newPanel );
                helperNorth.add( newPanel,
                                 targetPanel );
                break;

            case SOUTH :
                workbenchPanels.add( newPanel );
                helperSouth.add( newPanel,
                                 targetPanel );
                break;

            case EAST :
                workbenchPanels.add( newPanel );
                helperEast.add( newPanel,
                                targetPanel );
                break;

            case WEST :
                workbenchPanels.add( newPanel );
                helperWest.add( newPanel,
                                targetPanel );
                break;
        }

        setFocus( newPanel );
    }

    public void removeWorkbenchPanel(final WorkbenchPanel panel) {

        //The root WorkbenchPanel cannot be removed
        if ( panel == rootPanel ) {
            return;
        }

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
                factory.release( panel );
                break;

            case SOUTH :
                helperSouth.remove( panel );
                workbenchPanels.remove( panel );
                factory.release( panel );
                break;

            case EAST :
                helperEast.remove( panel );
                workbenchPanels.remove( panel );
                factory.release( panel );
                break;

            case WEST :
                helperWest.remove( panel );
                workbenchPanels.remove( panel );
                factory.release( panel );
                break;
        }

        if ( this.focusPanel == panel ) {
            this.focusPanel = null;
            assertFocusPanel();
            setFocus( this.focusPanel );
        }
    }

    private void setFocus(final WorkbenchPanel panel) {
        workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( panel ) );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPanelOnFocus(@Observes WorkbenchPanelOnFocusEvent event) {
        final WorkbenchPanel panel = event.getWorkbenchPanel();
        this.focusPanel = panel;
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartClosedEvent(@Observes WorkbenchPartCloseEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        removeWorkbenchPart( part );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartDroppedEvent(@Observes WorkbenchPartDroppedEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        removeWorkbenchPart( part );
    }

    private void assertFocusPanel() {
        if ( this.focusPanel == null ) {
            this.focusPanel = rootPanel;
        }
    }

    private void removeWorkbenchPart(WorkbenchPart workbenchPart) {
        for ( WorkbenchPanel workbenchPanel : workbenchPanels ) {
            if ( workbenchPanel.contains( workbenchPart ) ) {
                workbenchPanel.remove( workbenchPart );
                factory.release( workbenchPart );
                return;
            }
        }
    }

    private void scheduleResize(final RequiresResize widget) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                widget.onResize();
            }

        } );
    }

}
