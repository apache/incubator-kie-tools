package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.*;

public class WiresControlFactoryImpl implements WiresControlFactory {
    @Override
    public WiresShapeControl newShapeControl( WiresShape shape, WiresManager wiresManager ) {
        return new WiresShapeControlImpl( shape, wiresManager);
    }

    @Override
    public WiresDockingAndContainmentControl newDockingAndContainmentControl( WiresShape shape, WiresManager wiresManager ) {
        return new WiresDockingAndContainmentControlImpl( shape, wiresManager );
    }

    @Override
    public WiresConnectorControl newConnectorControl( WiresConnector connector, WiresManager wiresManager ) {
        return new WiresConnectorControlImpl( connector, wiresManager );
    }

    @Override
    public WiresConnectionControl newConnectionControl( WiresConnector connector, WiresManager wiresManager ) {
        return new WiresConnectionControlImpl( connector, wiresManager );
    }

}
