package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.DragArea;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.workbench.model.PartDefinition;

public class SimpleFocusedResizePanel
        extends Composite
        implements RequiresResize,
                   HasSelectionHandlers<PartDefinition>,
                   HasFocusHandlers {

    interface SimpleFocusedResizePanelBinder
            extends
            UiBinder<FlowPanel, SimpleFocusedResizePanel> {

    }

    private static SimpleFocusedResizePanelBinder uiBinder = GWT.create( SimpleFocusedResizePanelBinder.class );

    @UiField
    SimplePanel title;

    @UiField
    FlowPanel header;

    @UiField
    SimplePanel content;

    private PartDefinition partDefinition;

    private boolean isDndEnabled = false;

    private WorkbenchDragAndDropManager dndManager;

    public SimpleFocusedResizePanel() {
        initWidget( uiBinder.createAndBindUi( this ) );
//        container.addClickHandler( new ClickHandler() {
//            @Override
//            public void onClick( final ClickEvent event ) {
//                SelectionEvent.fire( SimpleFocusedResizePanel.this, partDefinition );
//            }
//        } );
    }

    public void setDndManager( WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
    }

    public void enableDnd() {
        this.isDndEnabled = true;
    }

    public void setPart( final WorkbenchPartPresenter.View part ) {
        this.partDefinition = part.getPresenter().getDefinition();
        content.setWidget( part );

        final Widget _title = buildTitle( part.getPresenter().getTitle() );
        title.add( _title );

        if ( isDndEnabled ) {
            dndManager.makeDraggable( part, _title );
        }
    }

    private Widget buildTitle( final String title ) {
        final SpanElement spanElement = Document.get().createSpanElement();
        spanElement.setInnerText( title );

        return new DragArea() {{
            add( spanElement );
        }};
    }

    public void clear() {
        partDefinition = null;
        content.clear();
        title.clear();
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
        //this.title.setText( title );
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<PartDefinition> handler ) {
        return addHandler( handler, SelectionEvent.getType() );
    }

    @Override
    public HandlerRegistration addFocusHandler( FocusHandler handler ) {
//        return container.addFocusHandler( handler );
        return null;
    }

    @Override
    public void onResize() {
        if ( isAttached() ) {
            final int width = getParent().getOffsetWidth();
            final int height = getParent().getOffsetHeight();
            setPixelSize( width, height );

            content.setPixelSize( width, height - title.getOffsetHeight() );
            if ( content.getWidget() != null ) {
                content.getWidget().setPixelSize( width, height - title.getOffsetHeight() );
            }
        }
    }

}
