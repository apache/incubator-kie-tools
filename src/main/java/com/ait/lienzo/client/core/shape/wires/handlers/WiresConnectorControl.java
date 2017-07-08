package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.IPrimitive;

/**
 * Connector control handler provides user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types fired, and these calls be reused programatically as well.
 *
 * The default event handlers used on wires connectors registrations delegate to this control, so developers
 * can create custom connector controls and provide the instances by using the
 * <code>com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory</code> and provide custom
 * user interaction behaviours rather than defaults.
 *
 */
public interface WiresConnectorControl extends DragControl {

    void addControlPoint( double x, double y );

    void destroyControlPoint( IPrimitive<?> control);

    void showControlPoints();

    void hideControlPoints();

}