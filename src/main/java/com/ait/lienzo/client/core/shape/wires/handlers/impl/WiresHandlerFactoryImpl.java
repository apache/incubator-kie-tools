package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;

/**
 * Default factory implementation for Wires Handlers.
 */
public class WiresHandlerFactoryImpl implements WiresHandlerFactory {

    @Override
    public WiresConnectorHandler newConnectorHandler(final WiresConnector connector,
                                                     final WiresManager wiresManager) {
        return WiresConnectorHandlerImpl.build(connector, wiresManager);
    }

    @Override
    public WiresControlPointHandler newControlPointHandler(final WiresConnector connector,
                                                           final WiresManager wiresManager) {
        return new WiresControlPointHandlerImpl(connector, wiresManager);
    }

    @Override
    public WiresShapeHandler newShapeHandler(final WiresShape shape,
                                             final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                                             final WiresManager manager) {
        return new WiresShapeHandlerImpl(shape, highlight, manager);
    }
}
