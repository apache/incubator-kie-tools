package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;

public class WiresControlFactoryImpl implements WiresControlFactory {

    @Override
    public WiresShapeControl newShapeControl(WiresShape shape,
                                             WiresManager wiresManager) {
        return new WiresShapeControlImpl(shape,
                                         wiresManager);
    }

    @Override
    public WiresCompositeControl newCompositeControl(WiresCompositeControl.Context selectionContext,
                                                     WiresManager wiresManager) {
        return new WiresCompositeControlImpl(selectionContext);
    }

    @Override
    public WiresConnectorControl newConnectorControl(WiresConnector connector,
                                                     WiresManager wiresManager) {
        return new WiresConnectorControlImpl(connector,
                                             wiresManager);
    }

    @Override
    public WiresConnectionControl newConnectionControl(WiresConnector connector,
                                                       boolean headNotTail,
                                                       WiresManager wiresManager) {
        return new WiresConnectionControlImpl(connector,
                                              headNotTail,
                                              wiresManager);
    }
}
