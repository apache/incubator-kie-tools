package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;

public class WiresConnectorHandlerImpl implements WiresConnectorHandler {

    private final WiresConnectorControl m_control;

    private final WiresConnector m_connector;

    private final WiresManager m_wiresManager;

    public WiresConnectorHandlerImpl(WiresConnector connector, WiresManager wiresManager) {
        this.m_control = wiresManager.getControlFactory().newConnectorControl(connector, wiresManager);
        this.m_connector = connector;
        m_wiresManager = wiresManager;
    }

    @Override
    public void onNodeDragStart(NodeDragStartEvent event) {
        this.m_control.onMoveStart(event.getDragContext().getDragStartX(),
                                   event.getDragContext().getDragStartY());
    }

    @Override
    public void onNodeDragMove(NodeDragMoveEvent event) {
        this.m_control.onMove(event.getDragContext().getDragStartX(),
                              event.getDragContext().getDragStartY());
    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event) {
        this.m_control.onMoveComplete();
    }

    @Override
    public void onNodeMouseClick(NodeMouseClickEvent event) {
        if (m_wiresManager.getSelectionManager() != null) {
            m_wiresManager.getSelectionManager().selected(m_connector, event.isShiftKeyDown());
        }
    }

    @Override
    public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event) {
        m_control.addControlPoint(event.getX(), event.getY());
    }

    public WiresConnectorControl getControl() {
        return m_control;
    }
}