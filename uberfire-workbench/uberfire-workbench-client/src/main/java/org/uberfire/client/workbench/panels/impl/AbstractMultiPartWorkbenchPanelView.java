package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.ContextPanel;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;

public abstract class AbstractMultiPartWorkbenchPanelView<P extends AbstractMultiPartWorkbenchPanelPresenter>
extends AbstractDockingWorkbenchPanelView<P> {

    protected ContextPanel contextWidget = new ContextPanel();
    protected MultiPartWidget widget;

    protected abstract MultiPartWidget setupWidget();

    @PostConstruct
    private void setupDragAndDrop() {
        widget = setupWidget();
        widget.asWidget().getElement().getStyle().setOverflow( Style.Overflow.HIDDEN );
        Layouts.setToFillParent( widget.asWidget() );
        getPartViewContainer().add( contextWidget );
        getPartViewContainer().add( widget );
        dndManager.registerDropController( this, factory.newDropController( this ) );
    }

    @PreDestroy
    private void tearDownDragAndDrop() {
        dndManager.unregisterDropController( this );
    }

    @Override
    public void init( final P presenter ) {
        this.presenter = presenter;
        widget.setPresenter( presenter );
        widget.setDndManager( dndManager );
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
        return widget.selectPart( part );
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        if ( widget.remove( part ) ) {
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
