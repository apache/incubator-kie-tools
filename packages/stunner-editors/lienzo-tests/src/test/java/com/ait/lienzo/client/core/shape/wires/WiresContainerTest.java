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
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.event.HandlerManager;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresContainerTest {

    private WiresShape shape;

    private Group parentContainer;

    private WiresContainer tested;

    @Mock
    private HandlerRegistrationManager handlerRegistrationManager;

    @Mock
    private HandlerManager handlerManager;

    private WiresManager wiresManager;

    @Mock
    private AlignAndDistribute alignAndDistribute;

    @Mock
    private Layer layer;

    @Mock
    private Viewport viewport;

    @Mock
    private AlignAndDistributeControl alignAndDistributeControl;

    private WiresLayoutContainer layoutContainer;

    private Group layoutContainerGroup;

    @Before
    public void setup() {
        when(layer.getViewport()).thenReturn(viewport);
        wiresManager = spy(WiresManager.get(layer));
        layoutContainerGroup = spy(new Group());
        layoutContainer = spy(new WiresLayoutContainer(layoutContainerGroup));
        when(wiresManager.getAlignAndDistribute()).thenReturn(alignAndDistribute);
        when(alignAndDistribute.getControlForShape(anyString())).thenReturn(alignAndDistributeControl);
        when(layoutContainer.getGroup()).thenReturn(layoutContainerGroup);
        parentContainer = spy(new Group());
        shape = createShape();
        tested = new WiresContainer(parentContainer, handlerManager, handlerRegistrationManager);
        tested.setWiresManager(wiresManager);
    }

    @Test
    public void testInit() {
        assertNull(tested.getParent());
        assertNull(tested.getDockedTo());
        assertEquals(parentContainer, tested.getContainer());
        assertEquals(0, tested.getChildShapes().size());
    }

    @Test
    public void testLocation() {
        WiresShape child = spy(new WiresShape(new MultiPath().circle(5)));
        Point2D point = new Point2D(0d, 0d);
        tested.add(child);
        tested.setLocation(point);
        verify(parentContainer, times(1)).setLocation(eq(point));
        verify(child, atLeastOnce()).shapeMoved();
    }

    @Test
    public void testSetDraggable() {
        tested.setDraggable(true);
        //was 5, 3 coz Attribute change handlers were removed
        verify(handlerRegistrationManager, times(3)).register(any(HandlerRegistration.class));
        verify(parentContainer, times(1)).addNodeDragStartHandler(any(NodeDragStartHandler.class));
        verify(parentContainer, times(1)).addNodeDragMoveHandler(any(NodeDragMoveHandler.class));
        verify(parentContainer, times(1)).addNodeDragEndHandler(any(NodeDragEndHandler.class));
    }

    @Test
    public void testDockedTo() {
        final WiresContainer parent = mock(WiresContainer.class);
        tested.setDockedTo(parent);
        assertEquals(parent, tested.getDockedTo());
    }

    @Test
    public void testParent() {
        final WiresContainer parent = mock(WiresContainer.class);
        tested.setParent(parent);
        assertEquals(parent, tested.getParent());
    }

    @Test
    public void testDraggagle() {
        tested.setDraggable(true);
        assertTrue(tested.getGroup().isDraggable());
    }

    @Test
    public void testListen() {
        tested.listen(true);
        assertTrue(tested.isListening());
        tested.listen(false);
        assertFalse(tested.isListening());
    }

    @Test
    public void testAddChild() {
        tested.add(createShape(layoutContainer));

        final NFastArrayList<WiresShape> children = tested.getChildShapes();
        assertEquals(1, tested.getChildShapes().size());
        assertEquals(tested, children.get(0).getParent());

        assertEquals(0, layoutContainerGroup.getX(), 0);
        assertEquals(0, layoutContainerGroup.getY(), 0);
    }

    @Test
    public void testAddTwoChilds() {
        assertEquals(0, tested.getChildShapes().size());
        tested.add(shape);
        assertEquals(1, tested.getChildShapes().size());
        final WiresShape s2 = createShape();
        tested.add(s2);
        assertEquals(2, tested.getChildShapes().size());
    }

    @Test
    public void testAddChildAlreadyPresent() {
        assertEquals(0, tested.getChildShapes().size());
        tested.add(shape);
        assertEquals(1, tested.getChildShapes().size());
        final WiresShape s2 = createShape();
        tested.add(s2);
        assertEquals(2, tested.getChildShapes().size());
        tested.add(shape);
        assertEquals(2, tested.getChildShapes().size());
    }

    @Test
    public void testRemoveChild() {
        assertEquals(0, tested.getChildShapes().size());
        tested.add(shape);
        assertEquals(1, tested.getChildShapes().size());
        tested.remove(shape);
        assertEquals(0, tested.getChildShapes().size());
    }

    @Test
    public void testAddWiresHandlers() {
        final WiresMoveHandler moveHandler = mock(WiresMoveHandler.class);
        final WiresDragStartHandler startHandler = mock(WiresDragStartHandler.class);
        final WiresDragMoveHandler dragMoveHandler = mock(WiresDragMoveHandler.class);
        final WiresDragEndHandler endHandler = mock(WiresDragEndHandler.class);
        tested.addWiresMoveHandler(moveHandler);
        tested.addWiresDragStartHandler(startHandler);
        tested.addWiresDragMoveHandler(dragMoveHandler);
        tested.addWiresDragEndHandler(endHandler);
        verify(handlerManager, times(1)).addHandler(WiresMoveEvent.TYPE, moveHandler);
        verify(handlerManager, times(1)).addHandler(WiresDragStartEvent.TYPE, startHandler);
        verify(handlerManager, times(1)).addHandler(WiresDragMoveEvent.TYPE, dragMoveHandler);
        verify(handlerManager, times(1)).addHandler(WiresDragEndEvent.TYPE, endHandler);
    }

    @Test
    public void testWiresDragStartHandler() {
        final WiresContainer handledContainer = createWithRealHandlers().setDraggable(true);
        final Group group = handledContainer.getGroup();
        final Point2D result = new Point2D(0, 0);
        handledContainer.addWiresDragStartHandler(event -> {
            result.setX(event.getX());
            result.setY(event.getY());
        });
        EventMockUtils.dragStart(group, 20, 30);

        assertEquals(20, result.getX(), 0);
        assertEquals(30, result.getY(), 0);
    }

    @Test
    public void testWiresDragMoveHandler() {
        final WiresContainer handledContainer = createWithRealHandlers().setDraggable(true);
        final Group group = handledContainer.getGroup();
        final Point2D result = new Point2D(0, 0);
        handledContainer.addWiresDragMoveHandler(event -> {
            result.setX(event.getX());
            result.setY(event.getY());
        });

        EventMockUtils.dragMove(group, 20, 30);

        assertEquals(20, result.getX(), 0);
        assertEquals(30, result.getY(), 0);
    }

    @Test
    public void testWiresDragEndHandler() {
        final WiresContainer handledContainer = createWithRealHandlers().setDraggable(true);
        final Group group = handledContainer.getGroup();
        final Point2D result = new Point2D(0, 0);
        handledContainer.addWiresDragEndHandler(event -> {
            result.setX(event.getX());
            result.setY(event.getY());
        });
        EventMockUtils.dragEnd(group, 20, 30);

        assertEquals(20, result.getX(), 0);
        assertEquals(30, result.getY(), 0);
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(handlerRegistrationManager, times(1)).removeHandler();
        verify(parentContainer, times(1)).removeFromParent();
        assertNull(tested.getParent());
    }

    private WiresContainer createWithRealHandlers() {
        final Layer layer = new Layer();
        final Group group = new Group();
        layer.add(group);
        WiresContainer wiresContainer = new WiresContainer(group);
        wiresContainer.setWiresManager(wiresManager);
        return wiresContainer;
    }

    private WiresShape createShape() {
        return new WiresShape(new MultiPath().rect(0, 0, 100, 100));
    }

    private WiresShape createShape(LayoutContainer layoutContainer) {
        return new WiresShape(new MultiPath().rect(0, 0, 100, 100), layoutContainer);
    }
}
