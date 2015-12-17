/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SelectedCellChangeEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetModelEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.ColumnResizeEvent;

/**
 * Abstract grid, decorated with DecoratedGridHeaderWidget and
 * DecoratedGridSidebarWidget encapsulating basic operation: keyboard navigation
 * and column resizing.
 * @param <M> The domain model represented by the Grid
 * @param <T> The type of domain columns represented by the Grid
 * @param <C> The type of domain cell represented by the Grid
 */
public abstract class AbstractDecoratedGridWidget<M, T, C> extends Composite
        implements
        ColumnResizeEvent.Handler,
        SelectedCellChangeEvent.Handler,
        DeleteRowEvent.Handler,
        InsertRowEvent.Handler,
        AppendRowEvent.Handler,
        SetModelEvent.Handler<M> {

    // Widgets for UI
    protected Panel mainPanel;
    protected Panel bodyPanel;
    protected ScrollPanel scrollPanel;
    protected AbstractMergableGridWidget<M, T> gridWidget;
    protected AbstractDecoratedGridHeaderWidget<M, T> headerWidget;
    protected AbstractDecoratedGridSidebarWidget<M, T> sidebarWidget;

    protected int height;
    protected int width;

    // Resources
    protected final ResourcesProvider<T> resources;

    //Event Bus
    protected final EventBus eventBus;

    /**
     * Construct at empty DecoratedGridWidget, without DecoratedGridHeaderWidget
     * or DecoratedGridSidebarWidget These should be set before the grid is
     * displayed using setHeaderWidget and setSidebarWidget respectively.
     */
    public AbstractDecoratedGridWidget( ResourcesProvider<T> resources,
                                        EventBus eventBus,
                                        Panel mainPanel,
                                        Panel bodyPanel,
                                        AbstractMergableGridWidget<M, T> gridWidget,
                                        AbstractDecoratedGridHeaderWidget<M, T> headerWidget,
                                        AbstractDecoratedGridSidebarWidget<M, T> sidebarWidget ) {

        if ( resources == null ) {
            throw new IllegalArgumentException( "resources cannot be null" );
        }
        if ( eventBus == null ) {
            throw new IllegalArgumentException( "eventBus cannot be null" );
        }
        if ( mainPanel == null ) {
            throw new IllegalArgumentException( "mainPanel cannot be null" );
        }
        if ( bodyPanel == null ) {
            throw new IllegalArgumentException( "bodyPanel cannot be null" );
        }
        if ( gridWidget == null ) {
            throw new IllegalArgumentException( "gridWidget cannot be null" );
        }
        if ( headerWidget == null ) {
            throw new IllegalArgumentException( "headerWidget cannot be null" );
        }
        if ( sidebarWidget == null ) {
            throw new IllegalArgumentException( "sidebarWidget cannot be null" );
        }

        this.resources = resources;
        this.eventBus = eventBus;
        this.mainPanel = mainPanel;
        this.bodyPanel = bodyPanel;
        this.gridWidget = gridWidget;
        this.headerWidget = headerWidget;
        this.sidebarWidget = sidebarWidget;

        //The height of the sidebar controls the height of the resize divider
        this.headerWidget.setSidebar( this.sidebarWidget );

        scrollPanel = new ScrollPanel();
        scrollPanel.add( gridWidget );
        scrollPanel.addScrollHandler( getScrollHandler() );

        initialiseHeaderWidget();
        initialiseSidebarWidget();

        initWidget( mainPanel );

        //Wire-up event handlers
        eventBus.addHandler( DeleteRowEvent.TYPE,
                             this );
        eventBus.addHandler( InsertRowEvent.TYPE,
                             this );
        eventBus.addHandler( AppendRowEvent.TYPE,
                             this );
        eventBus.addHandler( SelectedCellChangeEvent.TYPE,
                             this );
    }

    /**
     * Resize the DecoratedGridHeaderWidget and DecoratedGridSidebarWidget when
     * DecoratedGridWidget shows scrollbars
     */
    protected void assertDimensions() {
        headerWidget.setWidth( scrollPanel.getElement().getClientWidth()
                                       + "px" );
        sidebarWidget.setHeight( scrollPanel.getElement().getClientHeight()
                                         + "px" );
    }

    /**
     * Return the ScrollPanel in which the DecoratedGridWidget "grid" is nested.
     * This allows ScrollEvents to be hooked up to other defendant controls
     * (e.g. the Header).
     * @return
     */
    protected abstract ScrollHandler getScrollHandler();

    //Initialise the Header Widget and attach resize handlers to GridWidget to support
    //column resizing and to resize GridWidget's ScrollPanel when header resizes.
    private void initialiseHeaderWidget() {
        eventBus.addHandler( ColumnResizeEvent.TYPE,
                             this );
        this.headerWidget.addResizeHandler( new ResizeHandler() {

            public void onResize( ResizeEvent event ) {
                final int scrollPanelHeight = height - event.getHeight();
                if ( scrollPanelHeight > 0 ) {
                    scrollPanel.setHeight( scrollPanelHeight + "px" );
                    assertDimensions();
                }
            }
        } );
        bodyPanel.add( headerWidget );
        bodyPanel.add( scrollPanel );
    }

    //Set the SidebarWidget and attach a ResizeEvent handler to the Sidebar for when the header changes 
    //size and the Sidebar needs to be redrawn to align correctly. Also attach a RowGroupingChangeEvent 
    //handler to the MergableGridWidget so the Sidebar can redraw itself when rows are merged, grouped, 
    //ungrouped or unmerged.
    private void initialiseSidebarWidget() {
        this.headerWidget.addResizeHandler( new ResizeHandler() {

            public void onResize( ResizeEvent event ) {
                sidebarWidget.resizeSidebar( event.getHeight() );
            }

        } );

        this.mainPanel.add( sidebarWidget );
        this.mainPanel.add( bodyPanel );
    }

    /**
     * This should be used instead of setHeight(String) and setWidth(String) as
     * various child Widgets of the DecisionTable need to have their sizes set
     * relative to the outermost Widget (i.e. this).
     */
    @Override
    public void setPixelSize( int width,
                              int height ) {
        if ( width < 0 ) {
            throw new IllegalArgumentException( "width cannot be less than zero" );
        }
        if ( height < 0 ) {
            throw new IllegalArgumentException( "height cannot be less than zero" );
        }
        super.setPixelSize( width,
                            height );
        this.height = height;
        setHeight( height );
        setWidth( width );
    }

    //Ensure the selected cell is visible
    private void cellSelected( AbstractMergableGridWidget.CellSelectionDetail ce ) {

        //No selection
        if ( ce == null ) {
            return;
        }

        //Left extent
        if ( ce.getOffsetX() < scrollPanel.getHorizontalScrollPosition() ) {
            scrollPanel.setHorizontalScrollPosition( ce.getOffsetX() );
        }

        //Right extent
        int scrollWidth = scrollPanel.getElement().getClientWidth();
        if ( ce.getOffsetX() + ce.getWidth() > scrollWidth + scrollPanel.getHorizontalScrollPosition() ) {
            int delta = ce.getOffsetX() + ce.getWidth() - scrollPanel.getHorizontalScrollPosition() - scrollWidth;
            scrollPanel.setHorizontalScrollPosition( scrollPanel.getHorizontalScrollPosition() + delta );
        }

        //Top extent
        if ( ce.getOffsetY() < scrollPanel.getVerticalScrollPosition() ) {
            scrollPanel.setVerticalScrollPosition( ce.getOffsetY() );
        }

        //Bottom extent
        int scrollHeight = scrollPanel.getElement().getClientHeight();
        if ( ce.getOffsetY() + ce.getHeight() > scrollHeight + scrollPanel.getVerticalScrollPosition() ) {
            int delta = ce.getOffsetY() + ce.getHeight() - scrollPanel.getVerticalScrollPosition() - scrollHeight;
            scrollPanel.setVerticalScrollPosition( scrollPanel.getVerticalScrollPosition() + delta );
        }

    }

    // Set height of outer most Widget and related children
    private void setHeight( final int height ) {
        mainPanel.setHeight( height
                                     + "px" );

        // The Sidebar and Header sizes are derived from the ScrollPanel
        Scheduler.get().scheduleFinally( new ScheduledCommand() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    // Set width of outer most Widget and related children
    private void setWidth( int width ) {
        mainPanel.setWidth( width
                                    + "px" );
        scrollPanel.setWidth( ( width - resources.sidebarWidth() )
                                      + "px" );

        // The Sidebar and Header sizes are derived from the ScrollPanel
        Scheduler.get().scheduleFinally( new ScheduledCommand() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    public void onColumnResize( final ColumnResizeEvent event ) {
        Scheduler.get().scheduleDeferred( new Command() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    public void onSelectedCellChange( SelectedCellChangeEvent event ) {
        cellSelected( event.getCellSelectionDetail() );
    }

    public void onDeleteRow( DeleteRowEvent event ) {
        Scheduler.get().scheduleDeferred( new Command() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    public void onInsertRow( InsertRowEvent event ) {
        Scheduler.get().scheduleDeferred( new Command() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

    public void onAppendRow( AppendRowEvent event ) {
        Scheduler.get().scheduleDeferred( new Command() {

            public void execute() {
                assertDimensions();
            }

        } );
    }

}
