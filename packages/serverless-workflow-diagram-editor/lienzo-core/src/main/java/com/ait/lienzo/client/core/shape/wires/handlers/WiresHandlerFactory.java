package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;

/**
 * Factory to create wire shapes and connectors handlers.
 * This may be implemented to create custom handlers if necessary to override the default implementations.
 */
public interface WiresHandlerFactory {

    WiresConnectorHandler newConnectorHandler(WiresConnector connector,
                                              WiresManager wiresManager);

    WiresControlPointHandler newControlPointHandler(WiresConnector connector,
                                                    WiresManager wiresManager);

    WiresShapeHandler newShapeHandler(WiresShape shape,
                                      WiresShapeHighlight<PickerPart.ShapePart> highlight,
                                      WiresManager manager);
}