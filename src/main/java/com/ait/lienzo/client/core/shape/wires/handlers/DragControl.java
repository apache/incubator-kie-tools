package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;

public interface DragControl {

    void dragStart( DragContext context );

    void dragMove( DragContext context );

    boolean dragEnd( DragContext context );

    boolean dragAdjust( Point2D dxy );
}
