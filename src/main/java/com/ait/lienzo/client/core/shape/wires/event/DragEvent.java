package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.event.shared.GwtEvent;

public class DragEvent extends AbstractWiresEvent<DragHandler> implements INodeXYEvent {

    private int x;
    private int y;
    private Type type;

    public DragEvent(WiresShape shape, int x, int y, Type type) {
        super(shape);
        this.x = x;
        this.y = y;
        this.type = type;
    }

    @Override
    public GwtEvent.Type<DragHandler> getAssociatedType() {
        return DRAG;
    }

    @Override
    protected void dispatch(DragHandler handler) {
        if (Type.START.equals(type)) {
            handler.onDragStart(this);
        } else if (Type.STEP.equals(type)) {
            handler.onDragMove(this);
        } else {
            handler.onDragEnd(this);
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
