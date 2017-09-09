package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.handlers.WiresBoundsConstraintControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMoveControl;
import com.ait.lienzo.client.core.types.BoundingBox;

public abstract class AbstractWiresBoundsConstraintControl implements WiresMoveControl,
                                                                      WiresBoundsConstraintControl {

    private BoundingBox constrainedBounds;

    protected abstract BoundingBox getBounds();

    @Override
    public void setBoundsConstraint(BoundingBox boundingBox) {
        this.constrainedBounds = boundingBox;
    }

    @Override
    public boolean onMove(double dx,
                          double dy) {
        // Check the location bounds, if any.
        if (null != constrainedBounds) {
            final double shapeMinX = getBounds().getMinX() + dx;
            final double shapeMinY = getBounds().getMinY() + dy;
            final double shapeMaxX = shapeMinX + (getBounds().getMaxX() - getBounds().getMinX());
            final double shapeMaxY = shapeMinY + (getBounds().getMaxY() - getBounds().getMinY());
            if (shapeMinX <= constrainedBounds.getMinX() ||
                    shapeMaxX >= constrainedBounds.getMaxX() ||
                    shapeMinY <= constrainedBounds.getMinY() ||
                    shapeMaxY >= constrainedBounds.getMaxY()) {
                // Bounds are exceeded as from last adjusted location, so
                // just accept adjust and keep current location value.
                return true;
            }
        }
        return false;
    }
}
