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

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragEndEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.controlpoint.CanvasControlPointDragStartEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerWiresControlPointHandlerTest {

    private StunnerWiresControlPointHandler stunnerWiresControlPointHandler;

    @Mock
    private WiresConnectorView connector;

    @Mock
    private WiresConnectorControl connectorControl;

    @Mock
    private EventSourceMock<CanvasControlPointDragStartEvent> controlPointDragStartEvent;

    @Mock
    private EventSourceMock<CanvasControlPointDragEndEvent> controlPointDragEndEvent;

    @Mock
    private EventSourceMock<CanvasControlPointDoubleClickEvent> controlPointDoubleClickEvent;

    @Mock
    private NodeDragStartEvent dragStartEvent;

    @Mock
    private NodeDragEndEvent dragEndEvent;

    @Mock
    private NodeMouseDoubleClickEvent mouseDoubleClickEvent;

    @Mock
    private DragContext dragContext;

    @Mock
    private IPrimitive primitive;

    private Point2D location;

    @Before
    public void setUp() throws Exception {
        location = new Point2D(0,0);
        stunnerWiresControlPointHandler =
                new StunnerWiresControlPointHandler(connector, connectorControl, controlPointDragStartEvent,
                                                    controlPointDragEndEvent, controlPointDoubleClickEvent);

        when(dragStartEvent.getDragContext()).thenReturn(dragContext);
        when(dragEndEvent.getDragContext()).thenReturn(dragContext);
        when(dragContext.getNode()).thenReturn(primitive);
        when(mouseDoubleClickEvent.getSource()).thenReturn(primitive);
        when(primitive.getLocation()).thenReturn(location);

    }

    @Test
    public void onNodeDragEnd() {
        stunnerWiresControlPointHandler.onNodeDragEnd(dragEndEvent);
        ArgumentCaptor<CanvasControlPointDragEndEvent> eventArgumentCaptor = ArgumentCaptor.forClass(CanvasControlPointDragEndEvent.class);
        verify(controlPointDragEndEvent).fire(eventArgumentCaptor.capture());
        eventArgumentCaptor.getValue().getPosition().equals(getLocation());
    }

    private org.kie.workbench.common.stunner.core.graph.content.view.Point2D getLocation() {
        return new org.kie.workbench.common.stunner.core.graph.content.view.Point2D(location.getX(), location.getY());
    }

    @Test
    public void onNodeDragStart() {
        stunnerWiresControlPointHandler.onNodeDragStart(dragStartEvent);
        ArgumentCaptor<CanvasControlPointDragStartEvent> eventArgumentCaptor = ArgumentCaptor.forClass(CanvasControlPointDragStartEvent.class);
        verify(controlPointDragStartEvent).fire(eventArgumentCaptor.capture());
        eventArgumentCaptor.getValue().getPosition().equals(getLocation());
    }

    @Test
    public void onNodeMouseDoubleClick() {
        stunnerWiresControlPointHandler.onNodeMouseDoubleClick(mouseDoubleClickEvent);
        ArgumentCaptor<CanvasControlPointDoubleClickEvent> eventArgumentCaptor = ArgumentCaptor.forClass(CanvasControlPointDoubleClickEvent.class);
        verify(controlPointDoubleClickEvent).fire(eventArgumentCaptor.capture());
        eventArgumentCaptor.getValue().getPosition().equals(getLocation());
    }
}