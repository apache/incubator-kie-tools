package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;

/**
 * <p>Event that is fired when a wires container drag ends.</p>
 */
public class WiresDragEndEvent extends AbstractWiresDragEvent<WiresDragEndHandler> implements INodeXYEvent {

    public static final Type<WiresDragEndHandler> TYPE = new Type<WiresDragEndHandler>();

    public WiresDragEndEvent( final WiresContainer shape,
                              final AbstractNodeDragEvent<?> nodeDragEvent ) {
        super( shape, nodeDragEvent );
    }

    @Override
    public Type<WiresDragEndHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( final WiresDragEndHandler shapeMovedHandler ) {
        shapeMovedHandler.onShapeDragEnd( this );
    }


}
