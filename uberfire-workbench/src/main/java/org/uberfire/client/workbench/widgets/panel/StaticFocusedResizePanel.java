package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ResizeComposite;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PartDefinition;

public class StaticFocusedResizePanel
        extends ResizeComposite
        implements HasSelectionHandlers<PartDefinition>,
                   HasFocusHandlers {

    private RequiresResizeFocusPanel container = new RequiresResizeFocusPanel();

    private PartDefinition partDefinition;

    public StaticFocusedResizePanel() {
        initWidget( container );
        container.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                if ( partDefinition != null ) {
                    SelectionEvent.fire( StaticFocusedResizePanel.this, partDefinition );
                }
            }
        } );
    }

    public void setPart( final WorkbenchPartPresenter.View part ) {
        this.partDefinition = part.getPresenter().getDefinition();
        container.setWidget( part );
    }

    public void clear() {
        partDefinition = null;
        container.clear();
    }

    public void setFocus( boolean hasFocus ) {
        if ( hasFocus ) {
            //style
        } else {
            //style
        }
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<PartDefinition> handler ) {
        return addHandler( handler, SelectionEvent.getType() );
    }

    @Override
    public HandlerRegistration addFocusHandler( FocusHandler handler ) {
        return container.addFocusHandler( handler );
    }

    public void onResize() {
        if ( isAttached() ) {
            final int width = getParent().getOffsetWidth();
            final int height = getParent().getOffsetHeight();
            setPixelSize( width, height );

            container.setPixelSize( width, height );
            if ( container.getWidget() != null ) {
                container.getWidget().setPixelSize( width, height );
            }
        }
        super.onResize();
    }

}
