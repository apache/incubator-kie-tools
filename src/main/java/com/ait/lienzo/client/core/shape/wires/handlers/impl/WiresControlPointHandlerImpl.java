package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresControlPointHandlerImpl implements WiresControlPointHandler {

    private WiresConnector m_connector;
    private WiresConnectorControl m_connectorControl;

    public WiresControlPointHandlerImpl(WiresConnector connector, WiresConnectorControl connectorControl) {
        this.m_connector = connector;
        this.m_connectorControl = connectorControl;
    }

    @Override
    public void onNodeMouseDoubleClick(final NodeMouseDoubleClickEvent event) {
        if (m_connector.getPointHandles().isVisible()) {
            m_connectorControl.destroyControlPoint((IPrimitive<?>) event.getSource());
            m_connector.getLine().getLayer().batch();
        }
    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event) {
        //no default implementation
    }

    @Override
    public void onNodeDragStart(NodeDragStartEvent event) {
        //no default implementation
    }

    @Override
    public void onNodeDragMove(NodeDragMoveEvent event) {
        final IPrimitive<?> primitive = (IPrimitive<?>) event.getSource();
        final Point2D adjust = m_connectorControl.adjustControlPointAt(primitive.getX(), primitive.getY(), event.getX(), event.getY());
        if (null != adjust) {
            primitive.setX(adjust.getX());
            primitive.setY(adjust.getY());
        }
    }
}
