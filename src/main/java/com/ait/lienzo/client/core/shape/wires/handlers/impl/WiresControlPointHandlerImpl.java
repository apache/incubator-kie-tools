/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
