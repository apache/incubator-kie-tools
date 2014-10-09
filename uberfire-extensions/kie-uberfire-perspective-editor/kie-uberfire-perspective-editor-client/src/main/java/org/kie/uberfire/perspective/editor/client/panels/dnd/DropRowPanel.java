package org.kie.uberfire.perspective.editor.client.panels.dnd;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import org.kie.uberfire.perspective.editor.client.panels.row.RowView;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditorUI;
import org.kie.uberfire.perspective.editor.client.util.DragType;

public class DropRowPanel extends FlowPanel {

    private final PerspectiveEditorUI parent;
    private final int parentIndex;

    public DropRowPanel( final PerspectiveEditorUI parent ) {
        this.parent = parent;
        this.parentIndex = parent.getWidget().getWidgetIndex( this );
        setSize( "100%", "20px" );
        addDragOverHandler( new DragOverHandler() {
            @Override
            public void onDragOver( DragOverEvent event ) {
                addDropBorder();
            }
        } );
        addDragLeaveHandler( new DragLeaveHandler() {
            @Override
            public void onDragLeave( DragLeaveEvent event ) {
                removeDropBorder();
            }
        } );
        addDropHandler( new DropHandler() {
            @Override
            public void onDrop( DropEvent event ) {
                event.preventDefault();

                if ( isAGridDrop( event ) ) {
                    String gridData = event.getData( DragType.GRID.name() );
                    handleGridDrop( gridData );
                }
                removeDropBorder();
            }
        } );
    }

    private void addDropBorder() {
        getElement().getStyle().setBorderStyle( Style.BorderStyle.SOLID );
        getElement().getStyle().setBorderColor( "Red" );
        getElement().getStyle().setBorderWidth( 1, Style.Unit.PX );
    }

    private void removeDropBorder() {
        getElement().getStyle().setBorderStyle( Style.BorderStyle.NONE );
    }

    private boolean isAGridDrop( DropEvent event ) {
        return !event.getData( DragType.GRID.name() ).isEmpty();
    }

    private void handleGridDrop( String grid ) {
        parent.getWidget().remove( this );
        parent.getWidget().add( new RowView( parent, grid ) );
        parent.getWidget().add( new DropRowPanel( parent ) );
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