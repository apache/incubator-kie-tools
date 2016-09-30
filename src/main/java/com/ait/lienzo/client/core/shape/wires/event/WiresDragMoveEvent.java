package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;

/**
 * <p>Event that is fired when a wires container drag moves.</p>
 */
public class WiresDragMoveEvent extends AbstractWiresDragEvent<WiresDragMoveHandler> implements INodeXYEvent {

    public static final Type<WiresDragMoveHandler> TYPE = new Type<WiresDragMoveHandler>();

    public WiresDragMoveEvent( final WiresContainer shape,
                               final AbstractNodeDragEvent<?> nodeDragEvent ) {
        super( shape, nodeDragEvent );
    }

    @Override
    public Type<WiresDragMoveHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( final WiresDragMoveHandler shapeMovedHandler ) {
        shapeMovedHandler.onShapeDragMove( this );
    }


}
