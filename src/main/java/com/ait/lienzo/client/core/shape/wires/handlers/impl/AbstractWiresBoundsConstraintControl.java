package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.handlers.WiresBoundsConstraintControl;
import com.ait.lienzo.client.core.types.BoundingBox;

public abstract class AbstractWiresBoundsConstraintControl implements WiresBoundsConstraintControl {

    private BoundingBox constrainedBounds;

    @Override
    public void setBoundsConstraint(BoundingBox boundingBox) {
        this.constrainedBounds = boundingBox;
    }

    protected BoundingBox getConstrainedBounds() {
        return constrainedBounds;
    }

}
