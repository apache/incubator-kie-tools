package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;

/**
 * Handler to perform callback operations on a connector.
 * This may be extended using the {@link WiresHandlerFactory} that is used by
 * the {@link WiresManager} on the {@link WiresConnector} register process.
 */
public interface WiresConnectorHandler extends NodeDragStartHandler,
                                               NodeDragMoveHandler,
                                               NodeDragEndHandler,
                                               NodeMouseClickHandler,
                                               NodeMouseEnterHandler,
                                               NodeMouseExitHandler,
                                               NodeMouseDownHandler,
                                               NodeMouseMoveHandler {

    WiresConnectorControl getControl();
}