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

import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.widgets.dnd.CompassDropController;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class VerticalSplitterPanel extends ResizeComposite
    implements
    SplitPanel {

    private final ResizableSplitLayoutPanel slp                  = new ResizableSplitLayoutPanel();
    private final ScrollPanel               northWidgetContainer = new ScrollPanel();
    private final ScrollPanel               southWidgetContainer = new ScrollPanel();

    private CompassDropController           northDropController;
    private CompassDropController           southDropController;

    private final EventBus                  eventBus;

    public VerticalSplitterPanel(final EventBus eventBus,
                                 final WorkbenchPanel northWidget,
                                 final WorkbenchPanel southWidget,
                                 final Position position) {
        this.eventBus = eventBus;
        switch ( position ) {
            case NORTH :
                slp.addNorth( northWidgetContainer,
                              INITIAL_SIZE );
                slp.add( southWidgetContainer );
                slp.setWidgetMinSize( northWidgetContainer,
                                      MIN_SIZE );
                break;
            case SOUTH :
                slp.addSouth( southWidgetContainer,
                              INITIAL_SIZE );
                slp.add( northWidgetContainer );
                slp.setWidgetMinSize( southWidgetContainer,
                                      MIN_SIZE );
                break;
            default :
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
        }
        northWidgetContainer.setWidget( northWidget );
        southWidgetContainer.setWidget( southWidget );

        initWidget( slp );

        //Wire-up DnD controllers
        northDropController = new CompassDropController( northWidget,
                                                         eventBus );
        WorkbenchDragAndDropManager.getInstance().registerDropController( northWidgetContainer,
                                                                          northDropController );
        southDropController = new CompassDropController( southWidget,
                                                         eventBus );
        WorkbenchDragAndDropManager.getInstance().registerDropController( southWidgetContainer,
                                                                          southDropController );
    }

    @Override
    public void remove(WorkbenchPanel panel) {
        final Widget parent = getParent();
        final Widget northWidget = getWidget( Position.NORTH );
        final Widget southWidget = getWidget( Position.SOUTH );

        slp.clear();

        WorkbenchDragAndDropManager.getInstance().unregisterDropController( northWidgetContainer );
        WorkbenchDragAndDropManager.getInstance().unregisterDropController( southWidgetContainer );

        Position positionToDelete = Position.NONE;
        if ( panel == northWidget ) {
            positionToDelete = Position.NORTH;
        } else if ( panel == southWidget ) {
            positionToDelete = Position.SOUTH;
        }

        switch ( positionToDelete ) {
            case NONE :
                break;
            case NORTH :
                //Set parent's content to the SOUTH widget
                if ( parent instanceof SimplePanel ) {
                    ((SimplePanel) parent).setWidget( southWidget );
                    if ( southWidget instanceof WorkbenchPanel ) {
                        final WorkbenchPanel wbp = (WorkbenchPanel) southWidget;
                        southDropController = new CompassDropController( wbp,
                                                                         eventBus );
                        WorkbenchDragAndDropManager.getInstance().registerDropController( (SimplePanel) parent,
                                                                                          southDropController );
                    }
                }

                if ( southWidget instanceof RequiresResize ) {
                    scheduleResize( (RequiresResize) southWidget );
                }

                break;
            case SOUTH :
                //Set parent's content to the NORTH widget
                if ( parent instanceof SimplePanel ) {
                    ((SimplePanel) parent).setWidget( northWidget );
                    if ( northWidget instanceof WorkbenchPanel ) {
                        final WorkbenchPanel wbp = (WorkbenchPanel) northWidget;
                        final CompassDropController northDropController = new CompassDropController( wbp,
                                                                                                     eventBus );
                        WorkbenchDragAndDropManager.getInstance().registerDropController( (SimplePanel) parent,
                                                                                          northDropController );
                    }
                }

                if ( northWidget instanceof RequiresResize ) {
                    scheduleResize( (RequiresResize) northWidget );
                }

                break;
            default :
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
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

    @Override
    public Widget getWidget(Position position) {
        switch ( position ) {
            case NORTH :
                return this.northWidgetContainer.getWidget();
            case SOUTH :
                return this.southWidgetContainer.getWidget();
            default :
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
        }
    }

    @Override
    public void onResize() {
        Widget parent = getParent();
        int width = parent.getElement().getOffsetWidth();
        int height = parent.getElement().getOffsetHeight();
        this.getElement().getStyle().setWidth( width,
                                               Unit.PX );
        this.getElement().getStyle().setHeight( height,
                                                Unit.PX );
        super.onResize();
    }

}
