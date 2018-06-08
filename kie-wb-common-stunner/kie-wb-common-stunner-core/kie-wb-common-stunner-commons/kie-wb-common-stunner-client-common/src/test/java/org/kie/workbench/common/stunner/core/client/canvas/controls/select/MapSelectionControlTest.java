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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MapSelectionControlTest {

    private static final String ROOT_UUID = "root-uuid1";
    private static final String ELEMENT_UUID = "element-uuid1";

    @Mock
    private EventSourceMock<CanvasSelectionEvent> elementSelectedEvent;

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
    private Object definition;

    @Mock
    private Element element;

    @Mock
    private Shape<ShapeView> shape;

    @Mock
    private HasEventHandlers<ShapeViewExtStub, Object> shapeEventHandler;

    @Mock
    private HasControlPoints<ShapeViewExtStub> hasControlPoints;

    private MapSelectionControl<AbstractCanvasHandler> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        ShapeViewExtStub shapeView = new ShapeViewExtStub(shapeEventHandler,
                                                          hasControlPoints);
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.getContent()).thenReturn(new ViewImpl<>(definition,
                                                             BoundsImpl.build(0, 0, 10, 10)));
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
        this.tested = new MapSelectionControl(e -> elementSelectedEvent.fire((CanvasSelectionEvent) e),
                                              e -> clearSelectionEvent.fire((CanvasClearSelectionEvent) e));
        this.tested.setReadonly(false);
    }

    @Test
    public void testEnable() {
        tested.init(canvasHandler);
        verify(layer,
               times(1)).addHandler(eq(ViewEventType.MOUSE_CLICK),
                                    any(MouseClickHandler.class));
    }

    @Test
    public void testLayerClickAndSelectRootElement() {
        tested.init(canvasHandler);
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
        final ArgumentCaptor<CanvasSelectionEvent> elementSelectedEventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(elementSelectedEvent,
               times(1)).fire(elementSelectedEventArgumentCaptor.capture());
        verify(clearSelectionEvent,
               times(1)).fire(any(CanvasClearSelectionEvent.class));
        final CanvasSelectionEvent ese = elementSelectedEventArgumentCaptor.getValue();
        assertEquals(ROOT_UUID,
                     ese.getIdentifiers().iterator().next());
    }

    @Test
    public void testLayerClickAndClear() {
        when(metadata.getCanvasRootUUID()).thenReturn(null);
        tested.init(canvasHandler);
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
               never()).fire(any(CanvasSelectionEvent.class));
    }

    @Test
    public void testRegisterElement() {
        tested.init(canvasHandler);
        assertFalse(isRegistered(element));
        tested.register(element);
        assertTrue(isRegistered(element));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregisterElement() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.deregister(element);
        assertFalse(isRegistered(element));
    }

    @Test
    public void testSelect() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element);
        assertEquals(1, tested.getSelectedItems().size());
        assertEquals(ELEMENT_UUID, tested.getSelectedItems().iterator().next());
        verify(shape, times(1)).applyState(eq(ShapeState.SELECTED));
        verify(shape, never()).applyState(eq(ShapeState.NONE));
        verify(shape, never()).applyState(eq(ShapeState.INVALID));
        verify(shape, never()).applyState(eq(ShapeState.HIGHLIGHT));
        final ArgumentCaptor<CanvasSelectionEvent> elementSelectedEventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(elementSelectedEvent,
               times(1)).fire(elementSelectedEventArgumentCaptor.capture());
        final CanvasSelectionEvent event = elementSelectedEventArgumentCaptor.getValue();
        assertEquals(1, event.getIdentifiers().size());
        assertEquals(ELEMENT_UUID, event.getIdentifiers().iterator().next());
    }

    @Test
    public void testSelectReadOnly() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.setReadonly(true);
        tested.select(element);
        verify(shape, never()).applyState(eq(ShapeState.SELECTED));
        verify(shape, never()).applyState(eq(ShapeState.NONE));
        verify(shape, never()).applyState(eq(ShapeState.INVALID));
        verify(shape, times(1)).applyState(eq(ShapeState.HIGHLIGHT));
    }

    @Test
    public void testDeselect() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element);
        tested.deselect(element);
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(shape, times(1)).applyState(eq(ShapeState.SELECTED));
        verify(shape, times(1)).applyState(eq(ShapeState.NONE));
        verify(shape, never()).applyState(eq(ShapeState.INVALID));
        verify(shape, never()).applyState(eq(ShapeState.HIGHLIGHT));
    }

    @Test
    public void testClearSelection() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element);
        tested.clearSelection();
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(shape, times(1)).applyState(eq(ShapeState.SELECTED));
        verify(shape, times(1)).applyState(eq(ShapeState.NONE));
        verify(shape, never()).applyState(eq(ShapeState.INVALID));
        verify(shape, never()).applyState(eq(ShapeState.HIGHLIGHT));
        verify(clearSelectionEvent,
               times(1)).fire(any(CanvasClearSelectionEvent.class));
    }

    @Test
    public void testOnShapeRemovedEvent() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element);
        CanvasShapeRemovedEvent shapeRemovedEvent = new CanvasShapeRemovedEvent(canvas,
                                                                                shape);
        tested.onShapeRemoved(shapeRemovedEvent);
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(shape, times(1)).applyState(eq(ShapeState.SELECTED));
        verify(shape, times(1)).applyState(eq(ShapeState.NONE));
        verify(shape, never()).applyState(eq(ShapeState.INVALID));
        verify(shape, never()).applyState(eq(ShapeState.HIGHLIGHT));
    }

    @Test
    public void testOnClearSelectionEvent() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element);
        CanvasClearSelectionEvent event = new CanvasClearSelectionEvent(canvasHandler);
        tested.onCanvasClearSelection(event);
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(shape, times(1)).applyState(eq(ShapeState.SELECTED));
        verify(shape, times(1)).applyState(eq(ShapeState.NONE));
        verify(shape, never()).applyState(eq(ShapeState.INVALID));
        verify(shape, never()).applyState(eq(ShapeState.HIGHLIGHT));
        verify(clearSelectionEvent,
               never()).fire(any(CanvasClearSelectionEvent.class));
    }

    @Test
    public void testOnSelectEvent() {
        tested.init(canvasHandler);
        tested.register(element);
        CanvasSelectionEvent event = new CanvasSelectionEvent(canvasHandler,
                                                              ELEMENT_UUID);
        tested.onCanvasElementSelected(event);
        assertEquals(1, tested.getSelectedItems().size());
        assertEquals(ELEMENT_UUID, tested.getSelectedItems().iterator().next());
        verify(shape, times(1)).applyState(eq(ShapeState.SELECTED));
        verify(shape, times(1)).applyState(eq(ShapeState.NONE));
        verify(shape, never()).applyState(eq(ShapeState.INVALID));
        verify(shape, never()).applyState(eq(ShapeState.HIGHLIGHT));
        final ArgumentCaptor<CanvasSelectionEvent> elementSelectedEventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(elementSelectedEvent,
               times(1)).fire(elementSelectedEventArgumentCaptor.capture());
        final CanvasSelectionEvent selectionEvent = elementSelectedEventArgumentCaptor.getValue();
        assertEquals(1, selectionEvent.getIdentifiers().size());
        assertEquals(ELEMENT_UUID, selectionEvent.getIdentifiers().iterator().next());
    }

    private boolean isRegistered(Element e) {
        return tested.itemsRegistered().test(e.getUUID());
    }
}
