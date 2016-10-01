/*
 *
 *    Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeControlHandleListTest
{
    WiresShapeControlHandleList              tested;

    @Mock
    WiresShape                               shape;

    @Mock
    HandlerRegistrationManager               handlerRegistrationManager;

    @Mock
    ControlHandleList                        controlHandleList;

    @Mock
    IControlHandle                           handle0;

    IPrimitive<?>                            primitive0;

    @Mock
    IControlHandle                           handle1;

    IPrimitive<?>                            primitive1;

    @Mock
    IControlHandle                           handle2;

    IPrimitive<?>                            primitive2;

    @Mock
    IControlHandle                           handle3;

    IPrimitive<?>                            primitive3;

    private Layer                            layer;

    private Group                            group;

    private final NFastArrayList<WiresShape> children = new NFastArrayList<>();

    @Before
    public void setup()
    {
        layer = new Layer();
        group = new Group();
        layer.add(group);
        doReturn(group).when(shape).getGroup();
        doReturn(children).when(shape).getChildShapes();
        doReturn(false).when(controlHandleList).isEmpty();
        doReturn(true).when(controlHandleList).isActive();
        doReturn(4).when(controlHandleList).size();
        this.primitive0 = spy(new Circle(20));
        group.add(primitive0);
        this.primitive1 = spy(new Circle(20));
        group.add(primitive1);
        this.primitive2 = spy(new Circle(20));
        group.add(primitive2);
        this.primitive3 = spy(new Circle(20));
        group.add(primitive3);
        doReturn(handle0).when(controlHandleList).getHandle(eq(0));
        doReturn(primitive0).when(handle0).getControl();
        doReturn(handle1).when(controlHandleList).getHandle(eq(1));
        doReturn(primitive1).when(handle1).getControl();
        doReturn(handle2).when(controlHandleList).getHandle(eq(2));
        doReturn(primitive2).when(handle2).getControl();
        doReturn(handle3).when(controlHandleList).getHandle(eq(3));
        doReturn(primitive3).when(handle3).getControl();

        tested = new WiresShapeControlHandleList(shape, IControlHandle.ControlHandleStandardType.RESIZE, controlHandleList, handlerRegistrationManager);
    }

    @Test
    public void testInitHandlers()
    {
        verify(shape, times(1)).addWiresMoveHandler(any(WiresMoveHandler.class));
        verify(shape, times(1)).addWiresDragStartHandler(any(WiresDragStartHandler.class));
        verify(shape, times(1)).addWiresDragMoveHandler(any(WiresDragMoveHandler.class));
        verify(shape, times(1)).addWiresDragEndHandler(any(WiresDragEndHandler.class));
        verify(primitive0, times(1)).addNodeDragStartHandler(any(NodeDragStartHandler.class));
        verify(primitive0, times(1)).addNodeDragMoveHandler(any(NodeDragMoveHandler.class));
        verify(primitive0, times(1)).addNodeDragEndHandler(any(NodeDragEndHandler.class));
        verify(primitive1, times(1)).addNodeDragStartHandler(any(NodeDragStartHandler.class));
        verify(primitive1, times(1)).addNodeDragMoveHandler(any(NodeDragMoveHandler.class));
        verify(primitive1, times(1)).addNodeDragEndHandler(any(NodeDragEndHandler.class));
        verify(primitive2, times(1)).addNodeDragStartHandler(any(NodeDragStartHandler.class));
        verify(primitive2, times(1)).addNodeDragMoveHandler(any(NodeDragMoveHandler.class));
        verify(primitive2, times(1)).addNodeDragEndHandler(any(NodeDragEndHandler.class));
        verify(primitive3, times(1)).addNodeDragStartHandler(any(NodeDragStartHandler.class));
        verify(primitive3, times(1)).addNodeDragMoveHandler(any(NodeDragMoveHandler.class));
        verify(primitive3, times(1)).addNodeDragEndHandler(any(NodeDragEndHandler.class));
    }

    @Test
    public void testCP_DragHandlers()
    {
        WiresShape realShape = createWithRealHandlers();
        tested = new WiresShapeControlHandleList(realShape, IControlHandle.ControlHandleStandardType.RESIZE, controlHandleList, handlerRegistrationManager);

        setCPLocations(0, 0, 10, 10, 0, 10, 10, 0);

        final Point2D c0 = new Point2D(0, 0);
        final Point2D s0 = new Point2D(0, 0);
        realShape.setResizable(true).addWiresResizeStartHandler(new WiresResizeStartHandler()
        {
            @Override
            public void onShapeResizeStart(WiresResizeStartEvent event)
            {
                c0.setX(event.getX());
                c0.setY(event.getY());
                s0.setX(event.getWidth());
                s0.setY(event.getHeight());
            }
        });
        final Point2D c1 = new Point2D(0, 0);
        final Point2D s1 = new Point2D(0, 0);
        realShape.addWiresResizeStepHandler(new WiresResizeStepHandler()
        {
            @Override
            public void onShapeResizeStep(WiresResizeStepEvent event)
            {
                c1.setX(event.getX());
                c1.setY(event.getY());
                s1.setX(event.getWidth());
                s1.setY(event.getHeight());
            }
        });
        final Point2D c2 = new Point2D(0, 0);
        final Point2D s2 = new Point2D(0, 0);
        realShape.addWiresResizeEndHandler(new WiresResizeEndHandler()
        {
            @Override
            public void onShapeResizeEnd(WiresResizeEndEvent event)
            {
                c2.setX(event.getX());
                c2.setY(event.getY());
                s2.setX(event.getWidth());
                s2.setY(event.getHeight());
            }
        });
        EventMockUtils.dragStart(primitive0, 10, 10);
        EventMockUtils.dragMove(primitive0, 10, 10);
        EventMockUtils.dragEnd(primitive0, 10, 10);

        assertEquals(0, c0.getX(), 0);
        assertEquals(0, c0.getY(), 0);
        assertEquals(10, s0.getX(), 0);
        assertEquals(10, s0.getY(), 0);
        assertEquals(0, c1.getX(), 0);
        assertEquals(0, c1.getY(), 0);
        assertEquals(10, s1.getX(), 0);
        assertEquals(10, s1.getY(), 0);
        assertEquals(0, c2.getX(), 0);
        assertEquals(0, c2.getY(), 0);
        assertEquals(10, s2.getX(), 0);
        assertEquals(10, s2.getY(), 0);
    }

    private void setCPLocations(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3)
    {
        setLocation(primitive0, x0, y0);
        setLocation(primitive1, x1, y1);
        setLocation(primitive2, x2, y2);
        setLocation(primitive3, x3, y3);
    }

    private void setLocation(IPrimitive<?> _primitive, double x, double y)
    {
        _primitive.setX(x);
        _primitive.setY(y);
    }

    private static WiresShape createWithRealHandlers()
    {
        Layer layer = new Layer();
        Group group = new Group();
        layer.add(group);
        return new WiresShape(new MultiPath().rect(0, 0, 10, 10));
    }
}
