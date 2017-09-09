package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.types.BoundingBox;

/**
 * A control that has some kind of bounds location constraint.
 * Eg: WiresMoveControl implementation can use it in order to enforce
 * shape's location restrictions.
 */
public interface WiresBoundsConstraintControl {

    void setBoundsConstraint(BoundingBox boundingBox);
}
