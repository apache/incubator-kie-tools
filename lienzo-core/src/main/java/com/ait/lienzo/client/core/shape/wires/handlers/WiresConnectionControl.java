package com.ait.lienzo.client.core.shape.wires.handlers;

/**
 * Connection control handler provides user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types used to fire those functions and call be reused programatically as well.
 * <p>
 * The default event handlers used on wires connection registrations delegate to this control, so developers
 * can create custom connection controls and provide the instances by using the
 * <code>com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory</code> and provide custom
 * user interaction behaviours rather than defaults.
 */
public interface WiresConnectionControl extends WiresMoveControl {

    void destroy();
}