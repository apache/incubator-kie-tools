package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.google.gwt.event.shared.GwtEvent;

/**
 * <p>Event that is fired when a WiresShape x/y coordinates has been changed.</p>
 * <ul>
 *     <li>shape = the shape</li>
 *     <li>x = the X coordinate value of the shape's container</li>
 *     <li>y = the Y coordinate value of the shape's container</li>
 * </ul>
 */
public class WiresMoveEvent extends AbstractWiresEvent<WiresContainer, WiresMoveHandler> implements INodeXYEvent {

    public static final GwtEvent.Type<WiresMoveHandler> TYPE = new GwtEvent.Type<WiresMoveHandler>();

    private final int x;
    private final int y;

    public WiresMoveEvent( final WiresContainer shape,
                           final int x,
                           final int y ) {
        super( shape );
        this.x = x;
        this.y = y;
    }

    @Override
    public GwtEvent.Type<WiresMoveHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( final WiresMoveHandler shapeMovedHandler ) {
        shapeMovedHandler.onShapeMoved( this );
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
