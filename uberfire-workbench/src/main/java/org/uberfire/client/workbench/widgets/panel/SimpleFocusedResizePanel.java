package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.workbench.model.PartDefinition;

public class SimpleFocusedResizePanel
        extends ResizeComposite
        implements HasSelectionHandlers<PartDefinition>,
                   HasFocusHandlers {

    interface SimpleFocusedResizePanelBinder
            extends
            UiBinder<Panel, SimpleFocusedResizePanel> {

    }

    private static SimpleFocusedResizePanelBinder uiBinder = GWT.create( SimpleFocusedResizePanelBinder.class );

    @UiField
    RequiresResizeFocusPanel container;

    @UiField
    FlowPanel wrapped;

    @UiField
    SimplePanel title;

    @UiField
    SimplePanel content;

    private PartDefinition partDefinition;

    private WorkbenchDragAndDropManager dndManager;

    public SimpleFocusedResizePanel() {
        initWidget( uiBinder.createAndBindUi( this ) );
        container.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                SelectionEvent.fire( SimpleFocusedResizePanel.this, partDefinition );
            }
        } );
    }

    public void setDndManager( WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
    }

    public void setPart( final WorkbenchPartPresenter.View part ) {
        this.partDefinition = part.getPresenter().getDefinition();
        content.setWidget( part );
        title.add( new Label( part.getPresenter().getTitle() ) );
        dndManager.makeDraggable( part, title );
    }

    public void clear() {
        partDefinition = null;
        content.clear();
    }

    public void setFocus( boolean hasFocus ) {
        if ( hasFocus ) {
            //style
        } else {
            //style
        }
    }

    public void changeTitle( final String title,
                             final IsWidget titleDecoration ) {
        this.title.clear();
        if ( titleDecoration == null ) {
            this.title.add( new Label( title ) );
        } else {
            this.title.add( titleDecoration );
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
            wrapped.setPixelSize( width, height );
            content.setPixelSize( width, height - title.getOffsetHeight() );
            content.getWidget().setPixelSize( width, height - title.getOffsetHeight() );
        }
        super.onResize();
    }

}
