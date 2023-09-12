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


package com.ait.lienzo.client.core.shape.toolbox.items.impl;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerManager;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ButtonItemImplTest {

    private final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                    0d,
                                                                    100d,
                                                                    200d);

    @Mock
    private AbstractFocusableGroupItem<?> groupItem;

    @Mock
    private Shape groupItemPrim;

    @Mock
    private Group groupItemGroup;

    @Mock
    private BiConsumer<Group, Runnable> showExecutor;

    @Mock
    private BiConsumer<Group, Runnable> hideExecutor;

    @Mock
    private BoundingPoints boundingPoints;

    @Mock
    private HandlerManager handlerManager;

    @Mock
    private NodeMouseClickHandler nodeMouseClickHandler;

    @Mock
    private NodeMouseDownHandler nodeMouseDownHandler;

    @Mock
    private NodeMouseMoveHandler nodeMouseMoveHandler;

    @Mock
    private NodeMouseExitHandler nodeMouseExitHandler;

    @Mock
    private HandlerRegistration clickReg = new HandlerManager.HandlerRegistrationImpl(NodeMouseClickEvent.getType(), nodeMouseClickHandler, handlerManager);

    @Mock
    private HandlerRegistration downReg = new HandlerManager.HandlerRegistrationImpl(NodeMouseDownEvent.getType(), nodeMouseDownHandler, handlerManager);

    @Mock
    private HandlerRegistration moveReg = new HandlerManager.HandlerRegistrationImpl(NodeMouseMoveEvent.getType(), nodeMouseMoveHandler, handlerManager);

    @Mock
    private HandlerRegistration exitReg = new HandlerManager.HandlerRegistrationImpl(NodeMouseExitEvent.getType(), nodeMouseExitHandler, handlerManager);

    private ButtonItemImpl tested;
    private Layer layer;
    private NodeMouseClickHandler clickHandler;
    private NodeMouseDownHandler downHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        layer = spy(new Layer());
        when(groupItem.getPrimitive()).thenReturn(groupItemPrim);
        when(groupItemPrim.getLayer()).thenReturn(layer);
        when(groupItemPrim.asNode()).thenReturn(new Group());
        when(groupItemPrim.copyTo(any(Shape.class))).thenReturn(groupItemPrim);
        when(groupItemPrim.setLocation(any(Point2D.class))).thenReturn(groupItemPrim);
        when(groupItemPrim.setAlpha(anyDouble())).thenReturn(groupItemPrim);
        when(groupItemPrim.setListening(anyBoolean())).thenReturn(groupItemPrim);
        when(groupItemPrim.setDraggable(anyBoolean())).thenReturn(groupItemPrim);
        when(groupItemPrim.addNodeMouseClickHandler(any(NodeMouseClickHandler.class))).thenReturn(clickReg);
        when(groupItemPrim.addNodeMouseDownHandler(any(NodeMouseDownHandler.class))).thenReturn(downReg);
        when(groupItemPrim.addNodeMouseMoveHandler(any(NodeMouseMoveHandler.class))).thenReturn(moveReg);
        when(groupItemPrim.addNodeMouseExitHandler(any(NodeMouseExitHandler.class))).thenReturn(exitReg);
        when(groupItem.asPrimitive()).thenReturn(groupItemGroup);
        when(boundingPoints.getBoundingBox()).thenReturn(boundingBox);
        when(groupItem.getBoundingBox()).thenReturn(() -> boundingBox);
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return groupItem;
        }).when(groupItem).show(any(Runnable.class),
                                any(Runnable.class));
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return groupItem;
        }).when(groupItem).hide(any(Runnable.class),
                                any(Runnable.class));
        tested =
                new ButtonItemImpl(groupItem)
                        .useHideExecutor(hideExecutor)
                        .useShowExecutor(showExecutor);

        ArgumentCaptor<NodeMouseClickHandler> clickHandlerCaptor = ArgumentCaptor.forClass(NodeMouseClickHandler.class);
        verify(groupItemPrim, times(1)).addNodeMouseClickHandler(clickHandlerCaptor.capture());
        clickHandler = clickHandlerCaptor.getValue();
        ArgumentCaptor<NodeMouseDownHandler> downHandlerCaptor = ArgumentCaptor.forClass(NodeMouseDownHandler.class);
        verify(groupItemPrim, times(1)).addNodeMouseDownHandler(downHandlerCaptor.capture());
        downHandler = downHandlerCaptor.getValue();
    }

    @Test
    public void testInit() {
        assertEquals(groupItemGroup,
                     tested.asPrimitive());
        assertEquals(groupItemPrim,
                     tested.getPrimitive());
        assertEquals(groupItem,
                     tested.getWrapped());
        assertEquals(boundingBox,
                     tested.getBoundingBox().get());
        assertFalse(tested.isVisible());
    }

    @Test
    public void testShow() {
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.show(before,
                    after);
        verify(groupItem,
               times(1)).show(eq(before),
                              eq(after));
        verify(groupItem,
               never()).hide(any(Runnable.class),
                             any(Runnable.class));
        verify(before,
               times(1)).run();
        verify(after,
               times(1)).run();
    }

    @Test
    public void testHide() {
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.hide(before,
                    after);
        verify(groupItem,
               times(1)).hide(any(Runnable.class),
                              eq(after));
        verify(groupItem,
               never()).show(any(Runnable.class),
                             any(Runnable.class));
        verify(before,
               times(1)).run();
        verify(after,
               times(1)).run();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClickEvent() {
        Consumer<NodeMouseClickEvent> clickConsumer = mock(Consumer.class);
        Consumer<NodeMouseMoveEvent> moveStartConsumer = mock(Consumer.class);
        ButtonItem cascade = tested.onClick(clickConsumer);
        assertEquals(tested, cascade);
        cascade = tested.onMoveStart(moveStartConsumer);
        assertEquals(tested, cascade);
        NodeMouseClickEvent clickEvent = mock(NodeMouseClickEvent.class);
        clickHandler.onNodeMouseClick(clickEvent);
        verify(clickConsumer, times(1)).accept(eq(clickEvent));
        verify(moveStartConsumer, never()).accept(any(NodeMouseMoveEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDownAndThenMoveEvent() {
        ArgumentCaptor<NodeMouseMoveHandler> moveHandlerCaptor = ArgumentCaptor.forClass(NodeMouseMoveHandler.class);
        verify(groupItemPrim, times(1)).addNodeMouseMoveHandler(moveHandlerCaptor.capture());
        NodeMouseMoveHandler moveHandler = moveHandlerCaptor.getValue();
        Consumer<NodeMouseClickEvent> clickConsumer = mock(Consumer.class);
        Consumer<NodeMouseMoveEvent> moveStartConsumer = mock(Consumer.class);
        ButtonItem cascade = tested.onClick(clickConsumer);
        assertEquals(tested, cascade);
        cascade = tested.onMoveStart(moveStartConsumer);
        assertEquals(tested, cascade);
        NodeMouseDownEvent downEvent = mock(NodeMouseDownEvent.class);
        downHandler.onNodeMouseDown(downEvent);
        NodeMouseMoveEvent moveEvent = mock(NodeMouseMoveEvent.class);
        moveHandler.onNodeMouseMove(moveEvent);
        verify(moveStartConsumer, times(1)).accept(eq(moveEvent));
        verify(clickConsumer, never()).accept(any(NodeMouseClickEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDownButThenExitEvent() {
        ArgumentCaptor<NodeMouseExitHandler> exitHandlerCaptor = ArgumentCaptor.forClass(NodeMouseExitHandler.class);
        verify(groupItemPrim, times(1)).addNodeMouseExitHandler(exitHandlerCaptor.capture());
        NodeMouseExitHandler exitHandler = exitHandlerCaptor.getValue();
        Consumer<NodeMouseClickEvent> clickConsumer = mock(Consumer.class);
        Consumer<NodeMouseMoveEvent> moveStartConsumer = mock(Consumer.class);
        ButtonItem cascade = tested.onClick(clickConsumer);
        assertEquals(tested, cascade);
        cascade = tested.onMoveStart(moveStartConsumer);
        assertEquals(tested, cascade);
        NodeMouseDownEvent downEvent = mock(NodeMouseDownEvent.class);
        downHandler.onNodeMouseDown(downEvent);
        NodeMouseExitEvent exitEvent = mock(NodeMouseExitEvent.class);
        exitHandler.onNodeMouseExit(exitEvent);
        verify(clickConsumer, never()).accept(any(NodeMouseClickEvent.class));
        verify(moveStartConsumer, never()).accept(any(NodeMouseMoveEvent.class));
    }

    @Test
    public void testEnsureShapeSelection() {
        verify(groupItemPrim, times(1)).setFillBoundsForSelection(eq(true));
        verify(groupItemPrim, times(1)).setFillShapeForSelection(eq(true));
    }

    @Test
    public void testEnable() {
        GroupItem item = mock(GroupItem.class);
        Group itemPrimitive = mock(Group.class);
        when(groupItem.getGroupItem()).thenReturn(item);
        when(item.asPrimitive()).thenReturn(itemPrimitive);
        tested.enable();
        verify(itemPrimitive, times(1)).setListening(eq(true));
        verify(itemPrimitive, times(1)).setAlpha(eq(ButtonItemImpl.ALPHA_ENABLED));
    }

    @Test
    public void testDisable() {
        GroupItem item = mock(GroupItem.class);
        Group itemPrimitive = mock(Group.class);
        when(groupItem.getGroupItem()).thenReturn(item);
        when(item.asPrimitive()).thenReturn(itemPrimitive);
        tested.disable();
        verify(itemPrimitive, times(1)).setListening(eq(false));
        verify(itemPrimitive, times(1)).setAlpha(eq(ButtonItemImpl.ALPHA_DISABLED));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(groupItem, times(1)).destroy();
        verify(clickReg, times(1)).removeHandler();
        verify(downReg, times(1)).removeHandler();
        verify(moveReg, times(1)).removeHandler();
        verify(exitReg, times(1)).removeHandler();
    }
}
