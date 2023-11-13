/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToolboxControlTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Node<View<?>, Edge> element;

    @Mock
    private ActionsToolboxFactory toolboxFactory;

    @Mock
    private ToolboxControlImpl<ActionsToolboxFactory> delegated;

    private AbstractToolboxControl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(delegated.getCanvasHandler()).thenReturn(canvasHandler);
        this.tested = new AbstractToolboxControl(delegated) {
            @Override
            protected List<ActionsToolboxFactory> getFactories() {
                return Collections.singletonList(toolboxFactory);
            }
        };
    }

    @Test
    public void testEnable() {
        tested.init(canvasHandler);
        verify(delegated,
               times(1)).init(eq(canvasHandler));
    }

    @Test
    public void testRegister() {
        tested.init(canvasHandler);
        tested.register(element);
        verify(delegated,
               times(1)).register(eq(element));
        verify(delegated,
               never()).deregister(any(Element.class));
    }

    @Test
    public void testDeRegister() {
        tested.init(canvasHandler);
        tested.deregister(element);
        verify(delegated,
               times(1)).deregister(eq(element));
        verify(delegated,
               never()).register(any(Element.class));
    }

    @Test
    public void testGetToolboxes() {
        tested.getToolboxes(element);
        verify(delegated,
               times(1)).getToolboxes(eq(element));
    }

    @Test
    public void testElementSelectedEvent() {
        final String uuid = "uuid1";
        when(element.getUUID()).thenReturn(uuid);
        final CanvasSelectionEvent event = new CanvasSelectionEvent(canvasHandler,
                                                                    element.getUUID());
        tested.onCanvasSelectionEvent(event);
        verify(delegated,
               times(1)).show(eq(uuid));
        verify(delegated,
               never()).destroy();
    }

    @Test
    public void testElementSelectedEventCache() {
        final String uuid = "uuid1";
        when(element.getUUID()).thenReturn(uuid);
        final CanvasSelectionEvent event = new CanvasSelectionEvent(canvasHandler,
                                                                    element.getUUID());
        tested.onCanvasSelectionEvent(event);
        tested.onCanvasSelectionEvent(event);
        // Verify if it has been selected and called show on every selection event.
        verify(delegated,
               times(1)).show(eq(uuid));
        verify(delegated,
               never()).destroy();
    }

    @Test
    public void testClearSelectionEvent() {
        final CanvasClearSelectionEvent event = new CanvasClearSelectionEvent(canvasHandler);
        tested.onCanvasClearSelectionEvent(event);
        verify(delegated,
               times(1)).destroyToolboxes();
        verify(delegated,
               never()).show(any(Element.class));
        verify(delegated,
               never()).show(anyString());
    }

    @Test
    public void testShapeRemovedEvent() {
        final String uuid = "uuid1";
        when(delegated.isActive(eq(uuid))).thenReturn(true);
        final Shape shape = mock(Shape.class);
        when(shape.getUUID()).thenReturn(uuid);
        final CanvasShapeRemovedEvent event = new CanvasShapeRemovedEvent(canvas,
                                                                          shape);
        tested.onCanvasShapeRemovedEvent(event);
        verify(delegated,
               times(1)).hideAndDestroyToolboxes();
        verify(delegated,
               never()).show(any(Element.class));
        verify(delegated,
               never()).show(anyString());
    }
}
