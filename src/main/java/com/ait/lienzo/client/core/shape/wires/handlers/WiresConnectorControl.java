package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.IPrimitive;
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
public interface WiresConnectorControl extends WiresMoveControl {

    void move(double dx, double dy, boolean midPointsOnly, boolean moveLinePoints);

    /**
     * Add a control point on the existing {@link WiresConnector#getLine()} returning the selected index to the control point
     * @param x position
     * @param y position
     * @return index to the added Control Point
     */
    int addControlPoint(double x, double y);

    /**
     * Create a new point on the {@link WiresConnector#getLine()} and a control point on the given index.
     * @param x position
     * @param y position
     * @param index point index on the connector line
     */
    void addControlPointToLine(double x, double y, int index);

    /**
     * Removes a given control point, based on the x,y position from the {@link WiresConnector line}.
     * @param x position
     * @param y position
     */
    void removeControlPoint(final double x, final double y);

    void destroyControlPoint(IPrimitive<?> control);

    void showControlPoints();

    void hideControlPoints();

    Point2D adjustControlPointAt(double x, double y, double deltaX, double deltaY);

    WiresConnectionControl getHeadConnectionControl();

    WiresConnectionControl getTailConnectionControl();

    void reset();
}