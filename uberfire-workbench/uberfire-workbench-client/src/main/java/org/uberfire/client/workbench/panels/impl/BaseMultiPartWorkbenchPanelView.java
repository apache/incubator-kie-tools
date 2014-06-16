package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;

import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.ContextPanel;
import org.uberfire.client.workbench.widgets.panel.RequiresResizeFlowPanel;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

public abstract class BaseMultiPartWorkbenchPanelView<P extends AbstractMultiPartWorkbenchPanelPresenter>
extends BaseWorkbenchPanelView<P> {

    protected RequiresResizeFlowPanel container = new RequiresResizeFlowPanel();
    protected ContextPanel contextWidget = new ContextPanel();
    protected MultiPartWidget widget;

    protected abstract MultiPartWidget setupWidget();

    @PostConstruct
    private void setupDragAndDrop() {
        widget = setupWidget();
        widget.asWidget().getElement().getStyle().setOverflow( Style.Overflow.HIDDEN );
        container.getElement().getStyle().setPosition( Style.Position.ABSOLUTE );
        container.getElement().getStyle().setTop( 0, Unit.PX );
        container.getElement().getStyle().setBottom( 0, Unit.PX );
        container.getElement().getStyle().setLeft( 0, Unit.PX );
        container.getElement().getStyle().setRight( 0, Unit.PX );
        container.add( contextWidget );
        container.add( widget );
        initWidget( container );
        dndManager.registerDropController( this, factory.newDropController( this ) );
    }

    @Override
    public void init( final P presenter ) {
        this.presenter = presenter;
        widget.setPresenter( presenter );
        widget.setDndManager( dndManager );
        setupContext( null );
    }

    @Override
    public void clear() {
        widget.clear();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        widget.addPart( view );
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        widget.changeTitle( part, title, titleDecoration );
    }

    @Override
    public boolean selectPart( final PartDefinition part ) {
        setupContext( part );
        return widget.selectPart( part );
    }

    private void setupContext( final PartDefinition part ) {
        final ContextActivity context = presenter.resolveContext( part );
        if ( context != null ) {
            final UIPart contextUiPart = new UIPart( context.getTitle(), context.getTitleDecoration(), context.getWidget() );
            if ( contextWidget.getUiPart() == null ) {
                context.onAttach( presenter.getDefinition() );
                contextWidget.setUiPart( contextUiPart );
            } else if ( !contextUiPart.getWidget().equals( contextWidget.getUiPart().getWidget() ) ) {
                context.onAttach( presenter.getDefinition() );
                contextWidget.setUiPart( contextUiPart );
            }
        } else {
            contextWidget.setUiPart( null );
        }
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        if ( widget.remove( part ) ) {
            if ( widget.getPartsSize() == 0 ) {
                setupContext( null );
            }
            return true;
        }
        return false;
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        widget.setFocus( hasFocus );
    }

    @Override
    public void onResize() {
        presenter.onResize( getOffsetWidth(), getOffsetHeight() );
        super.onResize();
    }

}
