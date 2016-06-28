package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;

/**
 * In general control handlers provide user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types fired, and these calls be reused programatically as well.
 *
 * By default, <code>com.ait.lienzo.client.core.shape.wires.WiresManager</code> register some event types when a shape or connector is registered. Those event handlers
 * just delegate the operations to the different control interfaces.
 *
 * Developers can provide custom control instances to override default event handlers behaviors by providing
 * a concrete <code>WiresControlFactory</code> instance for <code>com.ait.lienzo.client.core.shape.wires.WiresManager</code>.
 */
public interface WiresControlFactory {

    WiresShapeControl newShapeControl( WiresShape shape, WiresManager wiresManager );

    WiresDockingAndContainmentControl newDockingAndContainmentControl( WiresShape shape, WiresManager wiresManager );

    WiresConnectorControl newConnectorControl( WiresConnector connector, WiresManager wiresManager );

    WiresConnectionControl newConnectionControl( WiresConnector connector, WiresManager wiresManager );

}
