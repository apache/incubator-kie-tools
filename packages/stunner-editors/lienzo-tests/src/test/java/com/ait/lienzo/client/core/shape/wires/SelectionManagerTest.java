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

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.OnEventHandlers;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresCompositeShapeHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.EventType;
import com.ait.lienzo.tools.client.event.MouseEventUtil;
import elemental2.dom.Document;
import elemental2.dom.HTMLHtmlElement;
import elemental2.dom.MouseEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SelectionManagerTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private SelectionManager.SelectionShapeProvider selectionShapeProvider;

    @Mock
    private SelectionManager.SelectionDragHandler selectionDragHandler;

    @Mock
    private Shape<?> selectionShape;

    @Mock
    private NodeMouseDownEvent mouseEvent;

    @Mock
    private NodeDragEndEvent dragEndEvent;

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private Layer layer;

    private OnEventHandlers onEventHandlers;

    @Mock
    private WiresControlFactory factory;

    @Mock
    private Layer overLayer;

    @Mock
    private Transform transform;

    @Captor
    private ArgumentCaptor<SelectionManager.OnMouseXEventHandler> onMouseXEventHandlerArgumentCaptor;

    private SelectionManager.OnMouseXEventHandler onMouseXEventHandler;

    private SelectionManager manager;

    private SelectionManager realManager;

    @Before
    public void setup() {
        Viewport viewport = spy(new Viewport());
        onEventHandlers = spy(new OnEventHandlers());
        Document document = new Document();
        viewport.getElement().ownerDocument = document;
        document.documentElement = mock(HTMLHtmlElement.class);
        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(wiresManager.getControlFactory()).thenReturn(factory);
        when(wiresLayer.getLayer()).thenReturn(layer);
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getOnEventHandlers()).thenReturn(onEventHandlers);
        when(viewport.getOverLayer()).thenReturn(overLayer);
        when(viewport.getTransform()).thenReturn(transform);
        when(selectionShapeProvider.setLocation(any(Point2D.class))).thenReturn(selectionShapeProvider);
        when(selectionShapeProvider.setSize(anyDouble(), anyDouble())).thenReturn(selectionShapeProvider);
        when(selectionShapeProvider.getShape()).thenReturn(selectionShape);

        realManager = new SelectionManager(wiresManager);
        manager = spy(realManager);
        manager.setSelectionShapeProvider(selectionShapeProvider);

        verify(onEventHandlers).setOnMouseMoveEventHandle(onMouseXEventHandlerArgumentCaptor.capture());

        onMouseXEventHandler = spy(onMouseXEventHandlerArgumentCaptor.getValue());
    }

    @Test
    public void testOnlyLeftMouseButtonCanStartSelection() {
        when(mouseEvent.isButtonLeft()).thenReturn(false);
        manager.onNodeMouseDown(mouseEvent);
        assertFalse("Selection should be started by Left mouse button ONLY", manager.isSelectionCreationInProcess());
        verify(layer, times(0)).draw();

        when(mouseEvent.isButtonLeft()).thenReturn(true);
        manager.onNodeMouseDown(mouseEvent);
        assertTrue("Selection should be started by Left mouse button", manager.isSelectionCreationInProcess());
        verify(layer, times(1)).draw();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDoNotCleanSelectionForNodeDuringSelectionMove() {
        realManager.setSelectionShapeProvider(selectionShapeProvider);
        realManager.setSelectionCreationInProcess(false);

        SelectionManager.SelectedItems selectedItems = spy(realManager.getSelectedItems());
        realManager.setSelectedItems(selectedItems);

        final MouseEvent mouseEvent = mock(MouseEvent.class);
        mouseEvent.button = MouseEventUtil.BUTTON_LEFT;
        mouseEvent.type = EventType.MOUSE_UP.getType();
        onMouseXEventHandler.onMouseEventBefore(mouseEvent);

        verify(selectedItems, times(1)).notifyListener();
    }

    @Test
    public void testNodesHighlightedAfterSelection() {
        realManager.setSelectionShapeProvider(selectionShapeProvider);
        realManager.setSelectionCreationInProcess(true);

        SelectionManager.SelectedItems selectedItems = spy(realManager.getSelectedItems());
        realManager.setSelectedItems(selectedItems);
        when(selectionShape.getLayer()).thenReturn(layer);

        final NodeMouseDownEvent nodeMouseEvent = mock(NodeMouseDownEvent.class);
        when(nodeMouseEvent.isButtonLeft()).thenReturn(true);
        when(nodeMouseEvent.getX()).thenReturn(0);
        when(nodeMouseEvent.getY()).thenReturn(0);
        realManager.onNodeMouseDown(nodeMouseEvent);

        final MouseEvent mouseEvent = mock(MouseEvent.class);
        mouseEvent.button = MouseEventUtil.BUTTON_LEFT;
        mouseEvent.type = EventType.MOUSE_UP.getType();
        onMouseXEventHandler.onMouseEventBefore(mouseEvent);

        verify(selectedItems, never()).notifyListener();
        verify(selectedItems, times(1)).selectShapes();
    }

    @Test
    public void testReinforceSelectionShapeOnTop() {
        Layer layer = mock(Layer.class);
        selectionDragHandler.m_selectionManager = manager;
        selectionDragHandler.m_selectionManager.m_startBoundingBox = mock(BoundingBox.class);
        selectionDragHandler.multipleShapeHandler = mock(WiresCompositeShapeHandler.class);
        doCallRealMethod().when(selectionDragHandler).onNodeDragEnd(dragEndEvent);
        doCallRealMethod().when(selectionDragHandler).reinforceSelectionShapeOnTop();
        when(selectionShape.getLayer()).thenReturn(layer);
        doNothing().when(selectionDragHandler).updateSelectionShapeForExternallyConnectedConnectors(anyInt(),
                                                                                                    anyInt(),
                                                                                                    any(BoundingBox.class));
        selectionDragHandler.onNodeDragEnd(dragEndEvent);

        verify(layer, times(1)).moveToTop(selectionShape);
    }

    @Test
    public void testReinforceSelectionShapeOnTopShapeNull() {
        SelectionManager selectionManager = mock(SelectionManager.class);
        selectionDragHandler.m_selectionManager = selectionManager;
        selectionDragHandler.m_selectionManager.m_startBoundingBox = mock(BoundingBox.class);
        selectionDragHandler.multipleShapeHandler = mock(WiresCompositeShapeHandler.class);
        doCallRealMethod().when(selectionDragHandler).onNodeDragEnd(dragEndEvent);
        doCallRealMethod().when(selectionDragHandler).reinforceSelectionShapeOnTop();
        doCallRealMethod().when(selectionManager).getSelectionShape();
        Shape<?> shape = selectionDragHandler.m_selectionManager.getSelectionShape();
        when(selectionShapeProvider.getShape()).thenReturn(shape);
        doNothing().when(selectionDragHandler).updateSelectionShapeForExternallyConnectedConnectors(anyInt(),
                                                                                                    anyInt(),
                                                                                                    any(BoundingBox.class));
        selectionDragHandler.onNodeDragEnd(dragEndEvent);

        verify(layer, never()).moveToTop(selectionShape);
    }

    @Test
    public void testDrawSelectionShape() {
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        final double x = 10;
        final double y = 20;
        final double translateX = 40;
        final double translateY = 80;
        final double scaleX = 2;
        final double scaleY = 2;
        final double expectedX = 10;
        final double expectedY = 20;
        final double expectedWidth = -30;
        final double expectedHeight = -60;
        final Transform transform = Transform.makeFromValues(scaleX, 0, 0, scaleY, translateX, translateY);

        doReturn(transform).when(manager).getViewportTransform();
        doReturn(x).when(manager).relativeStartX();
        doReturn(y).when(manager).relativeStartY();
        doReturn(manager).when(onMouseXEventHandler).getSelectionManager();

        onMouseXEventHandler.drawSelectionShape(mouseEvent, 0, 0);

        verify(manager).drawSelectionShape(eq(expectedX), eq(expectedY), eq(expectedWidth), eq(expectedHeight), eq(overLayer));
        verify(overLayer).draw();
        verify(selectionShape, never()).moveToTop();
    }

    @Test
    public void testDrawSelectionShapeWhenHeightAndWidthAreZero() {
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        final double x = 10;
        final double y = 20;
        final double translateX = -10;
        final double translateY = -20;
        final double scaleX = 1;
        final double scaleY = 1;
        final double expectedX = 10;
        final double expectedY = 20;
        final double expectedWidth = 1;
        final double expectedHeight = 1;
        final Transform transform = Transform.makeFromValues(scaleX, 0, 0, scaleY, translateX, translateY);

        doReturn(transform).when(manager).getViewportTransform();
        doReturn(x).when(manager).relativeStartX();
        doReturn(y).when(manager).relativeStartY();
        doReturn(manager).when(onMouseXEventHandler).getSelectionManager();
        doNothing().when(manager).drawSelectionShape(anyInt(), anyInt(), anyInt(), anyInt(), any(Layer.class));

        onMouseXEventHandler.drawSelectionShape(mouseEvent, 0, 0);

        verify(manager).drawSelectionShape(eq(expectedX), eq(expectedY), eq(expectedWidth), eq(expectedHeight), eq(overLayer));
        verify(overLayer).draw();
    }

    @Test
    public void testRelativeStartX() {
        final double startX = 20;
        final double startY = 25;
        final Point2D start = new Point2D(startX, startY);
        final double translateX = 10d;
        final double scaleX = 2d;
        final Transform transform = Transform.makeFromValues(scaleX, 0, 0, 1, translateX, 1);

        doReturn(transform).when(manager).getViewportTransform();
        doReturn(start).when(manager).getStart();

        final Double relativeStartX = manager.relativeStartX();

        assertEquals(5d, relativeStartX, 0);
    }

    @Test
    public void testRelativeStartY() {
        final double startX = 25;
        final double startY = 20;
        final Point2D start = new Point2D(startX, startY);
        final double translateY = 10d;
        final double scaleY = 2d;
        final Transform transform = Transform.makeFromValues(1, 0, 0, scaleY, 1, translateY);

        doReturn(transform).when(manager).getViewportTransform();
        doReturn(start).when(manager).getStart();

        final Double relativeStartY = manager.relativeStartY();

        assertEquals(5d, relativeStartY, 0);
    }

    @Test
    public void testDestroy() {
        manager.destroy();

        assertNull(onEventHandlers.getOnMouseClickEventHandle());
        assertNull(onEventHandlers.getOnMouseDoubleClickEventHandle());
        assertNull(onEventHandlers.getOnMouseDownEventHandle());
        assertNull(onEventHandlers.getOnMouseMoveEventHandle());
        assertNull(onEventHandlers.getOnMouseUpEventHandle());
    }
}
