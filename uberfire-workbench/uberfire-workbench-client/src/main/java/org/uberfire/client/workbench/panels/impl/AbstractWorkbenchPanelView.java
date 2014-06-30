package org.uberfire.client.workbench.panels.impl;

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.SplitPanel;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PanelSupport;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

public abstract class AbstractWorkbenchPanelView<P extends WorkbenchPanelPresenter>
        extends ResizeComposite
        implements WorkbenchPanelView<P> {

    @Inject
    private PanelSupport panelSupport;

    @Inject
    protected WorkbenchDragAndDropManager dndManager;

    @Inject
    protected PanelManager panelManager;

    @Inject
    protected BeanFactory factory;

    protected P presenter;

    @Override
    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position ) {

        panelSupport.addPanel( panel, view, this, position );
    }

    @Override
    public void removePanel() {
        final Widget parent = this.asWidget().getParent().getParent().getParent();

        panelSupport.remove( this, parent );

        //Release DnD DropController
        dndManager.unregisterDropController( this );
    }

    @Override
    public P getPresenter() {
        return this.presenter;
    }

    protected void resizeParent( final Widget widget ) {
        if ( widget instanceof SplitPanel ) {
            scheduleResize( (RequiresResize) widget );
            return;
        } else if ( widget.getParent() != null && widget.getParent() instanceof RequiresResize ) {
            resizeParent( widget.getParent() );
        } else if ( widget instanceof RequiresResize ) {
            scheduleResize( (RequiresResize) widget );
        }
    }

    protected void scheduleResize( final RequiresResize widget ) {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                widget.onResize();
            }
        } );
    }

    protected void addOnFocusHandler( MultiPartWidget widget ) {
        widget.addOnFocusHandler( new Command() {
            @Override
            public void execute() {
                panelManager.onPanelFocus( presenter.getDefinition() );
            }
        } );
    }

    protected void addSelectionHandler( HasSelectionHandlers widget ) {
        widget.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( final SelectionEvent<PartDefinition> event ) {
                presenter.onPartLostFocus();
                presenter.onPartFocus( event.getSelectedItem() );
            }
        } );
    }
}
