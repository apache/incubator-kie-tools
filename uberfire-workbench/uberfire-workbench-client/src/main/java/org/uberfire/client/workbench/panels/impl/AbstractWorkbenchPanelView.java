package org.uberfire.client.workbench.panels.impl;

import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.support.PanelSupport;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.ResizeComposite;

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
    public boolean removePanel( WorkbenchPanelView<?> childView ) {
        if ( panelSupport.remove( childView, this.getWidget() ) ) {
            dndManager.unregisterDropController( childView );
            return true;
        }
        return false;
    }

    @Override
    public P getPresenter() {
        return this.presenter;
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
                panelManager.onPartLostFocus();
                panelManager.onPartFocus( event.getSelectedItem() );
            }
        } );
    }
}
