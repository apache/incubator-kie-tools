package com.ait.lienzo.client.core.shape.wires.handlers;

public final class WiresDragControlContext implements HasDragControl.Context {

    private final int x;
    private final int y;
    private final Object source;

    public WiresDragControlContext( final int x,
                                    final int y,
                                    final Object source ) {
        this.x = x;
        this.y = y;
        this.source = source;
    }

    @Override
    public Object getSource() {
        return source;
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
