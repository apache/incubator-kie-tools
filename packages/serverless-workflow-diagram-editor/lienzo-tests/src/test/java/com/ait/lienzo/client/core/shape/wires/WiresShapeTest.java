/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerManager;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import com.ait.lienzo.tools.client.event.INodeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.CONNECTOR;
import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.POINT;
import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.RESIZE;
import static com.ait.lienzo.shared.core.types.EventPropagationMode.FIRST_ANCESTOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeTest {

    private WiresShape tested;

    private MultiPath path;

    @Mock
    private LayoutContainer layoutContainer;

    @Mock
    private HandlerRegistrationManager handlerRegistrationManager;

    @Mock
    private HandlerManager handlerManager;

    @Mock
    private WiresContainer parent;

    @Mock
    private MagnetManager.Magnets magnets;

    private Group group;

    @Before
    public void setup() {
        path = spy(new MultiPath().rect(3, 7, 100, 100));
        group = spy(new Group());
        when(layoutContainer.getGroup()).thenReturn(group);
        when(layoutContainer.setOffset(any(Point2D.class))).thenReturn(layoutContainer);
        when(layoutContainer.setSize(anyDouble(), anyDouble())).thenReturn(layoutContainer);
        when(layoutContainer.execute()).thenReturn(layoutContainer);
        when(layoutContainer.refresh()).thenReturn(layoutContainer);
        tested = spy(new WiresShape(path, layoutContainer, handlerManager, handlerRegistrationManager));
    }

    @Test
    public void testInit() {
        assertNull(tested.getParent());
        assertNull(tested.getDockedTo());
        assertEquals(path, tested.getPath());
        assertEquals(0, tested.getChildShapes().size());
        verify(layoutContainer).setOffset(any(Point2D.class));
        verify(layoutContainer).setSize(anyDouble(), anyDouble());
        verify(layoutContainer).execute();
        verify(layoutContainer, never()).refresh();
        verify(layoutContainer).add(path);

        verify(group).setEventPropagationMode(FIRST_ANCESTOR);
    }

    @Test
    public void testSetDraggable() {
        tested.setDraggable(false);
        assertFalse(tested.getGroup().isDraggable());

        tested.setDraggable(true);
        assertTrue(tested.getGroup().isDraggable());
    }

    @Test
    public void testDraggableHandlers() {
        final WiresShapeHandler handler = mock(WiresShapeHandler.class);
        WiresManager.addWiresShapeHandler(tested,
                                          handlerRegistrationManager,
                                          handler);
        verify(path, times(1)).addNodeMouseClickHandler(any(NodeMouseClickHandler.class));
        verify(path, times(1)).addNodeMouseDownHandler(any(NodeMouseDownHandler.class));
        verify(path, times(1)).addNodeMouseUpHandler(any(NodeMouseUpHandler.class));
        verify(group, times(1)).setDragConstraints(any(DragConstraintEnforcer.class));
        verify(group, times(1)).addNodeDragEndHandler(any(NodeDragEndHandler.class));
        verify(handlerRegistrationManager, times(4)).register(any(HandlerRegistration.class));
    }

    @Test
    public void testLocation() {
        final Point2D location = new Point2D(11, 55.5);
        tested.setLocation(location);
        assertEquals(location, tested.getLocation());
        assertEquals(location, tested.getGroup().getLocation());
    }

    @Test
    public void testSetResizable() {
        tested.setResizable(false);
        assertFalse(tested.isResizable());

        tested.setResizable(true);
        assertTrue(tested.isResizable());
    }

    @Test
    public void testAddChild() {
        final IPrimitive<?> child = new Rectangle(10, 10);
        tested.addChild(child);

        verify(layoutContainer).add(child);
        // Initial MultiPath + new child
        verify(layoutContainer, times(2)).add(any(IPrimitive.class));
        verify(layoutContainer, never()).remove(any(IPrimitive.class));
        verify(layoutContainer, never()).add(eq(child), any(LayoutContainer.Layout.class));
    }

    @Test
    public void testAddChildWithLayout() {
        final IPrimitive<?> child = new Rectangle(10, 10);
        final LayoutContainer.Layout layout = LayoutContainer.Layout.CENTER;
        tested.addChild(child, layout);

        verify(layoutContainer).add(child, layout);
        // Initial MultiPath
        verify(layoutContainer).add(any(IPrimitive.class));
        verify(layoutContainer, never()).remove(any(IPrimitive.class));
    }

    @Test
    public void testRemoveChild() {
        final IPrimitive<?> child = new Rectangle(10, 10);
        tested.removeChild(child);
        verify(layoutContainer, never()).add(child);
        verify(layoutContainer).add(any(IPrimitive.class));
        verify(layoutContainer).remove(child);
        verify(layoutContainer).remove(any(IPrimitive.class));
        verify(layoutContainer, never()).add(eq(child), any(LayoutContainer.Layout.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddWiresHandlers() {
        final WiresResizeStartHandler startHandler = mock(WiresResizeStartHandler.class);
        final WiresResizeStepHandler stepHandler = mock(WiresResizeStepHandler.class);
        final WiresResizeEndHandler endHandler = mock(WiresResizeEndHandler.class);
        tested.addWiresResizeStartHandler(startHandler);
        tested.addWiresResizeStepHandler(stepHandler);
        tested.addWiresResizeEndHandler(endHandler);

        verify(handlerManager).addHandler(WiresResizeStartEvent.TYPE, startHandler);
        verify(handlerManager).addHandler(WiresResizeStepEvent.TYPE, stepHandler);

        final HandlerRegistration registration = mock(HandlerRegistration.class);
        doAnswer((Answer<HandlerRegistration>) invocationOnMock -> {
            if (WiresResizeEndEvent.TYPE.equals(invocationOnMock.getArguments()[0])) {
                final WiresResizeEndHandler handler = (WiresResizeEndHandler) invocationOnMock.getArguments()[1];
                final WiresResizeEndEvent endEvent = mock(WiresResizeEndEvent.class);
                handler.onShapeResizeEnd(endEvent);
                verify(endHandler, times(1)).onShapeResizeEnd(eq(endEvent));
            }
            return registration;
        }).when(handlerManager).addHandler(any(INodeEvent.Type.class), any(EventHandler.class));
    }

    @Test
    public void testListen() {
        tested.listen(true);
        assertTrue(tested.isListening());
        tested.listen(false);
        assertFalse(tested.isListening());
    }

    @Test
    public void testDestroy() {
        final WiresShapeControlHandleList controls = mock(WiresShapeControlHandleList.class);
        final WiresShape shape = spy(new WiresShape(path, layoutContainer));
        final WiresShapeControl shapeControl = mock(WiresShapeControl.class);
        doReturn(controls).when(shape).getControls();
        shape.setControl(shapeControl);
        shape.destroy();

        verify(layoutContainer).destroy();
        verify(controls).destroy();
        verify(shape).removeFromParent();
        verify(shapeControl).destroy();
    }

    @Test
    public void testRemoveFromParent() {
        final WiresShape shape = spy(new WiresShape(path, layoutContainer));

        // No null pointer expected
        shape.removeFromParent();

        shape.setParent(parent);
        shape.removeFromParent();

        verify(parent).remove(shape);
    }

    @Test
    public void testLoadControls() {
        assertNull(tested.getControls());

        assertNull(tested.loadControls(null));
        assertNull(tested.getControls());

        assertNull(tested.loadControls(CONNECTOR));
        assertNull(tested.getControls());
        verify(path).getControlHandles(CONNECTOR);
        verify(tested, never()).createControlHandles(eq(CONNECTOR), any(ControlHandleList.class));

        assertNotNull(tested.loadControls(RESIZE));
        assertNotNull(tested.getControls());
        verify(path).getControlHandles(RESIZE);
        verify(tested).createControlHandles(eq(RESIZE), any(ControlHandleList.class));

        assertNotNull(tested.loadControls(POINT));
        assertNotNull(tested.getControls());
        verify(path).getControlHandles(POINT);
        verify(tested, never()).createControlHandles(eq(POINT), any(ControlHandleList.class));
    }

    @Test
    public void testSetMagnets() {
        assertNull(tested.getMagnets());

        tested.setMagnets(magnets);
        assertNotNull(tested.getMagnets());
    }

    @Test
    public void testRefresh() {
        final WiresShapeControlHandleList controls = mock(WiresShapeControlHandleList.class);
        doReturn(controls).when(tested).createControlHandles(eq(RESIZE), any(ControlHandleList.class));

        tested.refresh();

        verify(controls).refresh();
    }

    @Test
    public void testEquals() {
        WiresShape shape1 = new WiresShape(path, layoutContainer, handlerManager, handlerRegistrationManager);
        assertEquals(shape1, shape1);
        assertFalse(shape1.equals(null));
        assertFalse(shape1.equals(new Object()));

        WiresShape shape2 = new WiresShape(path, layoutContainer, handlerManager, handlerRegistrationManager);
        assertEquals(shape1, shape2);
        assertEquals(shape2, shape1);

        Group group2 = new Group();
        when(layoutContainer.getGroup()).thenReturn(group2);
        shape2 = new WiresShape(path, layoutContainer, handlerManager, handlerRegistrationManager);
        assertNotEquals(shape1, shape2);
        assertNotEquals(shape2, shape1);
    }
}
