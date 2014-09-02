package org.uberfire.client.workbench.pmgr.nswe.panels.support;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.SplitPanel;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PanelSupport;
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

    @Inject
    private BeanFactory factory;

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

        final SplitPanel splitter = (SplitPanel) parent;

        CompassPosition removalPosition = CompassPosition.NONE;
        for ( CompassPosition searchPosition : new CompassPosition[]{ CompassPosition.NORTH,
                                                                      CompassPosition.SOUTH,
                                                                      CompassPosition.EAST,
                                                                      CompassPosition.WEST } ) {
            if ( view.asWidget().equals( splitter.getWidget( searchPosition ) ) ) {
                removalPosition = searchPosition;
                break;
            }
        }

        if ( removalPosition == CompassPosition.NONE ) {
            return false;
        }

        final Widget splitterParent = splitter.getParent();
        final Widget widgetToKeep = splitter.getWidget( opposite( removalPosition ) );

        splitter.clear();

        if ( splitterParent instanceof SimplePanel ) {
            ( (SimplePanel) splitterParent ).setWidget( widgetToKeep );
        } else {
            System.out.println("Splitter's parent was not a SimplePanel!");
        }

        if ( widgetToKeep instanceof RequiresResize ) {
            scheduleResize( (RequiresResize) widgetToKeep );
        }

        return true;
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
