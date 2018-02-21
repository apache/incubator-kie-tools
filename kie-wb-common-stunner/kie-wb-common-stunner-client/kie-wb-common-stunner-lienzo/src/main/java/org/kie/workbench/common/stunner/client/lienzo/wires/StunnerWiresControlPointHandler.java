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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresControlPointHandlerImpl;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragEndEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragStartEvent;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class StunnerWiresControlPointHandler implements WiresControlPointHandler {

    private final WiresConnectorView connector;
    private final Event<CanvasControlPointDragEndEvent> controlPointDragEndEvent;
    private final Event<CanvasControlPointDragStartEvent> controlPointDragStartEvent;
    private final Event<CanvasControlPointDoubleClickEvent> controlPointDoubleClickEvent;
    private WiresControlPointHandler delegate;

    public StunnerWiresControlPointHandler() {
        this(null, null, null, null, null);
    }

    public StunnerWiresControlPointHandler(final WiresConnector connector, final WiresConnectorControl connectorControl,
                                           final Event<CanvasControlPointDragStartEvent> controlPointDragStartEvent,
                                           final Event<CanvasControlPointDragEndEvent> controlPointDragEndEvent,
                                           final Event<CanvasControlPointDoubleClickEvent> controlPointDoubleClickEvent) {

        if (!(connector instanceof WiresConnectorView)) {
            throw new IllegalArgumentException("connector should be a WiresConnectorView");
        }
        this.connector = (WiresConnectorView) connector;
        this.controlPointDragStartEvent = controlPointDragStartEvent;
        this.controlPointDragEndEvent = controlPointDragEndEvent;
        this.controlPointDoubleClickEvent = controlPointDoubleClickEvent;
        this.delegate = new WiresControlPointHandlerImpl(connector, connectorControl);
    }

    @Override
    public void onNodeDragEnd(NodeDragEndEvent event) {
        controlPointDragEndEvent.fire(new CanvasControlPointDragEndEvent(getPosition(event)));
    }

    private Point2D getPosition(AbstractNodeDragEvent event) {
        IPrimitive<?> node = event.getDragContext().getNode();
        return getPosition(node.getX(), node.getY());
    }

    private Point2D getPosition(double x, double y) {
        return new Point2D(x, y);
    }

    @Override
    public void onNodeDragMove(NodeDragMoveEvent event) {
        delegate.onNodeDragMove(event);
    }

    @Override
    public void onNodeDragStart(NodeDragStartEvent event) {
        controlPointDragStartEvent.fire(new CanvasControlPointDragStartEvent(getPosition(event)));
    }

    @Override
    public void onNodeMouseDoubleClick(NodeMouseDoubleClickEvent event) {
        IPrimitive<?> node = (IPrimitive<?>) event.getSource();
        controlPointDoubleClickEvent.fire(new CanvasControlPointDoubleClickEvent(getPosition(node.getLocation().getX(), node.getLocation().getY())));
    }
}