package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;
import org.uberfire.ext.layout.editor.client.components.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.InternalDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public class DragGridElement extends Composite {

    private LayoutDragComponent type;

    @UiField
    InputGroup move;

    public DragGridElement( LayoutDragComponent type ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.type = type;
        build();
    }

    private void build() {
        createComponentWidget();
        createMoveIcon( type );
    }

    private void createMoveIcon( final LayoutDragComponent type ) {
        move.setTitle( CommonConstants.INSTANCE.DragAndDrop() );
        move.addDomHandler( new DragStartHandler() {
            @Override
            public void onDragStart( DragStartEvent event ) {
                createDragStart( event, type );
            }
        }, DragStartEvent.getType() );

        move.getElement().setDraggable( Element.DRAGGABLE_TRUE );
    }

    void createDragStart( DragStartEvent event,
                          LayoutDragComponent type ) {
        if ( type instanceof InternalDragComponent ) {
            event.setData(InternalDragComponent.INTERNAL_DRAG_COMPONENT, ((GridLayoutDragComponent) type).label());
        } else {
            event.setData( LayoutDragComponent.class.toString(), type.getClass().getName() );
        }

        event.getDataTransfer().setDragImage( move.getElement(), 10, 10 );
    }

    private void createComponentWidget() {
        move.add( type.getDragWidget() );
    }

    interface MyUiBinder extends UiBinder<Widget, DragGridElement> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
