package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;

/**
 * Handler to perform callback actions on connector control points managed by the {@link WiresConnectorControl}.
 * This may be extended using the {@link WiresHandlerFactory} that is used by  * the {@link WiresConnectorControl}
 * when adding control points to a connector.
 */
public interface WiresControlPointHandler extends NodeMouseDoubleClickHandler,
                                                  NodeMouseClickHandler,
                                                  NodeDragStartHandler,
                                                  NodeDragEndHandler,
                                                  NodeDragMoveHandler {

}