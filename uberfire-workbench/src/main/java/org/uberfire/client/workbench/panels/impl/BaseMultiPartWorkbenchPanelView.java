package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PartDefinition;

public abstract class BaseMultiPartWorkbenchPanelView<P extends BaseMultiPartWorkbenchPanelPresenter>
        extends BaseWorkbenchPanelView<P> {

    private MultiPartWidget widget;

    public BaseMultiPartWorkbenchPanelView() {
        this.widget = setupWidget();
        initWidget( this.widget.asWidget() );
    }

    protected abstract MultiPartWidget setupWidget();

    @PostConstruct
    private void setupDragAndDrop() {
        dndManager.registerDropController( this, factory.newDropController( this ) );
    }

    @Override
    public void init( final P presenter ) {
        this.presenter = presenter;
        widget.setPresenter( presenter );
        widget.setDndManager( dndManager );
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
    public void selectPart( final PartDefinition part ) {
        widget.selectPart( part );
    }

    @Override
    public void removePart( final PartDefinition part ) {
        widget.remove( part );
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        widget.setFocus( hasFocus );
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            setPixelSize( width, height );
            presenter.onResize( width, height );
            widget.onResize();
            super.onResize();
        }
    }
}
