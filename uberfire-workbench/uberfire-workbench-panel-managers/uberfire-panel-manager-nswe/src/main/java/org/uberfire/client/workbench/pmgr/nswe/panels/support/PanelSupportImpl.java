package org.uberfire.client.workbench.pmgr.nswe.panels.support;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PanelSupport;
import org.uberfire.client.workbench.pmgr.nswe.annotations.WorkbenchPosition;
import org.uberfire.client.workbench.pmgr.nswe.panels.impl.HorizontalSplitterPanel;
import org.uberfire.client.workbench.pmgr.nswe.panels.impl.VerticalSplitterPanel;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
public class PanelSupportImpl implements PanelSupport {

    @Inject
    @WorkbenchPosition(position = CompassPosition.NORTH)
    protected PanelHelper helperNorth;

    @Inject
    @WorkbenchPosition(position = CompassPosition.SOUTH)
    protected PanelHelper helperSouth;

    @Inject
    @WorkbenchPosition(position = CompassPosition.EAST)
    protected PanelHelper helperEast;

    @Inject
    @WorkbenchPosition(position = CompassPosition.WEST)
    protected PanelHelper helperWest;

    @Override
    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView newView,
                          final WorkbenchPanelView targetView,
                          final Position position ) {
        switch ( (CompassPosition) position ) {
            case NORTH:
                helperNorth.add( newView,
                                 targetView,
                                 panel.getHeight(),
                                 panel.getMinHeight() );
                break;

            case SOUTH:
                helperSouth.add( newView,
                                 targetView,
                                 panel.getHeight(),
                                 panel.getMinHeight() );
                break;

            case EAST:
                helperEast.add( newView,
                                targetView,
                                panel.getWidth(),
                                panel.getMinWidth() );
                break;

            case WEST:
                helperWest.add( newView,
                                targetView,
                                panel.getWidth(),
                                panel.getMinWidth() );
                break;

            default:
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

    }

    @Override
    public void remove( final AbstractWorkbenchPanelView<?> view,
                        final Widget parent ) {
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

        switch ( position ) {
            case NORTH:
                helperNorth.remove( view );
                break;

            case SOUTH:
                helperSouth.remove( view );
                break;

            case EAST:
                helperEast.remove( view );
                break;

            case WEST:
                helperWest.remove( view );
                break;
        }

    }
}
