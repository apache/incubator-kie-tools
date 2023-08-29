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

import java.util.Iterator;
import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeToolboxTest {

    private final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                    0d,
                                                                    100d,
                                                                    200d);
    @Mock
    private BiConsumer<Group, Runnable> showExecutor;

    @Mock
    private BiConsumer<Group, Runnable> hideExecutor;

    @Mock
    private WiresShape shape;

    @Mock
    private ToolboxImpl toolbox;

    @Mock
    private Group group;

    @Mock
    private BoundingPoints boundingPoints;

    @Mock
    private HandlerRegistration moveRegistration;

    @Mock
    private HandlerRegistration dragStartRegistration;

    @Mock
    private HandlerRegistration dragMoveRegistration;

    @Mock
    private HandlerRegistration dragEndRegistration;

    @Mock
    private HandlerRegistration resizeStartRegistration;

    @Mock
    private HandlerRegistration resizeStepRegistration;

    @Mock
    private HandlerRegistration resizeEndRegistration;

    private WiresShapeToolbox tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(toolbox.getBoundingBox()).thenReturn(() -> boundingBox);
        when(toolbox.asPrimitive()).thenReturn(group);
        when(toolbox.setGridSize(anyDouble(),
                                 anyDouble())).thenReturn(toolbox);
        when(toolbox.refresh()).thenReturn(toolbox);
        when(group.getComputedLocation()).thenReturn(new Point2D(5d,
                                                                 10d));
        when(group.getComputedBoundingPoints()).thenReturn(boundingPoints);
        when(group.getBoundingBox()).thenReturn(boundingBox);
        when(boundingPoints.getBoundingBox()).thenReturn(boundingBox);
        when(shape.getGroup()).thenReturn(group);
        when(shape.addWiresMoveHandler(any(WiresMoveHandler.class))).thenReturn(moveRegistration);
        when(shape.addWiresDragStartHandler(any(WiresDragStartHandler.class))).thenReturn(dragStartRegistration);
        when(shape.addWiresDragMoveHandler(any(WiresDragMoveHandler.class))).thenReturn(dragMoveRegistration);
        when(shape.addWiresDragEndHandler(any(WiresDragEndHandler.class))).thenReturn(dragEndRegistration);
        when(shape.addWiresResizeStartHandler(any(WiresResizeStartHandler.class))).thenReturn(resizeStartRegistration);
        when(shape.addWiresResizeStepHandler(any(WiresResizeStepHandler.class))).thenReturn(resizeStepRegistration);
        when(shape.addWiresResizeEndHandler(any(WiresResizeEndHandler.class))).thenReturn(resizeEndRegistration);
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return toolbox;
        }).when(toolbox).show(any(Runnable.class),
                              any(Runnable.class));
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return toolbox;
        }).when(toolbox).hide(any(Runnable.class),
                              any(Runnable.class));
        tested = new WiresShapeToolbox(shape, toolbox)
                .useHideExecutor(hideExecutor)
                .useShowExecutor(showExecutor);
    }

    @Test
    public void testInit() {
        assertEquals(boundingBox, tested.getBoundingBox());
        assertFalse(tested.isVisible());
        verify(shape,
               times(1)).addWiresMoveHandler(any(WiresMoveHandler.class));
        verify(shape,
               times(1)).addWiresDragStartHandler(any(WiresDragStartHandler.class));
        verify(shape,
               times(1)).addWiresDragMoveHandler(any(WiresDragMoveHandler.class));
        verify(shape,
               times(1)).addWiresDragEndHandler(any(WiresDragEndHandler.class));
        verify(shape,
               times(1)).addWiresResizeStartHandler(any(WiresResizeStartHandler.class));
        verify(shape,
               times(1)).addWiresResizeStepHandler(any(WiresResizeStepHandler.class));
        verify(shape,
               times(1)).addWiresResizeEndHandler(any(WiresResizeEndHandler.class));
        verify(toolbox,
               times(1)).hide();
        ArgumentCaptor<Point2D> pc = ArgumentCaptor.forClass(Point2D.class);
        verify(toolbox,
               times(1)).offset(pc.capture());
        Point2D point = pc.getValue();
        assertEquals(5d,
                     point.getX(),
                     0);
        assertEquals(10d,
                     point.getY(),
                     0);
    }

    @Test
    public void testAt() {
        WiresShapeToolbox cascade = tested.at(Direction.EAST);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).at(eq(Direction.EAST));
    }

    @Test
    public void testGrid() {
        Point2DGrid grid = mock(Point2DGrid.class);
        when(toolbox.getAt()).thenReturn(Direction.EAST);
        when(toolbox.getGrid()).thenReturn(grid);
        WiresShapeToolbox cascade = tested.grid(grid);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).grid(eq(grid));
    }

    @Test
    public void testOffset() {
        tested.offset(new Point2D(10d,
                                  10d));
        ArgumentCaptor<Point2D> pc = ArgumentCaptor.forClass(Point2D.class);
        verify(toolbox,
               times(2)).offset(pc.capture());
        Point2D point = pc.getValue();
        assertEquals(10d,
                     point.getX(),
                     0);
        assertEquals(10d,
                     point.getY(),
                     0);
    }

    @Test
    public void testResize() {
        tested.resize(100d,
                      200d);
        verify(toolbox,
               times(1)).setGridSize(eq(100d),
                                     eq(200d));
        verify(toolbox,
               times(1)).refresh();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIterate() {
        Iterator<DecoratedItem> iterator = mock(Iterator.class);
        when(toolbox.iterator()).thenReturn(iterator);
        assertEquals(iterator,
                     tested.iterator());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddItem() {
        DecoratedItem item = mock(DecoratedItem.class);
        tested.add(item);
        verify(toolbox,
               times(1)).add(eq(item));
    }

    @Test
    public void testShow() {
        tested.show();
        verify(toolbox,
               times(1)).show();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(toolbox,
               times(2)).hide();
    }

    @Test
    public void testGetLayer() {
        Layer layer = mock(Layer.class);
        when(group.getLayer()).thenReturn(layer);
        assertEquals(layer,
                     tested.getLayer());
    }

    @Test
    public void testGetBB() {
        assertEquals(boundingBox,
                     tested.getBoundingBox());
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(toolbox, times(1)).hide();
        verify(toolbox, times(1)).destroy();
        verify(moveRegistration, times(1)).removeHandler();
        verify(dragStartRegistration, times(1)).removeHandler();
        verify(dragMoveRegistration, times(1)).removeHandler();
        verify(dragEndRegistration, times(1)).removeHandler();
        verify(resizeStartRegistration, times(1)).removeHandler();
        verify(resizeStepRegistration, times(1)).removeHandler();
        verify(resizeEndRegistration, times(1)).removeHandler();
    }

    @Test
    public void testHideAndDestroy() {
        tested.hideAndDestroy();
        verify(toolbox, times(1)).hide();
        verify(toolbox, times(1)).hide(any(Runnable.class), any(Runnable.class));
        verify(toolbox, times(1)).destroy();
        verify(moveRegistration, times(1)).removeHandler();
        verify(dragStartRegistration, times(1)).removeHandler();
        verify(dragMoveRegistration, times(1)).removeHandler();
        verify(dragEndRegistration, times(1)).removeHandler();
        verify(resizeStartRegistration, times(1)).removeHandler();
        verify(resizeStepRegistration, times(1)).removeHandler();
        verify(resizeEndRegistration, times(1)).removeHandler();
    }
}