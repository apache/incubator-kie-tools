package org.uberfire.client.workbench.panels.impl;

import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * Implements focus and selection handling.
 * @author jfuerth
 *
 * @param <P>
 */
public abstract class AbstractWorkbenchPanelView<P extends WorkbenchPanelPresenter>
extends ResizeComposite
implements WorkbenchPanelView<P> {

    @Inject
    protected PanelManager panelManager;

    @Inject
    protected BeanFactory factory;

    protected P presenter;

    /**
     * Throws {@code UnsupportedOperationException} when called. Subclasses that wish to support child panels should
     * override this and {@link #removePanel(WorkbenchPanelView)}.
     */
    @Override
    public void addPanel( PanelDefinition panel,
                          WorkbenchPanelView<?> view,
                          Position position ) {
        throw new UnsupportedOperationException("This panel does not support child panels");
    }

    /**
     * Throws {@code UnsupportedOperationException} when called. Subclasses that wish to support child panels should
     * override this and {@link #addPanel(PanelDefinition, WorkbenchPanelView, Position)}.
     */
    @Override
    public boolean removePanel( WorkbenchPanelView<?> child ) {
        throw new UnsupportedOperationException("This panel does not support child panels");
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

    protected void addSelectionHandler( HasSelectionHandlers<PartDefinition> widget ) {
        widget.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( final SelectionEvent<PartDefinition> event ) {
                panelManager.onPartLostFocus();
                panelManager.onPartFocus( event.getSelectedItem() );
            }
        } );
    }

    @Override
    public void setElementId( String elementId ) {
        if ( elementId == null ) {
            getElement().removeAttribute( "id" );
        } else {
            getElement().setAttribute( "id", elementId );
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( getClass().getName() );
        sb.append( "@" ).append(  System.identityHashCode( this ) );
        sb.append( " id=" ).append( getElement().getAttribute( "id" ) );
        return sb.toString();
    }

}
