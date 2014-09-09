package org.uberfire.client.workbench.panels.impl;

import static org.uberfire.client.util.Layouts.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.IdentityHashMap;

import javax.annotation.PostConstruct;
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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view behaviour required by all docking panel views: adding and removing child panels in the NORTH,
 * SOUTH, EAST, and WEST compass positions.
 * <p>
 * <h2>Information for subclassers</h2>
 * The top-level widget of an {@link AbstractDockingWorkbenchPanelView} is always a container for child panels, even if
 * this panel doesn't currently have any child panels. This is done so child panels can be inserted and removed without
 * making any assumptions about the parent widget this view is located in (if any!).
 * <p>
 * This means you need to put your part view UI into the widget returned from {@link #getPartViewContainer()}. This
 * container's contents will never be inspected or disturbed, but the container itself will be shuffled around between
 * intermediate parent panels as necessary to accommodate child panels being inserted and removed. <b>You must not
 * insert your part view UI directly into the top-level widget of this view!</b>
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

    @Inject
    private SimpleLayoutPanel topLevelWidget;

    @Inject
    private ResizeFlowPanel partViewContainer;

    @PostConstruct
    void setupWidgetLayout() {
        initWidget( topLevelWidget );
        topLevelWidget.add( partViewContainer );
        setToFillParent( topLevelWidget );
        setToFillParent( partViewContainer );
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

    @Override
    public void addPanel( final PanelDefinition childPanelDef,
                          final WorkbenchPanelView<?> childPanelView,
                          final Position childPosition ) {
        checkNotNull( "childPanelView", childPanelView );
        CompassPosition position = (CompassPosition) checkNotNull( "childPosition", childPosition );

        final WorkbenchSplitLayoutPanel splitPanel = new WorkbenchSplitLayoutPanel();
        // TODO this can be moved into an add(CompassPosition, size) method on WorkbenchSplitLayoutPanel
        switch ( position ) {
            case NORTH:
                splitPanel.addNorth( childPanelView, childPanelDef.getHeight() );
                break;
            case SOUTH:
                splitPanel.addSouth( childPanelView, childPanelDef.getHeight() );
                break;
            case EAST:
                splitPanel.addEast( childPanelView, childPanelDef.getWidth() );
                break;
            case WEST:
                splitPanel.addWest( childPanelView, childPanelDef.getWidth() );
                break;
            default:
                throw new IllegalArgumentException( "Bad child position: " + position );
        }

        // now reparent all our existing contents into the split panel's resizable area
        // (note that it could contain other split panels already)
        Widget previousContents = topLevelWidget.getWidget();
        splitPanel.add( previousContents );
        topLevelWidget.setWidget( splitPanel );

        Integer childMinSize = minWidthOrHeight( position, childPanelDef );
        if ( childMinSize != null ) {
            splitPanel.setWidgetMinSize( childPanelView.asWidget(), childMinSize );
        }
        Integer myMinSize = minWidthOrHeight( position, getPresenter().getDefinition() );
        if ( myMinSize != null ) {
            splitPanel.setWidgetMinSize( previousContents, myMinSize );
        }

        viewSplitters.put( childPanelView, splitPanel );

        //Adding an additional embedded ScrollPanel can cause scroll-bars to disappear
        //so ensure we set the sizes of the new Panel and it's children after the
        //browser has added the new DIVs to the HTML tree. This does occasionally
        //add slight flicker when adding a new Panel.
        scheduleResize( splitPanel );
    }

//    private SplitPanel newSplitterPanel( final PanelDefinition panel,
//                                         final WorkbenchPanelView<?> newView,
//                                         final WorkbenchPanelView<?> targetView,
//                                         final CompassPosition fixedSizePosition ) {
//        switch ( fixedSizePosition ) {
//            case NORTH:
//                return factory.newVerticalSplitterPanel( newView,
//                                                         targetView,
//                                                         fixedSizePosition,
//                                                         initialWidthOrHeight( fixedSizePosition, panel ),
//                                                         minWidthOrHeight( fixedSizePosition, panel ) );
//            case SOUTH:
//                return factory.newVerticalSplitterPanel( targetView,
//                                                         newView,
//                                                         fixedSizePosition,
//                                                         initialWidthOrHeight( fixedSizePosition, panel ),
//                                                         minWidthOrHeight( fixedSizePosition, panel ) );
//            case EAST:
//                return factory.newHorizontalSplitterPanel( newView,
//                                                           targetView,
//                                                           fixedSizePosition,
//                                                           initialWidthOrHeight( fixedSizePosition, panel ),
//                                                           minWidthOrHeight( fixedSizePosition, panel ) );
//            case WEST:
//                return factory.newHorizontalSplitterPanel( targetView,
//                                                           newView,
//                                                           fixedSizePosition,
//                                                           initialWidthOrHeight( fixedSizePosition, panel ),
//                                                           minWidthOrHeight( fixedSizePosition, panel ) );
//            default: throw new IllegalArgumentException( "Position " + fixedSizePosition + " has no horizontal or vertial aspect." );
//        }
//    }

    @Override
    public boolean removePanel( WorkbenchPanelView<?> childView ) {
        System.out.println("view.removePanel(): parent=" + asWidget().getElement().getId() +
                           "; child=" + childView.asWidget().getElement().getId());

        CompassPosition removalPosition = positionOf( childView );
        if ( removalPosition == null ) {
            return false;
        }

        // FIXME this is cleanup for stuff subclasses set up on their own.
        // there is no guarantee it is necessary or sufficient
        dndManager.unregisterDropController( childView );

        WorkbenchSplitLayoutPanel splitter = viewSplitters.remove( childView );
        topLevelWidget.setWidget( partViewContainer );

        if ( splitter.iterator().hasNext() ) {
            System.out.println( "Warning: removed split panel still contains the following children:");
            for ( Widget w : splitter ) {
                System.out.println( "  - " + w );
            }
        }
        scheduleResize( partViewContainer );

        return true;
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
        switch ( widgetDirection ) {
            case NORTH:
                return CompassPosition.NORTH;
            case SOUTH:
                return CompassPosition.SOUTH;
            case LINE_END:
            case EAST:
                return CompassPosition.EAST;
            case LINE_START:
            case WEST:
                return CompassPosition.WEST;
            case CENTER:
                throw new IllegalStateException( "Child panels can't end up in the center position" );
            default:
                throw new IllegalStateException( "Unknown direction for child widget: " + widgetDirection );
        }
    }

    @Override
    public boolean setChildSize( DockingWorkbenchPanelView<?> childPanel, int size ) {
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

    private static CompassPosition opposite( CompassPosition position ) {
        switch ( position ) {
            case NORTH: return CompassPosition.SOUTH;
            case SOUTH: return CompassPosition.NORTH;
            case EAST: return CompassPosition.WEST;
            case WEST: return CompassPosition.EAST;
            default: throw new IllegalArgumentException( "Position " + position + " has no opposite." );
        }
    }

    static Integer initialWidthOrHeight( CompassPosition position, PanelDefinition definition ) {
        switch ( position ) {
            case NORTH:
            case SOUTH:
                return definition.getHeight();
            case EAST:
            case WEST:
                return definition.getWidth();
            default: throw new IllegalArgumentException( "Position " + position + " has no horizontal or vertial aspect." );
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

}
