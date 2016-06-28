package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;

/**
 * <p>Event that is fired when a wires container drag starts.</p>
 */
public class WiresDragStartEvent extends AbstractWiresDragEvent<WiresDragStartHandler> implements INodeXYEvent {

    public static final Type<WiresDragStartHandler> TYPE = new Type<WiresDragStartHandler>();

    public WiresDragStartEvent( final WiresContainer shape,
                                final AbstractNodeDragEvent<?> nodeDragEvent ) {
        super( shape, nodeDragEvent );
    }

    @Override
    public Type<WiresDragStartHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( final WiresDragStartHandler shapeMovedHandler ) {
        shapeMovedHandler.onShapeDragStart( this );
    }


}
