package org.uberfire.client.workbench.pmgr.nswe.panels.support;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.SplitPanel;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PanelSupport;
import org.uberfire.client.workbench.pmgr.nswe.panels.impl.HorizontalSplitterPanel;
import org.uberfire.client.workbench.pmgr.nswe.panels.impl.VerticalSplitterPanel;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
public class PanelSupportImpl implements PanelSupport {

    private final Map<CompassPosition, AbstractPanelHelper> helpers = new HashMap<CompassPosition, AbstractPanelHelper>();

    @Inject
    private BeanFactory factory;

    @PostConstruct
    private void setup() {
        helpers.put( CompassPosition.NORTH, new PanelHelperNorth( factory ) );
        helpers.put( CompassPosition.SOUTH, new PanelHelperSouth( factory ) );
        helpers.put( CompassPosition.EAST, new PanelHelperEast( factory ) );
        helpers.put( CompassPosition.WEST, new PanelHelperWest( factory ) );
    }

    @Override
    public IsWidget addPanel( final PanelDefinition panel,
                            final WorkbenchPanelView newView,
                            final WorkbenchPanelView targetView,
                            final CompassPosition position ) {

        final Widget parent = targetView.asWidget().getParent();

        if ( parent instanceof SimplePanel ) {

            final SimplePanel oldParent = (SimplePanel) parent;
            final SplitPanel splitter = newSplitterPanel( panel,
                                                          newView,
                                                          targetView,
                                                          position );
            oldParent.clear();
            oldParent.setWidget( splitter );

            //Adding an additional embedded ScrollPanel can cause scroll-bars to disappear
            //so ensure we set the sizes of the new Panel and it's children after the
            //browser has added the new DIVs to the HTML tree. This does occasionally
            //add slight flicker when adding a new Panel.
            scheduleResize( splitter );

            return splitter;
        }
        return parent;
    }

    private SplitPanel newSplitterPanel( final PanelDefinition panel,
                                                      final WorkbenchPanelView newView,
                                                      final WorkbenchPanelView targetView,
                                                      final CompassPosition position ) {
        switch ( position ) {
            case NORTH:
                return factory.newVerticalSplitterPanel( newView,
                                                         targetView,
                                                         position,
                                                         initialWidthOrHeight( position, panel ),
                                                         minWidthOrHeight( position, panel ) );
            case SOUTH:
                return factory.newVerticalSplitterPanel( targetView,
                                                         newView,
                                                         position,
                                                         initialWidthOrHeight( position, panel ),
                                                         minWidthOrHeight( position, panel ) );
            case EAST:
                return factory.newHorizontalSplitterPanel( newView,
                                                           targetView,
                                                           position,
                                                           initialWidthOrHeight( position, panel ),
                                                           minWidthOrHeight( position, panel ) );
            case WEST:
                return factory.newHorizontalSplitterPanel( targetView,
                                                           newView,
                                                           position,
                                                           initialWidthOrHeight( position, panel ),
                                                           minWidthOrHeight( position, panel ) );
            default: throw new IllegalArgumentException( "Position " + position + " has no horizontal or vertial aspect." );
        }
    }

    @Override
    public boolean remove( final WorkbenchPanelView<?> view,
                           final IsWidget parent ) {

        CompassPosition position = CompassPosition.NONE;

        if ( parent instanceof HorizontalSplitterPanel ) {
            final HorizontalSplitterPanel hsp = (HorizontalSplitterPanel) parent;
            if ( view.asWidget().equals( hsp.getWidget( CompassPosition.EAST ) ) ) {
                position = CompassPosition.EAST;
            } else if ( view.asWidget().equals( hsp.getWidget( CompassPosition.WEST ) ) ) {
                position = CompassPosition.WEST;
            }
        } else if ( parent instanceof VerticalSplitterPanel ) {
            final VerticalSplitterPanel vsp = (VerticalSplitterPanel) parent;
            if ( view.asWidget().equals( vsp.getWidget( CompassPosition.NORTH ) ) ) {
                position = CompassPosition.NORTH;
            } else if ( view.asWidget().equals( vsp.getWidget( CompassPosition.SOUTH ) ) ) {
                position = CompassPosition.SOUTH;
            }
        }

        getHelper( position ).remove( view );

        return position != CompassPosition.NONE;
    }

    /**
     * Returns a helper for the given position, or throws an exception if not helper exists for that position type.
     */
    private AbstractPanelHelper getHelper( CompassPosition position ) {
        AbstractPanelHelper helper = helpers.get( position );
        if ( helper == null ) {
            throw new IllegalArgumentException( "Unhandled Position: " + position );
        }
        return helper;
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

    private static Integer initialWidthOrHeight( CompassPosition position, PanelDefinition definition ) {
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

    private static Integer minWidthOrHeight( CompassPosition position, PanelDefinition definition ) {
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

    private static void scheduleResize( final RequiresResize widget ) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {
            @Override
            public void execute() {
                widget.onResize();
            }
        } );
    }

}
