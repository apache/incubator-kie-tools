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


package org.kie.workbench.common.stunner.core.client.canvas.controls.select;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
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
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
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
    private Diagram diagram;

    @Mock
    private Index index;

    @Mock
    private Metadata metadata;

    @Mock
    private Object definition;

    @Mock
    private Object rootDefinition;

    @Mock
    private Element element;

    @Mock
    private Element rootElement;

    @Mock
    private Shape<ShapeView> shape;

    @Mock
    private HasEventHandlers<ShapeViewExtStub, Object> shapeEventHandler;

    @Mock
    private HasControlPoints<ShapeViewExtStub> hasControlPoints;

    @Captor
    private ArgumentCaptor<CanvasClearSelectionEvent> canvasClearSelectionEventCaptor;

    private ShapeViewExtStub shapeView;

    private MapSelectionControl<AbstractCanvasHandler> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        ShapeViewExtStub shapeView = new ShapeViewExtStub(shapeEventHandler,
                                                          hasControlPoints);
        when(rootElement.getUUID()).thenReturn(ROOT_UUID);
        when(rootElement.getContent()).thenReturn(new ViewImpl<>(rootDefinition,
                                                                 Bounds.create(0, 0, 10, 10)));
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.getContent()).thenReturn(new ViewImpl<>(definition,
                                                             Bounds.create(0, 0, 10, 10)));
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(canvas.getShape(ELEMENT_UUID)).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(shapeEventHandler.supports(ViewEventType.MOUSE_CLICK)).thenReturn(true);
        this.tested = new MapSelectionControl(e -> elementSelectedEvent.fire((CanvasSelectionEvent) e),
                                              e -> clearSelectionEvent.fire((CanvasClearSelectionEvent) e));
        this.tested.setReadonly(false);
    }

    @Test
    public void testEnable() {
        tested.init(canvasHandler);
        verify(canvas,
               times(1)).addHandler(eq(ViewEventType.MOUSE_CLICK),
                                    any(MouseClickHandler.class));
    }

    @Test
    public void testLayerClickAndSelectRootElement() {
        tested.init(canvasHandler);
        final ArgumentCaptor<MouseClickHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(MouseClickHandler.class);
        verify(canvas,
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
        verify(canvas,
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
        tested.select(element.getUUID());
        assertEquals(1, tested.getSelectedItems().size());
        assertEquals(ELEMENT_UUID, tested.getSelectedItems().iterator().next());
        verify(shape, times(1)).applyState(ShapeState.SELECTED);
        verify(shape, never()).applyState(ShapeState.NONE);
        verify(shape, never()).applyState(ShapeState.INVALID);
        verify(shape, never()).applyState(ShapeState.HIGHLIGHT);
        verify(canvas, times(1)).focus();
        final ArgumentCaptor<CanvasSelectionEvent> elementSelectedEventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(elementSelectedEvent,
               times(1)).fire(elementSelectedEventArgumentCaptor.capture());
        final CanvasSelectionEvent event = elementSelectedEventArgumentCaptor.getValue();
        assertEquals(1, event.getIdentifiers().size());
        assertEquals(ELEMENT_UUID, event.getIdentifiers().iterator().next());
    }

    @Test
    public void testAddSelection() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.addSelection(element.getUUID());
        assertEquals(1, tested.getSelectedItems().size());
        assertEquals(ELEMENT_UUID, tested.getSelectedItems().iterator().next());
        verify(shape, times(1)).applyState(ShapeState.SELECTED);
        verify(shape, never()).applyState(ShapeState.NONE);
        verify(shape, never()).applyState(ShapeState.INVALID);
        verify(shape, never()).applyState(ShapeState.HIGHLIGHT);
        verify(canvas, times(1)).focus();
        final ArgumentCaptor<CanvasSelectionEvent> elementSelectedEventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(elementSelectedEvent,
               never()).fire(elementSelectedEventArgumentCaptor.capture()); //must not fire event
    }

    @Test
    public void testSelectOnlyOnce() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element.getUUID());
        tested.select(element.getUUID());

        assertEquals(1, tested.getSelectedItems().size());
        assertEquals(ELEMENT_UUID, tested.getSelectedItems().iterator().next());
        verify(shape, times(2)).applyState(ShapeState.SELECTED);
        verify(shape, never()).applyState(ShapeState.NONE);
        verify(shape, never()).applyState(ShapeState.INVALID);
        verify(shape, never()).applyState(ShapeState.HIGHLIGHT);
        verify(canvas, times(2)).focus();
        final ArgumentCaptor<CanvasSelectionEvent> elementSelectedEventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        // Verify it has only been fired once
        verify(elementSelectedEvent,
               times(2)).fire(elementSelectedEventArgumentCaptor.capture());
        final CanvasSelectionEvent event = elementSelectedEventArgumentCaptor.getValue();
        assertEquals(1, event.getIdentifiers().size());
        assertEquals(ELEMENT_UUID, event.getIdentifiers().iterator().next());
    }

    @Test
    public void testSelectReadOnly() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.setReadonly(true);
        tested.select(element.getUUID());
        verify(shape, never()).applyState(ShapeState.SELECTED);
        verify(shape, never()).applyState(ShapeState.NONE);
        verify(shape, never()).applyState(ShapeState.INVALID);
        verify(shape, times(1)).applyState(ShapeState.HIGHLIGHT);
        verify(canvas, times(1)).focus();
    }

    @Test
    public void testDeselect() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element.getUUID());
        tested.deselect(element.getUUID());
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(shape, times(1)).applyState(ShapeState.SELECTED);
        verify(shape, times(1)).applyState(ShapeState.NONE);
        verify(shape, never()).applyState(ShapeState.INVALID);
        verify(shape, never()).applyState(ShapeState.HIGHLIGHT);
    }

    @Test
    public void testClearSelection() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element.getUUID());
        tested.clearSelection();
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(shape, times(1)).applyState(ShapeState.SELECTED);
        verify(shape, times(1)).applyState(ShapeState.NONE);
        verify(shape, never()).applyState(ShapeState.INVALID);
        verify(shape, never()).applyState(ShapeState.HIGHLIGHT);
        verify(clearSelectionEvent,
               times(1)).fire(any(CanvasClearSelectionEvent.class));
    }

    @Test
    public void testGetSelectedItemDefinitionWithNoItemsSelected() {
        tested.init(canvasHandler);

        when(index.get(ROOT_UUID)).thenReturn(rootElement);

        final Optional<Object> selectedItemDefinition = tested.getSelectedItemDefinition();
        assertTrue(selectedItemDefinition.isPresent());
        assertEquals(rootElement, selectedItemDefinition.get());
    }

    @Test
    public void testGetSelectedItemDefinitionWithNoItemsSelectedAndNoDiagram() {
        tested.init(canvasHandler);

        when(canvasHandler.getDiagram()).thenReturn(null);

        final Optional<Object> selectedItemDefinition = tested.getSelectedItemDefinition();
        assertFalse(selectedItemDefinition.isPresent());
    }

    @Test
    public void testGetSelectedItemDefinitionWithOneItemSelected() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element.getUUID());

        when(index.get(ELEMENT_UUID)).thenReturn(element);

        final Optional<Object> selectedItemDefinition = tested.getSelectedItemDefinition();
        assertTrue(selectedItemDefinition.isPresent());
        assertEquals(element, selectedItemDefinition.get());
    }

    @Test
    public void testGetSelectedItemDefinitionWithMultipleItemsSelected() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.register(rootElement);
        tested.select(Stream.of(ROOT_UUID, ELEMENT_UUID).collect(Collectors.toSet()));

        when(index.get(ELEMENT_UUID)).thenReturn(element);
        when(index.get(ROOT_UUID)).thenReturn(rootElement);

        final Optional<Object> selectedItemDefinition = tested.getSelectedItemDefinition();
        assertTrue(selectedItemDefinition.isPresent());
        assertEquals(rootElement, selectedItemDefinition.get());
    }

    @Test
    public void testOnShapeRemovedEvent() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element.getUUID());
        CanvasShapeRemovedEvent shapeRemovedEvent = new CanvasShapeRemovedEvent(canvas,
                                                                                shape);
        tested.onShapeRemoved(shapeRemovedEvent);
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(shape, times(1)).applyState(ShapeState.SELECTED);
    }

    @Test
    public void testOnClearSelectionEvent() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element.getUUID());
        CanvasClearSelectionEvent event = new CanvasClearSelectionEvent(canvasHandler);
        tested.onCanvasClearSelection(event);
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(shape, times(1)).applyState(ShapeState.SELECTED);
        verify(shape, times(1)).applyState(ShapeState.NONE);
        verify(shape, never()).applyState(ShapeState.INVALID);
        verify(shape, never()).applyState(ShapeState.HIGHLIGHT);
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
        verify(shape, times(1)).applyState(ShapeState.SELECTED);
        verify(shape, times(1)).applyState(ShapeState.NONE);
        verify(shape, never()).applyState(ShapeState.INVALID);
        verify(shape, never()).applyState(ShapeState.HIGHLIGHT);
        final ArgumentCaptor<CanvasSelectionEvent> elementSelectedEventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(elementSelectedEvent,
               times(1)).fire(elementSelectedEventArgumentCaptor.capture());
        final CanvasSelectionEvent selectionEvent = elementSelectedEventArgumentCaptor.getValue();
        assertEquals(1, selectionEvent.getIdentifiers().size());
        assertEquals(ELEMENT_UUID, selectionEvent.getIdentifiers().iterator().next());
    }

    @Test
    public void testClear() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.select(element.getUUID());
        tested.clear();
        assertTrue(tested.getSelectedItems().isEmpty());
        verify(clearSelectionEvent,
               times(1)).fire(canvasClearSelectionEventCaptor.capture());
        assertEquals(canvasHandler,
                     canvasClearSelectionEventCaptor.getValue().getCanvasHandler());
    }

    @Test
    public void testDestroy() {
        tested.init(canvasHandler);
        tested.destroy();
        assertFalse(tested.getSelectedItemDefinition().isPresent());
        verify(canvas).removeHandler(any(MouseClickHandler.class));
    }

    private boolean isRegistered(Element e) {
        return tested.itemsRegistered().test(e.getUUID());
    }
}
