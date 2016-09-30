package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresShape;

/**
 * <p>Event that is fired when the drag moves ( drag produced by one of resize control points for a wires shape ).</p>
 */
public class WiresResizeStepEvent extends AbstractWiresResizeEvent<WiresResizeStepHandler> implements INodeXYEvent {

    public static final Type<WiresResizeStepHandler> TYPE = new Type<WiresResizeStepHandler>();

    public WiresResizeStepEvent( final WiresShape shape,
                                 final AbstractNodeDragEvent<?> nodeDragEvent,
                                 final int x,
                                 final int y,
                                 final double width,
                                 final double height ) {
        super( shape, nodeDragEvent, x, y, width, height );
    }

    @Override
    public Type<WiresResizeStepHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( final WiresResizeStepHandler shapeMovedHandler ) {
        shapeMovedHandler.onShapeResizeStep( this );
    }

}
