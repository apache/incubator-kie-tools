package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.types.Point2D;

/**
 * Control for changing locations for wires objects (shapes, connectors, etc).
 *
 * This type allows decoupling the drag handlers added by default to wires objects from each control's logic.
 */
public interface WiresMoveControl {

    /**
     * The move is starting at this point.
     */
    void onMoveStart(double x,
                     double y);

    /**
     * Moving the wires object a certain distance (dx, dy) from
     * the starting point.
     * @return <code>true</code> if the wires object location should be adjusted, and
     * <code>false</code> in case no adjustment is required.
     */
    boolean onMove(double dx,
                   double dy);

    /**
     * The moving has been completed, so operations can be performed at this point.
     */
    void onMoveComplete();

    /**
     * Returns the current adjustment to apply to the wires object, in case
     * the <code>onMove</code> method returns <code>true</code>.
     */
    Point2D getAdjust();
}
