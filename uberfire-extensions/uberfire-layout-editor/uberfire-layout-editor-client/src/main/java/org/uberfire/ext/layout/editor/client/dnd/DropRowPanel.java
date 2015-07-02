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
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;
import org.uberfire.ext.layout.editor.client.components.InternalDragComponent;

public class DropRowPanel extends FlowPanel {

    private final LayoutEditorWidget parent;

    public DropRowPanel( final LayoutEditorWidget parent ) {
        this.parent = parent;

        Label label = GWT.create(Label.class);
        label.setText("New row ...");
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
        }
        dragLeaveHandler();
    }

    private boolean isInternalDragComponent( DropEvent event ) {
        String dragTypeClassName = event.getData( InternalDragComponent.INTERNAL_DRAG_COMPONENT );
        return dragTypeClassName != null;
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

    private void handleGridDrop( DropEvent event ) {
        String grid = event.getData( InternalDragComponent.INTERNAL_DRAG_COMPONENT );
        if ( isAGridDrop( grid ) ) {
            parent.getWidget().remove( this );
            parent.getWidget().add( createRowView( grid ) );
            parent.getWidget().add( createDropRowPanel() );
        }
    }

    private DropRowPanel createDropRowPanel() {
        return new DropRowPanel( parent );
    }

    private RowView createRowView( String grid ) {
        return new RowView( parent, grid );
    }

    private boolean isAGridDrop( String grid ) {
        return grid != null && !grid.isEmpty();
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