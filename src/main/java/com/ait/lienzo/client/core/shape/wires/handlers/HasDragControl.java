package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.types.Point2D;

public interface HasDragControl {

    interface Context {

        Object getSource();

        int getX();

        int getY();

    }

    void dragStart( Context context );

    void dragMove( Context context );

    boolean dragEnd( Context context );

    boolean dragAdjust( Point2D dxy );
}
