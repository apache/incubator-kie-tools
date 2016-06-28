package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresShape;

/**
 * <p>Base event that is fired when a resize control point type for a wires shape is being drag (so shape's path
 * being updated as well).</p>
 * <ul>
 *     <li>shape = the wires shape</li>
 *     <li>x = the X coordinate of the shape's container</li>
 *     <li>y = the Y coordinate of the shape's container</li>
 *     <li>with = the with of the shape's bounding box.</li>
 *     <li>height = the height of the shape's bounding box.</li>
 *     <li>nodeDragEvent = the drag event on the resize control point's primitive.</li>
 * </ul>
 */
public abstract class AbstractWiresResizeEvent<H extends WiresEventHandler> extends AbstractWiresEvent<WiresShape, H> implements INodeXYEvent {

    private final AbstractNodeDragEvent<?> nodeDragEvent;
    private final int x;
    private final int y;
    private final double width;
    private final double height;

    public AbstractWiresResizeEvent( final WiresShape shape,
                                     final AbstractNodeDragEvent<?> nodeDragEvent,
                                     final int x,
                                     final int y,
                                     final double width,
                                     final double height ) {
        super( shape );
        this.nodeDragEvent = nodeDragEvent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public AbstractNodeDragEvent<?> getNodeDragEvent() {
        return nodeDragEvent;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
