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

package org.kie.workbench.common.stunner.core.client.canvas.controls.select;

import java.util.Collections;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SelectionControlImplTest {

    private static final String ROOT_UUID = "root-uuid1";
    private static final String ELEMENT_UUID = "element-uuid1";

    @Mock
    private EventSourceMock<CanvasElementSelectedEvent> elementSelectedEvent;

    @Mock
    private EventSourceMock<CanvasClearSelectionEvent> clearSelectionEvent;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Layer layer;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Element element;

    @Mock
    private Shape<?> shape;

    @Mock
    private HasEventHandlers<ShapeViewExtStub, Object> shapeEventHandler;

    @Mock
    private HasControlPoints<ShapeViewExtStub> hasControlPoints;

    private ShapeViewExtStub shapeView;
    private SelectionControlImpl<AbstractCanvasHandler> tested;

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
        when(canvas.getLayer()).thenReturn(layer);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(shapeEventHandler.supports(eq(ViewEventType.MOUSE_CLICK))).thenReturn(true);
        this.tested = new SelectionControlImpl(elementSelectedEvent,
                                               clearSelectionEvent);
    }

    @Test
    public void testEnable() {
        tested.enable(canvasHandler);
        verify(layer,
               times(1)).addHandler(eq(ViewEventType.MOUSE_CLICK),
                                    any(MouseClickHandler.class));
    }

    @Test
    public void testLayerClickAndSelectRootElement() {
        tested.enable(canvasHandler);
        final ArgumentCaptor<MouseClickHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(MouseClickHandler.class);
        verify(layer,
               times(1)).addHandler(eq(ViewEventType.MOUSE_CLICK),
                                    clickHandlerArgumentCaptor.capture());
        final MouseClickHandler clickHandler = clickHandlerArgumentCaptor.getValue();
        final MouseClickEvent event = new MouseClickEvent(12,
                                                          20,
                                                          30,
                                                          40);
        event.setButtonLeft(true);
        event.setShiftKeyDown(false);
        clickHandler.handle(event);
        final ArgumentCaptor<CanvasElementSelectedEvent> elementSelectedEventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasElementSelectedEvent.class);
        verify(elementSelectedEvent,
               times(1)).fire(elementSelectedEventArgumentCaptor.capture());
        verify(clearSelectionEvent,
               times(1)).fire(any(CanvasClearSelectionEvent.class));
        final CanvasElementSelectedEvent ese = elementSelectedEventArgumentCaptor.getValue();
        assertEquals(ROOT_UUID,
                     ese.getElementUUID());
    }

    @Test
    public void testLayerClickAndClear() {
        when(metadata.getCanvasRootUUID()).thenReturn(null);
        tested.enable(canvasHandler);
        final ArgumentCaptor<MouseClickHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(MouseClickHandler.class);
        verify(layer,
               times(1)).addHandler(eq(ViewEventType.MOUSE_CLICK),
                                    clickHandlerArgumentCaptor.capture());
        final MouseClickHandler clickHandler = clickHandlerArgumentCaptor.getValue();
        final MouseClickEvent event = new MouseClickEvent(12,
                                                          20,
                                                          30,
                                                          40);
        event.setButtonLeft(true);
        event.setShiftKeyDown(false);
        clickHandler.handle(event);
        verify(clearSelectionEvent,
               times(1)).fire(any(CanvasClearSelectionEvent.class));
        verify(elementSelectedEvent,
               never()).fire(any(CanvasElementSelectedEvent.class));
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
    public void testSelect() {
        tested.enable(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        final ArgumentCaptor<MouseClickHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(MouseClickHandler.class);
        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.MOUSE_CLICK),
                                    clickHandlerArgumentCaptor.capture());
        assertTrue(tested.isRegistered(element));
        final MouseClickHandler clickHandler = clickHandlerArgumentCaptor.getValue();
        final MouseClickEvent event = new MouseClickEvent(12,
                                                          20,
                                                          30,
                                                          40);
        event.setButtonLeft(true);
        event.setShiftKeyDown(false);
        clickHandler.handle(event);
        verify(shape,
               times(1)).applyState(eq(ShapeState.SELECTED));
        assertEquals(1,
                     tested.getSelectedItems().size());
        assertEquals(ELEMENT_UUID,
                     tested.getSelectedItems().iterator().next());
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
        tested.disable();
        verify(layer,
               times(1)).removeHandler(any(ViewHandler.class));
    }

    @Test
    public void onKeyDownEventTest(){
        tested.enable(canvasHandler);
        testSelect();
        tested.onKeyDownEvent(KeyboardEvent.Key.ESC);

        assertTrue(tested.getSelectedItems().stream().allMatch(Objects::isNull));
        verify(shape, times(1)).applyState(ShapeState.NONE);
        verify(canvas, atLeastOnce()).draw();
    }
}
