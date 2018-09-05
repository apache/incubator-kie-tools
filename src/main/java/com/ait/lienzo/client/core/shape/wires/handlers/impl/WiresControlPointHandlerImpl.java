package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresControlPointHandlerImpl implements WiresControlPointHandler {

    private final WiresConnector m_connector;
    private final WiresManager m_manager;
    private int cpIndexInitial;


    public WiresControlPointHandlerImpl(final WiresConnector connector,
                                        final WiresManager wiresManager) {
        this.m_connector = connector;
        this.m_manager = wiresManager;
    }

    @Override
    public void onNodeMouseDoubleClick(final NodeMouseDoubleClickEvent event) {
        if (m_connector.getPointHandles().isVisible()) {
            final IPrimitive<?> cp = (IPrimitive<?>) event.getSource();
            final int index = m_connector.getControlPointIndex(cp.getX(), cp.getY());
            getControl().destroyControlPoint(index);
            batch();
        }
    }

    @Override
    public void onNodeMouseClick(NodeMouseClickEvent event) {
        // If no click handler present, it does not receives the double click event.
    }

    @Override
    public void onNodeDragStart(NodeDragStartEvent event) {
        final IPrimitive<?> cp = (IPrimitive<?>) event.getSource();
        cpIndexInitial = m_connector.getControlPointIndex(cp.getX(), cp.getY());
    }

    @Override
    public void onNodeDragMove(NodeDragMoveEvent event) {
        final IPrimitive<?> primitive = (IPrimitive<?>) event.getSource();
        final Point2D location = m_connector.getLine().adjustPoint(primitive.getX(),
                                                                     primitive.getY(),
                                                                     event.getX(),
                                                                     event.getY());
        if (null != location) {
            primitive.setX(location.getX());
            primitive.setY(location.getY());
        }
        m_connector.firePointsUpdated();
    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event) {
        final IPrimitive<?> primitive = (IPrimitive<?>) event.getSource();
        if (!getControl().moveControlPoint(cpIndexInitial,
                                      new Point2D(primitive.getX(),
                                                  primitive.getY()))) {
            event.getDragContext().reset();
            getControl().reset();
        }
    }

    private void batch() {
        m_manager.getLayer().getLayer().batch();
    }

    private WiresConnectorControl getControl() {
        return m_connector.getControl();
    }

}
