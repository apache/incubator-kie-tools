package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;

public interface WiresShapeLocationControl extends WiresMoveControl,
                                                   WiresControl {
    WiresShape getShape();

    Point2D getShapeInitialLocation();

    Point2D getCurrentLocation();

    Point2D getShapeLocation();

    void setShapeLocation(Point2D location);

    void onMoveAdjusted(Point2D dxy);
}
