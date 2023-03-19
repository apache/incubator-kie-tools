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
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeControlHandleListTest {

    private WiresShapeControlHandleList tested;

    @Mock
    private WiresShape shape;

    @Mock
    private WiresShapeHandler handler;

    @Mock
    private WiresShapeControl control;

    @Mock
    private WiresMagnetsControl magnetsControl;

    @Mock
    private HandlerRegistrationManager handlerRegistrationManager;

    @Mock
    private ControlHandleList controlHandleList;

    @Mock
    private IControlHandle handle0;

    private IPrimitive<?> primitive0;

    @Mock
    private IControlHandle handle1;

    private IPrimitive<?> primitive1;

    @Mock
    private IControlHandle handle2;

    private IPrimitive<?> primitive2;

    @Mock
    private IControlHandle handle3;

    private IPrimitive<?> primitive3;

    private final NFastArrayList<WiresShape> children = new NFastArrayList<>();

    @Before
    public void setup() {
        final Layer layer = new Layer();
        final Group group = spy(new Group());
        layer.add(group);
        doReturn(magnetsControl).when(control).getMagnetsControl();
        doReturn(control).when(handler).getControl();
        doReturn(control).when(shape).getControl();
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
    public void testInitHandlers() {
        verify(shape).addWiresMoveHandler(any(WiresMoveHandler.class));
        verify(shape).addWiresDragStartHandler(any(WiresDragStartHandler.class));
        verify(shape).addWiresDragMoveHandler(any(WiresDragMoveHandler.class));
        verify(shape).addWiresDragEndHandler(any(WiresDragEndHandler.class));

        final IPrimitive<?>[] primitives = new IPrimitive<?>[]{primitive0, primitive1, primitive2, primitive3};
        for (final IPrimitive<?> primitive : primitives) {
            verifyNodeHandlers(primitive);
        }
    }

    private void verifyNodeHandlers(final IPrimitive<?> primitive) {
        verify(primitive).addNodeDragStartHandler(any(NodeDragStartHandler.class));
        verify(primitive).addNodeDragMoveHandler(any(NodeDragMoveHandler.class));
        verify(primitive).addNodeDragEndHandler(any(NodeDragEndHandler.class));
    }

    @Test
    public void testCP_DragHandlers() {

        Viewport viewport = mock(Viewport.class);
        HTMLDivElement element = mock(HTMLDivElement.class);

        Group group = spy(new Group());
        Layer layer = spy(Layer.class);

        doReturn(layer).when(group).getLayer();
        doReturn(viewport).when(layer).getViewport();
        when(viewport.getElement()).thenReturn(element);

        final WiresShape realShape = spy(new WiresShape(new MultiPath().rect(0, 0, 10, 10)));

        doReturn(group).when(realShape).getGroup();

        tested = new WiresShapeControlHandleList(realShape,
                                                 IControlHandle.ControlHandleStandardType.RESIZE,
                                                 controlHandleList,
                                                 handlerRegistrationManager);

        WiresManager.addWiresShapeHandler(realShape,
                                          handlerRegistrationManager,
                                          handler);

        setCPLocations(1, 2, 11, 12, 3, 13, 14, 4);

        final Point2D c0 = new Point2D(0, 0);
        final Point2D s0 = new Point2D(0, 0);

        realShape.setResizable(true).addWiresResizeStartHandler(event -> {
            c0.setX(event.getX());
            c0.setY(event.getY());
            s0.setX(event.getWidth());
            s0.setY(event.getHeight());
        });
        final Point2D c1 = new Point2D(0, 0);
        final Point2D s1 = new Point2D(0, 0);

        realShape.addWiresResizeStepHandler(event -> {
            c1.setX(event.getX());
            c1.setY(event.getY());
            s1.setX(event.getWidth());
            s1.setY(event.getHeight());
        });
        final Point2D c2 = new Point2D(0, 0);
        final Point2D s2 = new Point2D(0, 0);

        realShape.addWiresResizeEndHandler(event -> {
            c2.setX(event.getX());
            c2.setY(event.getY());
            s2.setX(event.getWidth());
            s2.setY(event.getHeight());
        });

        // Event handlers checks Control Point position instead
        EventMockUtils.dragStart(primitive0, 9991, 9992);
        EventMockUtils.dragMove(primitive0, 9993, 9994);
        EventMockUtils.dragEnd(primitive0, 9995, 9996);

        EventMockUtils.dragEnd(primitive0, 9995, 9996);

        assertEquals(1.0, c0.getX(), 0);
        assertEquals(2.0, c0.getY(), 0);
        assertEquals(13.0, s0.getX(), 0);
        assertEquals(11.0, s0.getY(), 0);
        assertEquals(1.0, c1.getX(), 0);
        assertEquals(2.0, c1.getY(), 0);
        assertEquals(13.0, s1.getX(), 0);
        assertEquals(11.0, s1.getY(), 0);
        assertEquals(1.0, c2.getX(), 0);
        assertEquals(2.0, c2.getY(), 0);
        assertEquals(13.0, s2.getX(), 0);
        assertEquals(11.0, s2.getY(), 0);
    }

    @Test
    public void testShowWithoutChildren() {
        tested.show();
        verify(controlHandleList).showOn(any(Group.class));
        verify(controlHandleList, never()).hide();
    }

    @Test
    public void testShowWithChild() {
        final NFastArrayList<WiresShape> listOfChildren = new NFastArrayList<>();

        final WiresShape child = mock(WiresShape.class);
        final WiresShapeControlHandleList controls = mock(WiresShapeControlHandleList.class);
        when(child.getControls()).thenReturn(controls);
        when(shape.getChildShapes()).thenReturn(listOfChildren);

        listOfChildren.add(child);
        tested = new WiresShapeControlHandleList(shape, IControlHandle.ControlHandleStandardType.RESIZE, controlHandleList, handlerRegistrationManager);
        tested.show();
        verify(controlHandleList).showOn(any(Group.class));
        verify(controlHandleList, never()).hide();
        verify(controls, never()).show();
        verify(controls, never()).hide();
    }

    @Test
    public void testShowWithChildren() {
        final NFastArrayList<WiresShape> listOfChildren = new NFastArrayList<>();
        when(shape.getChildShapes()).thenReturn(listOfChildren);

        final WiresShapeControlHandleList controls = mock(WiresShapeControlHandleList.class);

        final WiresShape child = mock(WiresShape.class);
        when(child.getControls()).thenReturn(controls);

        final WiresShape child2 = mock(WiresShape.class);
        when(child2.getControls()).thenReturn(controls);

        final WiresShape child3 = mock(WiresShape.class);
        when(child3.getControls()).thenReturn(null);

        listOfChildren.add(child);
        listOfChildren.add(child2);
        listOfChildren.add(child3);

        tested = new WiresShapeControlHandleList(shape, IControlHandle.ControlHandleStandardType.RESIZE, controlHandleList, handlerRegistrationManager);
        tested.show();

        verify(controlHandleList).showOn(any(Group.class));
        verify(controlHandleList, never()).hide();
        verify(controls, never()).show();
        verify(controls, never()).hide();
    }

    @Test
    public void testHideWithoutChildren() {
        tested.hide();
        verify(controlHandleList).hide();
        verify(controlHandleList, never()).showOn(any(Group.class));
    }

    @Test
    public void testHideWithChild() {
        final NFastArrayList<WiresShape> listOfChildren = new NFastArrayList<>();

        final WiresShape child = mock(WiresShape.class);
        final WiresShapeControlHandleList controls = mock(WiresShapeControlHandleList.class);
        when(child.getControls()).thenReturn(controls);
        when(shape.getChildShapes()).thenReturn(listOfChildren);

        listOfChildren.add(child);
        tested = new WiresShapeControlHandleList(shape, IControlHandle.ControlHandleStandardType.RESIZE, controlHandleList, handlerRegistrationManager);
        tested.hide();
        verify(controlHandleList).hide();
        verify(controlHandleList, never()).showOn(any(Group.class));
        verify(controls).hide();
        verify(controls, never()).show();
    }

    @Test
    public void testHideWithChildren() {
        final NFastArrayList<WiresShape> listOfChildren = new NFastArrayList<>();
        when(shape.getChildShapes()).thenReturn(listOfChildren);

        final WiresShapeControlHandleList controls = mock(WiresShapeControlHandleList.class);

        final WiresShape child = mock(WiresShape.class);
        when(child.getControls()).thenReturn(controls);

        final WiresShape child2 = mock(WiresShape.class);
        when(child2.getControls()).thenReturn(controls);

        final WiresShape child3 = mock(WiresShape.class);
        when(child3.getControls()).thenReturn(null);

        listOfChildren.add(child);
        listOfChildren.add(child2);
        listOfChildren.add(child3);

        tested = new WiresShapeControlHandleList(shape, IControlHandle.ControlHandleStandardType.RESIZE, controlHandleList, handlerRegistrationManager);
        tested.hide();

        verify(controlHandleList).hide();
        verify(controlHandleList, never()).showOn(any(Group.class));
        verify(controls, times(2)).hide();
        verify(controls, never()).show();
    }

    @Test
    public void testRefresh() {
        final MultiPath path = mock(MultiPath.class);
        final LayoutContainer container = mock(LayoutContainer.class);
        final ILayoutHandler layoutHandler = mock(ILayoutHandler.class);

        when(shape.getPath()).thenReturn(path);
        when(shape.getLayoutContainer()).thenReturn(container);
        when(shape.getLayoutHandler()).thenReturn(layoutHandler);

        final BoundingBox box = mock(BoundingBox.class);
        when(path.getBoundingBox()).thenReturn(box);
        when(box.getWidth()).thenReturn(10.0);
        when(box.getHeight()).thenReturn(12.0);

        tested = spy(tested);
        tested.refresh();
        verify(magnetsControl).shapeChanged();
        verify(tested).resize(10.0, 12.0, true);
    }

    private void setCPLocations(final double x0, final double y0, final double x1, final double y1, final double x2, final double y2, final double x3, final double y3) {
        setLocation(primitive0, x0, y0);
        setLocation(primitive1, x1, y1);
        setLocation(primitive2, x2, y2);
        setLocation(primitive3, x3, y3);
    }

    private void setLocation(final IPrimitive<?> primitive, final double x, final double y) {
        primitive.setX(x);
        primitive.setY(y);
    }
}