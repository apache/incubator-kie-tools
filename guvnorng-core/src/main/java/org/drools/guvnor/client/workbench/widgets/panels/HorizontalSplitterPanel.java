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
public class HorizontalSplitterPanel extends ResizeComposite
    implements
    SplitPanel {

    private final ResizableSplitLayoutPanel slp                 = new ResizableSplitLayoutPanel();
    private final ScrollPanel               eastWidgetContainer = new ScrollPanel();
    private final ScrollPanel               westWidgetContainer = new ScrollPanel();

    private CompassDropController           eastDropController;
    private CompassDropController           westDropController;

    private final EventBus                  eventBus;

    public HorizontalSplitterPanel(final EventBus eventBus,
                                   final WorkbenchPanel eastWidget,
                                   final WorkbenchPanel westWidget,
                                   final Position position) {
        this.eventBus = eventBus;
        switch ( position ) {
            case EAST :
                slp.addEast( westWidgetContainer,
                             INITIAL_SIZE );
                slp.add( eastWidgetContainer );
                slp.setWidgetMinSize( westWidgetContainer,
                                      MIN_SIZE );
                break;
            case WEST :
                slp.addWest( eastWidgetContainer,
                             INITIAL_SIZE );
                slp.add( westWidgetContainer );
                slp.setWidgetMinSize( eastWidgetContainer,
                                      MIN_SIZE );
                break;
            default :
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
        }
        westWidgetContainer.setWidget( westWidget );
        eastWidgetContainer.setWidget( eastWidget );

        initWidget( slp );

        //Wire-up DnD controllers
        eastDropController = new CompassDropController( eastWidget,
                                                        eventBus );
        WorkbenchDragAndDropManager.getInstance().registerDropController( eastWidgetContainer,
                                                                          eastDropController );
        westDropController = new CompassDropController( westWidget,
                                                        eventBus );
        WorkbenchDragAndDropManager.getInstance().registerDropController( westWidgetContainer,
                                                                          westDropController );
    }

    @Override
    public void remove(WorkbenchPanel panel) {
        final Widget parent = getParent();
        final Widget eastWidget = getWidget( Position.EAST );
        final Widget westWidget = getWidget( Position.WEST );

        slp.clear();

        WorkbenchDragAndDropManager.getInstance().unregisterDropController( eastWidgetContainer );
        WorkbenchDragAndDropManager.getInstance().unregisterDropController( westWidgetContainer );

        Position positionToDelete = Position.NONE;
        if ( panel == eastWidget ) {
            positionToDelete = Position.EAST;
        } else if ( panel == westWidget ) {
            positionToDelete = Position.WEST;
        }

        switch ( positionToDelete ) {
            case NONE :
                break;
            case EAST :
                //Set parent's content to the WEST widget
                if ( parent instanceof SimplePanel ) {
                    ((SimplePanel) parent).setWidget( westWidget );
                    if ( westWidget instanceof WorkbenchPanel ) {
                        final WorkbenchPanel wbp = (WorkbenchPanel) westWidget;
                        westDropController = new CompassDropController( wbp,
                                                                        eventBus );
                        WorkbenchDragAndDropManager.getInstance().registerDropController( (SimplePanel) parent,
                                                                                          westDropController );
                    }
                }

                if ( westWidget instanceof RequiresResize ) {
                    scheduleResize( (RequiresResize) westWidget );
                }

                break;
            case WEST :
                //Set parent's content to the EAST widget
                if ( parent instanceof SimplePanel ) {
                    ((SimplePanel) parent).setWidget( eastWidget );
                    if ( eastWidget instanceof WorkbenchPanel ) {
                        final WorkbenchPanel wbp = (WorkbenchPanel) eastWidget;
                        eastDropController = new CompassDropController( wbp,
                                                                        eventBus );
                        WorkbenchDragAndDropManager.getInstance().registerDropController( (SimplePanel) parent,
                                                                                          eastDropController );
                    }
                }

                if ( eastWidget instanceof RequiresResize ) {
                    scheduleResize( (RequiresResize) eastWidget );
                }

                break;
            default :
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
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
            case EAST :
                return this.westWidgetContainer.getWidget();
            case WEST :
                return this.eastWidgetContainer.getWidget();
            default :
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
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
