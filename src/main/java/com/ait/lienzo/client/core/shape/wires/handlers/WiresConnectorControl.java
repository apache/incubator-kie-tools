package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.types.Point2D;

/**
 * Connector control handler provides user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types fired, and these calls be reused programatically as well.
 * <p>
 * The default event handlers used on wires connectors registrations delegate to this control, so developers
 * can create custom connector controls and provide the instances by using the
 * <code>com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory</code> and provide custom
 * user interaction behaviours rather than defaults.
 */
public interface WiresConnectorControl extends WiresMoveControl, WiresControl {

    /**
     * Add a control point on the existing {@link WiresConnector#getLine()} returning the selected index to the control point
     * @param x position
     * @param y position
     * @return index to the added Control Point
     */
    int addControlPoint(double x, double y);

    boolean moveControlPoint(int index, Point2D location);

    void destroyControlPoint(int index);

    void showControlPoints();

    void hideControlPoints();

    boolean areControlPointsVisible();

    boolean accept();

}