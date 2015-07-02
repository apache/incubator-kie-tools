package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.Label;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;
import org.uberfire.ext.layout.editor.client.components.InternalDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public class DropColumnPanel extends FlowPanel {

    private final ColumnEditorWidget parent;

    public DropColumnPanel( final ColumnEditorWidget parent ) {
        super();
        this.parent = parent;

        Label label = GWT.create(Label.class);
        label.setText("Column");
        this.add(label);

        addCSSClass(WebAppResource.INSTANCE.CSS().dropInactive());
        addDragOverHandler( new DragOverHandler() {
            @Override
            public void onDragOver( DragOverEvent event ) {
                dragOverHandler();
            }
        } );
        addDragLeaveHandler( new DragLeaveHandler() {
            @Override
            public void onDragLeave( DragLeaveEvent event ) {
                dragLeaveHandler();
            }
        } );
        addDropHandler( new DropHandler() {
            @Override
            public void onDrop( DropEvent event ) {
                dropHandler( event );
            }
        } );
    }

    void dropHandler( DropEvent event ) {
        event.preventDefault();

        if ( isInternalDragComponent( event ) ) {
            handleGridDrop( event );
        } else {
            handleExternalLayoutDragComponent( event );
        }
        dragLeaveHandler();
    }

    private boolean isInternalDragComponent( DropEvent event ) {
        String dragTypeClassName = event.getData( InternalDragComponent.INTERNAL_DRAG_COMPONENT );
        return dragTypeClassName != null && !dragTypeClassName.isEmpty();
    }

    private void handleExternalLayoutDragComponent( DropEvent event ) {
        String dragTypeClassName = event.getData( LayoutDragComponent.class.toString() );
        LayoutDragComponent layoutDragComponent = getLayoutDragComponent( dragTypeClassName );
        if ( layoutDragComponent != null ) {
            handleLayoutDrop( layoutDragComponent );
        }
    }

    LayoutDragComponent getLayoutDragComponent( String dragTypeClassName ) {
        return new DragTypeBeanResolver().lookupDragTypeBean( dragTypeClassName );
    }

    void dragOverHandler() {
        removeCSSClass(WebAppResource.INSTANCE.CSS().dropInactive());
        addCSSClass(WebAppResource.INSTANCE.CSS().dropBorder());
    }

    void addCSSClass( String className ) {
        getElement().addClassName( className );
    }

    void dragLeaveHandler() {
        removeCSSClass(WebAppResource.INSTANCE.CSS().dropBorder());
        addCSSClass(WebAppResource.INSTANCE.CSS().dropInactive());
    }

    void removeCSSClass( String className ) {
        getElement().removeClassName( className );
    }

    private void handleLayoutDrop( LayoutDragComponent layoutDragComponent ) {
        parent.getWidget().remove( this );
        parent.getWidget().add( new LayoutComponentView( parent, layoutDragComponent, true ) );
    }

    private void handleGridDrop( DropEvent event ) {
        String grid = event.getData( InternalDragComponent.INTERNAL_DRAG_COMPONENT );
        parent.getWidget().remove( this );
        parent.getWidget().add( new RowView( parent, grid, this ) );
    }

    private HandlerRegistration addDropHandler( DropHandler handler ) {
        return addBitlessDomHandler( handler, DropEvent.getType() );
    }

    private HandlerRegistration addDragOverHandler( DragOverHandler handler ) {
        return addBitlessDomHandler( handler, DragOverEvent.getType() );
    }

    private HandlerRegistration addDragLeaveHandler( DragLeaveHandler handler ) {
        return addBitlessDomHandler( handler, DragLeaveEvent.getType() );
    }

}