package org.uberfire.client.workbench.panels.impl;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.annotations.WorkbenchPosition;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PanelHelper;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

public abstract class BaseWorkbenchPanelView<P extends WorkbenchPanelPresenter>
        extends ResizeComposite
        implements WorkbenchPanelView<P> {

    @Inject
    @WorkbenchPosition(position = Position.NORTH)
    protected PanelHelper helperNorth;

    @Inject
    @WorkbenchPosition(position = Position.SOUTH)
    protected PanelHelper helperSouth;

    @Inject
    @WorkbenchPosition(position = Position.EAST)
    protected PanelHelper helperEast;

    @Inject
    @WorkbenchPosition(position = Position.WEST)
    protected PanelHelper helperWest;

    @Inject
    protected WorkbenchDragAndDropManager dndManager;

    @Inject
    protected PanelManager panelManager;

    @Inject
    protected BeanFactory factory;

    protected P presenter;

    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position ) {

        switch ( position ) {
            case NORTH:
                helperNorth.add( view,
                                 this,
                                 panel.getHeight(),
                                 panel.getMinHeight() );
                break;

            case SOUTH:
                helperSouth.add( view,
                                 this,
                                 panel.getHeight(),
                                 panel.getMinHeight() );
                break;

            case EAST:
                helperEast.add( view,
                                this,
                                panel.getWidth(),
                                panel.getMinWidth() );
                break;

            case WEST:
                helperWest.add( view,
                                this,
                                panel.getWidth(),
                                panel.getMinWidth() );
                break;

            default:
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

    }

    @Override
    public void removePanel() {
        //Find the position that needs to be deleted
        Position position = Position.NONE;
        final WorkbenchPanelView view = this;
        final Widget parent = view.asWidget().getParent().getParent().getParent();
        if ( parent instanceof HorizontalSplitterPanel ) {
            final HorizontalSplitterPanel hsp = (HorizontalSplitterPanel) parent;
            if ( view.asWidget().equals( hsp.getWidget( Position.EAST ) ) ) {
                position = Position.EAST;
            } else if ( view.asWidget().equals( hsp.getWidget( Position.WEST ) ) ) {
                position = Position.WEST;
            }
        } else if ( parent instanceof VerticalSplitterPanel ) {
            final VerticalSplitterPanel vsp = (VerticalSplitterPanel) parent;
            if ( view.asWidget().equals( vsp.getWidget( Position.NORTH ) ) ) {
                position = Position.NORTH;
            } else if ( view.asWidget().equals( vsp.getWidget( Position.SOUTH ) ) ) {
                position = Position.SOUTH;
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

        //Release DnD DropController
        dndManager.unregisterDropController( this );
    }

    @Override
    public P getPresenter() {
        return this.presenter;
    }

}
