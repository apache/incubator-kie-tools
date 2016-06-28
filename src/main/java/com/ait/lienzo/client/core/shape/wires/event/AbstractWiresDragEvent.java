package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;

/**
 * <p>Base event that is fired when a wires container is being drag.</p>
 * <ul>
 *     <li>shape = the wires container</li>
 *     <li>x = the X coordinate of the shape's container</li>
 *     <li>y = the Y coordinate of the shape's container</li>
 *     <li>nodeDragEvent = the drag event on the node.</li>
 * </ul>
 */
public abstract class AbstractWiresDragEvent<H extends WiresEventHandler> extends AbstractWiresEvent<WiresContainer, H> implements INodeXYEvent {

    private final AbstractNodeDragEvent<?> nodeDragEvent;

    public AbstractWiresDragEvent( final WiresContainer shape,
                                   final AbstractNodeDragEvent<?> nodeDragEvent ) {
        super( shape );
        this.nodeDragEvent = nodeDragEvent;
    }

    @Override
    public int getX() {
        return nodeDragEvent.getX();
    }

    @Override
    public int getY() {
        return nodeDragEvent.getY();
    }

    public AbstractNodeDragEvent<?> getNodeDragEvent() {
        return nodeDragEvent;
    }

}
