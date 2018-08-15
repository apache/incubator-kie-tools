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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.Collections;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MapSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoSelectionControlTest {

    private static final String ELEMENT_UUID = "element-uuid1";

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    @Mock
    private EventSourceMock<CanvasClearSelectionEvent> clearSelectionEvent;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Layer layer;

    @Mock
    private Object definition;

    @Mock
    private Element element;

    @Mock
    private Shape<ShapeView> shape;

    @Mock
    private HasEventHandlers<ShapeViewExtStub, Object> shapeViewHandlers;

    @Mock
    private MapSelectionControl<AbstractCanvasHandler> selectionControl;

    private LienzoSelectionControl<AbstractCanvasHandler> tested;
    private ShapeViewExtStub shapeView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.getContent()).thenReturn(new ViewImpl<>(definition,
                                                             BoundsImpl.build(0, 0, 10, 10)));
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getLayer()).thenReturn(layer);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(selectionControl.getCanvasHandler()).thenReturn(canvasHandler);
        when(selectionControl.getCanvas()).thenReturn(canvas);
        when(shapeViewHandlers.supports(any(ViewEventType.class))).thenReturn(true);
        shapeView = new ShapeViewExtStub(shapeViewHandlers, null);
        when(shape.getShapeView()).thenReturn(shapeView);
        tested = new LienzoSelectionControl<>(selectionControl,
                                              canvasSelectionEvent,
                                              clearSelectionEvent);
    }

    @Test
    public void testInit() {
        tested.init(canvasHandler);
        verify(selectionControl, times(1)).init(eq(canvasHandler));
    }

    @Test
    public void testRegisterAndClick() {
        tested.init(canvasHandler);
        tested.register(element);
        verify(selectionControl, times(1)).register(eq(element));
        ArgumentCaptor<MouseClickHandler> clickHandlerCaptor = ArgumentCaptor.forClass(MouseClickHandler.class);
        verify(shapeViewHandlers, times(1)).supports(eq(ViewEventType.MOUSE_CLICK));
        verify(shapeViewHandlers, times(1)).addHandler(eq(ViewEventType.MOUSE_CLICK),
                                                       clickHandlerCaptor.capture());
        final MouseClickHandler clickHandler = clickHandlerCaptor.getValue();
        assertEquals(clickHandler, tested.getHandlers().get(ELEMENT_UUID));
        MouseClickEvent event = mock(MouseClickEvent.class);
        when(event.isButtonLeft()).thenReturn(true);
        clickHandler.handle(event);
        verify(selectionControl, times(1)).select(eq(element.getUUID()));
    }

    @Test
    public void testSelectionIsSingle() {
        when(selectionControl.getSelectedItems()).thenReturn(Collections.singletonList(ELEMENT_UUID));
        tested.init(canvasHandler);
        tested.register(element);
        tested.singleSelect(element);
        verify(selectionControl, times(1)).clearSelection();
        verify(selectionControl, times(1)).select(eq(element.getUUID()));
    }

    @Test
    public void testClearSelection() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.singleSelect(element);
        tested.clearSelection();
        verify(selectionControl, times(1)).clearSelection();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregister() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.deregister(element);
        verify(selectionControl, times(1)).deregister(eq(element));
        verify(shapeViewHandlers, times(1)).removeHandler(any(ViewHandler.class));
        assertTrue(tested.getHandlers().isEmpty());
    }
}
