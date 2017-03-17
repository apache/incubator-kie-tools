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

import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.event.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.*;
import static com.ait.lienzo.shared.core.types.EventPropagationMode.FIRST_ANCESTOR;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeTest
{
    private WiresShape tested;

    private MultiPath path;

    @Mock
    private LayoutContainer layoutContainer;

    @Mock
    private HandlerRegistrationManager handlerRegistrationManager;

    @Mock
    private IAttributesChangedBatcher attributesChangedBatcher;

    @Mock
    private HandlerManager handlerManager;

    @Mock
    private WiresContainer parent;

    @Mock
    private MagnetManager.Magnets magnets;

    private Group group;

    @Before
    public void setup()
    {
        path = new MultiPath().rect(3, 7, 100, 100);
        group = spy(new Group());
        when(layoutContainer.getGroup()).thenReturn(group);
        when(layoutContainer.setOffset(any(Point2D.class))).thenReturn(layoutContainer);
        when(layoutContainer.setSize(anyDouble(), anyDouble())).thenReturn(layoutContainer);
        when(layoutContainer.execute()).thenReturn(layoutContainer);
        when(layoutContainer.refresh()).thenReturn(layoutContainer);
        tested = new WiresShape(path, layoutContainer, handlerManager, handlerRegistrationManager, attributesChangedBatcher);
    }

    @Test
    public void testInit()
    {
        assertNull(tested.getParent());
        assertNull(tested.getDockedTo());
        assertEquals(IContainmentAcceptor.ALL, tested.getContainmentAcceptor());
        assertEquals(IDockingAcceptor.ALL, tested.getDockingAcceptor());
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
    public void testSetDraggable()
    {
        tested.setDraggable(false);
        assertFalse(tested.getGroup().isDraggable());

        tested.setDraggable(true);
        assertTrue(tested.getGroup().isDraggable());
    }

    @Test
    public void testDraggableHandlers()
    {
        WiresShape.WiresShapeHandler handler = mock( WiresShape.WiresShapeHandler.class );
        tested.addWiresShapeHandler( handlerRegistrationManager, handler );
        verify(group, times(1)).addNodeMouseDownHandler(any(NodeMouseDownHandler.class));
        verify(group, times(1)).addNodeMouseUpHandler(any(NodeMouseUpHandler.class));
        verify(group, times(1)).setDragConstraints(any(DragConstraintEnforcer.class));
        verify(group, times(1)).addNodeDragEndHandler(any(NodeDragEndHandler.class));
        verify(handlerRegistrationManager, times(3)).register(any(HandlerRegistration.class));
    }

    @Test
    public void testXCoordinate()
    {
        tested.setX(100);
        assertEquals(100, tested.getGroup().getX(), 0);
    }

    @Test
    public void testSetResizable()
    {
        tested.setResizable(false);
        assertFalse(tested.isResizable());

        tested.setResizable(true);
        assertTrue(tested.isResizable());
    }

    @Test
    public void testYCoordinate()
    {
        tested.setY(100);
        assertEquals(100, tested.getGroup().getY(), 0);
    }

    @Test
    public void testAddChild()
    {
        IPrimitive<?> child = new Rectangle(10, 10);
        tested.addChild(child);

        verify(layoutContainer).add(child);
        // Initial MultiPath + new child
        verify(layoutContainer, times(2)).add(any(IPrimitive.class));
        verify(layoutContainer, never()).remove(any(IPrimitive.class));
        verify(layoutContainer, never()).add(eq(child), any(LayoutContainer.Layout.class));
    }

    @Test
    public void testAddChildWithLayout()
    {
        IPrimitive<?> child = new Rectangle(10, 10);
        LayoutContainer.Layout layout = LayoutContainer.Layout.CENTER;
        tested.addChild(child, layout);

        verify(layoutContainer).add(child, layout);
        // Initial MultiPath
        verify(layoutContainer).add(any(IPrimitive.class));
        verify(layoutContainer, never()).remove(any(IPrimitive.class));
    }

    @Test
    public void testRemoveChild()
    {
        IPrimitive<?> child = new Rectangle(10, 10);
        tested.removeChild(child);
        verify(layoutContainer, never()).add(child);
        verify(layoutContainer).add(any(IPrimitive.class));
        verify(layoutContainer).remove(child);
        verify(layoutContainer).remove(any(IPrimitive.class));
        verify(layoutContainer, never()).add(eq(child), any(LayoutContainer.Layout.class));
    }

    @Test
    public void testAddWiresHandlers()
    {
        WiresResizeStartHandler startHandler = mock(WiresResizeStartHandler.class);
        WiresResizeStepHandler stepHandler = mock(WiresResizeStepHandler.class);
        WiresResizeEndHandler endHandler = mock(WiresResizeEndHandler.class);
        tested.addWiresResizeStartHandler(startHandler);
        tested.addWiresResizeStepHandler(stepHandler);
        tested.addWiresResizeEndHandler(endHandler);

        verify(handlerManager).addHandler(WiresResizeStartEvent.TYPE, startHandler);
        verify(handlerManager).addHandler(WiresResizeStepEvent.TYPE, stepHandler);
        verify(handlerManager).addHandler(WiresResizeEndEvent.TYPE, endHandler);
    }

    @Test
    public void testDestroy()
    {
        WiresShapeControlHandleList controls = mock(WiresShapeControlHandleList.class);
        WiresShape shape = spy(new WiresShape(path, layoutContainer));
        doReturn(controls).when(shape).getControls();
        shape.destroy();

        verify(layoutContainer).destroy();
        verify(controls).destroy();
        verify(shape).removeFromParent();
    }

    @Test
    public void testRemoveFromParent()
    {
        WiresShape shape = spy(new WiresShape(path, layoutContainer));

        // No null pointer expected
        shape.removeFromParent();

        shape.setParent(parent);
        shape.removeFromParent();

        verify(parent).remove(shape);
    }

    @Test
    public void testLoadControls()
    {
        assertNull(tested.getControls());

        assertNotNull(tested.loadControls(RESIZE));
        assertNotNull(tested.getControls());

        assertNull(tested.loadControls(CONNECTOR));
        assertNull(tested.getControls());

        assertNotNull(tested.loadControls(POINT));
        assertNotNull(tested.getControls());

        assertNull(tested.loadControls(null));
        assertNull(tested.getControls());
    }

    @Test
    public void testSetMagnets()
    {
        assertNull(tested.getMagnets());

        tested.setMagnets(magnets);
        assertNotNull(tested.getMagnets());
    }

    @Test
    public void testRefresh()
    {
        tested = spy(tested);

        WiresShapeControlHandleList controls = mock(WiresShapeControlHandleList.class);
        doReturn(controls).when(tested).createControlHandles(eq(RESIZE), any(ControlHandleList.class));

        tested.refresh();

        verify(controls).refresh();
    }
}
