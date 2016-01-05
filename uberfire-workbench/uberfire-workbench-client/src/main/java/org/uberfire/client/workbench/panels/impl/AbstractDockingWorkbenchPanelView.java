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
import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelView;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;
import org.uberfire.client.workbench.widgets.split.WorkbenchSplitLayoutPanel;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view behaviour required by all docking panel views: adding and removing child panels in the NORTH,
 * SOUTH, EAST, and WEST compass positions.
 * <p>
 * <h2>Information for subclassers</h2>
 * The top-level widget of an {@link AbstractDockingWorkbenchPanelView} is always {@link #topLevelWidget}, a container
 * for child panels, even if this panel doesn't currently have any child panels. This is done so child panels can be
 * inserted and removed without making any assumptions about the parent panel this view is located in (if any!).
 * <p>
 * This means you must always put your part view UI into the widget returned from {@link #getPartViewContainer()}. The
 * <i>contents</i> of this container will never be inspected or modified, but the container itself will be reparented as
 * necessary to accommodate child panels being inserted and removed around it. Put another way, <b>do not insert your
 * part view UI directly into the top-level widget of this view! It will get wiped out!</b>
 * <p>
 * This also means you must not call {@link #initWidget(Widget)}. That will always be done by this superclass.
 *
 * @param <P>
 *            the presenter type this view binds to
 */
public abstract class AbstractDockingWorkbenchPanelView<P extends WorkbenchPanelPresenter>
extends AbstractWorkbenchPanelView<P> implements DockingWorkbenchPanelView<P> {

    private final IdentityHashMap<WorkbenchPanelView<?>, WorkbenchSplitLayoutPanel> viewSplitters = new IdentityHashMap<WorkbenchPanelView<?>, WorkbenchSplitLayoutPanel>();

    @Inject
    protected WorkbenchDragAndDropManager dndManager;

    @Inject
    protected BeanFactory factory;

    /**
     * The topmost widget (closest to DOM root) that this panel view manages. Contains either partViewContainer itself
     * (when there are no child panels) or a splitter (when there is at least one child panel).
     */
    @Inject
    private SimpleLayoutPanel topLevelWidget;

    @Inject
    private ResizeFlowPanel partViewContainer;

    @PostConstruct
    void setupDockingPanel() {
        initWidget( topLevelWidget );
        topLevelWidget.add( partViewContainer );
        setToFillParent( topLevelWidget );
        setToFillParent( partViewContainer );
        if ( getPartDropRegion() != null ) {
            dndManager.registerDropController( this, factory.newDropController( this ) );
        }
    }

    @PreDestroy
    private void tearDownDockingPanel() {
        if ( getPartDropRegion() != null ) {
            dndManager.unregisterDropController( this );
        }
    }

    /**
     * Overridden to ensure subclasses don't return the partViewContainer by mistake (this would interfere with nested
     * docking panels).
     */
    // override also helps with unit tests: under GWTMockito, super.getWidget() returns a new mock every time
    @Override
    public final Widget getWidget() {
        return topLevelWidget;
    }

    /**
     * Returns the panel that subclasses should put the part view UI into.
     */
    protected ResizeFlowPanel getPartViewContainer() {
        return partViewContainer;
    }

    /**
     * Returns the partViewContainer, which appears to be the "real" on-screen boundary of this widget.
     */
    @Override
    public Widget getPartDropRegion() {
        return getPartViewContainer();
    }

    /**
     * Overridden to attach the ID to the part container rather than the top-level widget, which may contain sub-panels
     * and be larger and further up the DOM tree than desired.
     */
    @Override
    public void setElementId( String elementId ) {
        if ( elementId == null ) {
            getPartViewContainer().getElement().removeAttribute( "id" );
        } else {
            getPartViewContainer().getElement().setAttribute( "id", elementId );
        }
    }

    @Override
    public void addPanel( final PanelDefinition childPanelDef,
                          final WorkbenchPanelView<?> childPanelView,
                          final Position childPosition ) {

        checkNotNull( "childPanelView", childPanelView );
        CompassPosition position = (CompassPosition) checkNotNull( "childPosition", childPosition );

        if ( viewSplitters.get( position ) != null ) {
            throw new IllegalStateException( "This panel already has a " + position + " child" );
        }

        final WorkbenchSplitLayoutPanel splitPanel = new WorkbenchSplitLayoutPanel();
        splitPanel.add( childPanelView.asWidget(),
                        position,
                        widthOrHeight( position, childPanelDef ) );

        // now reparent our existing part container into the split panel's resizable area
        Widget partContainerParent = partViewContainer.getParent();
        splitPanel.add( partViewContainer );
        ((HasWidgets) partContainerParent).add( splitPanel ); // this is either a WorkbenchSplitLayoutPanel or topLevelWidget (a SimpleLayoutPanel)

        Integer childMinSize = minWidthOrHeight( position, childPanelDef );
        if ( childMinSize != null ) {
            splitPanel.setWidgetMinSize( childPanelView.asWidget(), childMinSize );
        }
        Integer myMinSize = minWidthOrHeight( position, getPresenter().getDefinition() );
        if ( myMinSize != null ) {
            splitPanel.setWidgetMinSize( splitPanel, myMinSize );
        }

        viewSplitters.put( childPanelView, splitPanel );

        //Adding an additional embedded ScrollPanel can cause scroll-bars to disappear
        //so ensure we set the sizes of the new Panel and it's children after the
        //browser has added the new DIVs to the HTML tree. This does occasionally
        //add slight flicker when adding a new Panel.
        scheduleResize( splitPanel );
    }

    @Override
    public boolean removePanel( WorkbenchPanelView<?> childView ) {
        CompassPosition removalPosition = positionOf( childView );
        if ( removalPosition == null ) {
            System.out.println("  remove failed - no such child view");
            return false;
        }

        WorkbenchSplitLayoutPanel splitter = viewSplitters.remove( childView );
        splitter.remove( childView.asWidget() );

        // now search for 'splitter' in all remaining split panels in the map, plus topLevelWidget
        // when found, transfer orphaned children to the same position as splitter was in in its old parent

        Widget orphan = null;
        for ( Widget w : splitter ) {
            if ( orphan != null ) {
                System.out.println("  splitter@" + System.identityHashCode( splitter ) + " LOSING ORPHAN: " + splitter.getWidgetDirection( w ) + " - " + w);
            }
            orphan = w;
        }

        if ( topLevelWidget.getWidget() == splitter ) {
            if ( orphan != null ) {
                topLevelWidget.setWidget( orphan );
            }
        } else {
            for ( Map.Entry<WorkbenchPanelView<?>, WorkbenchSplitLayoutPanel> ent : viewSplitters.entrySet() ) {
                WorkbenchSplitLayoutPanel sp = ent.getValue();
                if ( sp.getWidgetIndex( splitter ) >= 0 ) {
                    Direction d = sp.getWidgetDirection( splitter );
                    Double size = sp.getWidgetSize( splitter );
                    sp.remove( splitter );
                    if ( orphan != null ) {
                        sp.insert( orphan, d, size, null );
                    }
                }
            }
        }

        scheduleResize( partViewContainer );

        return true;
    }

    private static CompassPosition toPosition( Direction d ) {
        if ( d == null ) {
            return null;
        }
        switch ( d ) {
            case NORTH:
                return CompassPosition.NORTH;
            case SOUTH:
                return CompassPosition.SOUTH;
            case EAST:
            case LINE_START:
                return CompassPosition.WEST;
            case WEST:
            case LINE_END:
                return CompassPosition.EAST;
            default:
                throw new IllegalArgumentException( "Unknown direction: " + d );
        }
    }

    private CompassPosition positionOf( WorkbenchPanelView<?> childView ) {
        final WorkbenchSplitLayoutPanel splitter = viewSplitters.get( childView );
        if ( splitter == null ) {
            return null;
        }
        Direction widgetDirection = splitter.getWidgetDirection( childView.asWidget() );
        if ( widgetDirection == null ) {
            throw new AssertionError( "Found child in splitter map but not in the splitter itself" );
        }
        return toPosition( widgetDirection );
    }

    @Override
    public boolean setChildSize( WorkbenchPanelView<?> childPanel, int size ) {
        WorkbenchSplitLayoutPanel splitPanel = viewSplitters.get( childPanel );
        if ( splitPanel != null ) {
            PanelDefinition definition = getPresenter().getDefinition();
            CompassPosition childPosition = positionOf( childPanel );

            Integer childMinSize = minWidthOrHeight( childPosition, definition );
            Integer myMinSize = minWidthOrHeight( childPosition, getPresenter().getDefinition() );
            int mySize = getWidthOrHeight( childPosition, asWidget() );

            if ( childMinSize != null ) {
                size = Math.max( size, childMinSize );
            }
            if ( myMinSize != null ) {
                size = Math.min( size, mySize - myMinSize );
            }

            splitPanel.setWidgetSize( childPanel.asWidget(), size );

            return true;
        }
        return false;
    }

    /**
     * Retrieves the application-requested initial size for a child panel, or calculates a good default based on the
     * available space.
     *
     * @param position
     *            the position the panel will be added within its parent.
     * @param definition
     *            the new panel's definition.
     * @param parent
     *            the widget whose space will be used up by the insertion of the new panel.
     */
    static int initialWidthOrHeight( CompassPosition position, PanelDefinition definition, Widget parent ) {
        Integer requestedSize;
        int availableSize;
        switch ( position ) {
            case NORTH:
            case SOUTH:
                requestedSize = definition.getHeight();
                availableSize = parent.getOffsetHeight();
                break;
            case EAST:
            case WEST:
                requestedSize = definition.getWidth();
                availableSize = parent.getOffsetWidth();
                break;
            default: throw new IllegalArgumentException( "Position " + position + " has no horizontal or vertial aspect." );
        }
        if ( requestedSize == null || requestedSize <= 0 ) {
            return availableSize / 2;
        } else {
            return requestedSize;
        }
    }

    static Integer minWidthOrHeight( CompassPosition position, PanelDefinition definition ) {
        switch ( position ) {
            case NORTH:
            case SOUTH:
                return definition.getMinHeight();
            case EAST:
            case WEST:
                return definition.getMinWidth();
            default: throw new IllegalArgumentException( "Position " + position + " has no horizontal or vertial aspect." );
        }
    }

    private static int getWidthOrHeight( CompassPosition position, Widget w ) {
        switch ( position ) {
            case NORTH:
            case SOUTH:
                return w.getOffsetHeight();
            case EAST:
            case WEST:
                return w.getOffsetWidth();
            default: throw new IllegalArgumentException( "Position " + position + " has no horizontal or vertial aspect." );
        }
    }

    private static void scheduleResize( final RequiresResize widget ) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {
            @Override
            public void execute() {
                widget.onResize();
            }
        } );
    }

    /**
     * Overridden to maximize the widget returned by {@link #getPartViewContainer()}.
     */
    @Override
    public void maximize() {
        layoutSelection.get().maximize( getPartViewContainer() );
    }

    /**
     * Overridden to match {@link #maximize()}.
     */
    @Override
    public void unmaximize() {
        layoutSelection.get().unmaximize( getPartViewContainer() );
    }
}
