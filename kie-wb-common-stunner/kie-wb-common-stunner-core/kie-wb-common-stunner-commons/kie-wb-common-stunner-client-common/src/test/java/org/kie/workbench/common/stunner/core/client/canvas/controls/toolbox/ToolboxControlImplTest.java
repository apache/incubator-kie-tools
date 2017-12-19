/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToolboxControlImplTest {

    private static final String ROOT_UUID = "root-uuid1";
    private static final String ELEMENT_UUID = "element-uuid1";

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Node<View<?>, Edge> element;

    @Mock
    private Shape<?> shape;

    @Mock
    private HasEventHandlers<ShapeViewExtStub, Object> shapeEventHandler;

    @Mock
    private HasControlPoints<ShapeViewExtStub> hasControlPoints;

    @Mock
    private ToolboxFactory<AbstractCanvasHandler, Element> toolboxFactory;

    @Mock
    private Predicate<String> showToolboxPredicate;

    @Mock
    private Toolbox<?> toolbox;

    private ShapeViewExtStub shapeView;
    private ToolboxControlImpl<ToolboxFactory<AbstractCanvasHandler, Element>> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.shapeView = new ShapeViewExtStub(shapeEventHandler,
                                              hasControlPoints);
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(shapeEventHandler.supports(eq(ViewEventType.MOUSE_CLICK))).thenReturn(true);
        when(toolboxFactory.build(eq(canvasHandler),
                                  eq(element)))
                .thenReturn(Optional.of(toolbox));
        when(showToolboxPredicate.test(anyString())).thenReturn(true);
        this.tested = new ToolboxControlImpl(() -> Collections.singletonList(toolboxFactory),
                                             showToolboxPredicate);
    }

    @Test
    public void testEnable() {
        tested.enable(canvasHandler);
        assertEquals(canvasHandler,
                     tested.getCanvasHandler());
    }

    @Test
    public void testRegisterElement() {
        tested.enable(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        verify(shapeEventHandler,
               times(1)).supports(eq(ViewEventType.MOUSE_CLICK));
        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.MOUSE_CLICK),
                                    any(MouseClickHandler.class));
        assertTrue(tested.isRegistered(element));
    }

    @Test
    public void testNotSupportedElement() {
        tested.enable(canvasHandler);
        final Edge edge = mock(Edge.class);
        tested.register(edge);
        assertFalse(tested.isRegistered(edge));
        verify(shapeEventHandler,
               never()).supports(any(ViewEventType.class));
        verify(shapeEventHandler,
               never()).addHandler(any(ViewEventType.class),
                                   any(MouseClickHandler.class));
    }

    @Test
    public void testGetFactories() {
        tested.enable(canvasHandler);
        tested.register(element);
        assertTrue(tested.isRegistered(element));
        final Iterator<Toolbox<?>> toolboxes = tested.getToolboxes(element);
        assertTrue(toolboxes.hasNext());
        assertEquals(toolbox,
                     toolboxes.next());
        verify(toolbox,
               never()).show();
        verify(toolbox,
               never()).hide();
        verify(toolbox,
               never()).destroy();
    }

    @Test
    public void testShowPredicate() {
        when(showToolboxPredicate.test(anyString())).thenReturn(false);
        tested.enable(canvasHandler);
        tested.register(element);
        tested.show(element);
        tested.show(ELEMENT_UUID);
        verify(toolbox,
               never()).show();
        verify(toolbox,
               never()).hide();
        verify(toolbox,
               never()).destroy();
    }

    @Test
    public void testShow() {
        tested.enable(canvasHandler);
        tested.register(element);
        assertTrue(tested.isRegistered(element));
        tested.show(element);
        verify(toolbox,
               times(1)).show();
        verify(toolbox,
               never()).hide();
        verify(toolbox,
               never()).destroy();
    }

    @Test
    public void testActiveElement() {
        tested.enable(canvasHandler);
        tested.register(element);
        assertTrue(tested.isRegistered(element));
        assertFalse(tested.isActive(element.getUUID()));
        tested.show(element);
        assertTrue(tested.isActive(element.getUUID()));
        tested.destroy();
        assertFalse(tested.isActive(element.getUUID()));
    }

    @Test
    public void testDestroy() {
        tested.enable(canvasHandler);
        tested.register(element);
        assertTrue(tested.isRegistered(element));
        tested.show(element);
        tested.destroy();
        verify(toolbox,
               times(1)).show();
        verify(toolbox,
               times(1)).destroy();
        verify(toolbox,
               never()).hide();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregisterElement() {
        tested.enable(canvasHandler);
        tested.register(element);
        tested.deregister(element);
        verify(shapeEventHandler,
               times(1)).removeHandler(any(ViewHandler.class));
        assertFalse(tested.isRegistered(element));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDisable() {
        tested.enable(canvasHandler);
        tested.register(element);
        tested.show(element);
        tested.disable();
        verify(toolbox,
               times(1)).show();
        verify(toolbox,
               times(1)).destroy();
        verify(toolbox,
               never()).hide();
    }
}
