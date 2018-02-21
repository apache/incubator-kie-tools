package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHandlerImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;

/**
 * Default factory implementation for Wires Handlers.
 */
public class WiresHandlerFactoryImpl implements WiresHandlerFactory {

    @Override
    public WiresConnectorHandler newConnectorHandler(WiresConnector connector, WiresManager wiresManager) {
        return new WiresConnectorHandlerImpl(connector, wiresManager);
    }

    @Override
    public WiresControlPointHandler newControlPointHandler(WiresConnector connector, WiresConnectorControl connectorControl) {
        return new WiresControlPointHandlerImpl(connector, connectorControl);
    }

    @Override
    public WiresShapeHandler newShapeHandler(WiresShapeControl control, WiresShapeHighlight<PickerPart.ShapePart> highlight, WiresManager manager) {
        return new WiresShapeHandlerImpl(control, highlight, manager);
    }
}
