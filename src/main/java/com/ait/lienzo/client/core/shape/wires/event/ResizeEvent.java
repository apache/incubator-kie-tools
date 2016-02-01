package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that is fired after a WiresShape has been resized.
 * - shape -> the resized WiresShape
 * - index -> the index for the resize control point used
 * - eventX -> the X coordinate value for the mouse drag event
 * - eventY -> the Y coordinate value for the mouse drag event
 * - width -> the WiresShape bounding box's width after resize
 * - height -> the WiresShape bounding box's height after resize
 */
public class ResizeEvent extends AbstractWiresEvent<ResizeHandler> implements INodeXYEvent {

    private int index;
    private int eventX;
    private int eventY;
    private double width;
    private double height;
    private Type type;

    public ResizeEvent(WiresShape shape, int index, 
                       int eventX, int eventY,
                       double width, double height,
                       Type type) {
        super(shape);
        this.index = index;
        this.eventX = eventX;
        this.eventY = eventY;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    @Override
    public GwtEvent.Type<ResizeHandler> getAssociatedType() {
        return RESIZE;
    }

    @Override
    protected void dispatch(ResizeHandler handler) {
        if (Type.START.equals(type)) {
            handler.onResizeStart(this);
        } else if (Type.STEP.equals(type)) {
            handler.onResizeStep(this);
        } else {
            handler.onResizeEnd(this);
        }
    }

    @Override
    public int getX() {
        return eventX;
    }

    @Override
    public int getY() {
        return eventY;
    }

    public int getControlIndex() {
        return index;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
