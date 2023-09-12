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
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.toolbox.grid.AutoGrid;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
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
public class ToolboxImplTest {

    private final BoundingBox shapeBoundingBox = BoundingBox.fromDoubles(0d,
                                                                         0d,
                                                                         33d,
                                                                         33d);

    private final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                    0d,
                                                                    100d,
                                                                    200d);
    @Mock
    private BiConsumer<Group, Runnable> showExecutor;

    @Mock
    private BiConsumer<Group, Runnable> hideExecutor;

    @Mock
    private ItemGridImpl wrapped;

    @Mock
    private IPrimitive primitive;

    @Mock
    private BoundingPoints boundingPoints;

    private ToolboxImpl tested;
    private Group group;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        group = spy(new Group());
        when(wrapped.getBoundingBox()).thenReturn(() -> boundingBox);
        when(wrapped.onRefresh(any(Runnable.class))).thenReturn(wrapped);
        when(wrapped.asPrimitive()).thenReturn(group);
        when(wrapped.getPrimitive()).thenReturn(primitive);
        when(boundingPoints.getBoundingBox()).thenReturn(boundingBox);
        doReturn(0d).when(group).getAlpha();
        doReturn(boundingPoints).when(group).getComputedBoundingPoints();
        doReturn(boundingBox).when(group).getBoundingBox();
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return wrapped;
        }).when(wrapped).show(any(Runnable.class),
                              any(Runnable.class));
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return wrapped;
        }).when(wrapped).hide(any(Runnable.class),
                              any(Runnable.class));
        tested = new ToolboxImpl(() -> shapeBoundingBox,
                                 wrapped)
                .useHideExecutor(hideExecutor)
                .useShowExecutor(showExecutor);
    }

    @Test
    public void testInit() {
        assertEquals(primitive,
                     tested.getPrimitive());
        assertEquals(group,
                     tested.asPrimitive());
        assertEquals(boundingBox,
                     tested.getBoundingBox().get());
        assertFalse(tested.isVisible());
        verify(wrapped,
               times(1)).useShowExecutor(eq(showExecutor));
        verify(wrapped,
               times(1)).useHideExecutor(eq(hideExecutor));
    }

    @Test
    public void testAt() {
        ToolboxImpl cascade = tested.at(Direction.EAST);
        assertEquals(tested,
                     cascade);
        assertEquals(Direction.EAST,
                     tested.getAt());
    }

    @Test
    public void testAtButVisible() {
        makeItVisible();
        tested.at(Direction.EAST);
        verifyGroupIsOnTop();
    }

    @Test
    public void testOffset() {
        Point2D o = new Point2D(50,
                                25);
        ToolboxImpl cascade = tested.offset(o);
        assertEquals(tested,
                     cascade);
        assertEquals(o,
                     tested.getOffset());
    }

    @Test
    public void testOffsetButVisible() {
        makeItVisible();
        tested.offset(new Point2D(50d, 25d));
        verifyGroupIsOnTop();
    }

    @Test
    public void testGrid() {
        Point2DGrid grid = mock(Point2DGrid.class);
        when(wrapped.getGrid()).thenReturn(grid);
        ToolboxImpl cascade = tested.grid(grid);
        assertEquals(tested,
                     cascade);
        verify(wrapped,
               times(1)).grid(eq(grid));
    }

    @Test
    public void testGridButVisible() {
        makeItVisible();
        Point2DGrid grid = mock(Point2DGrid.class);
        when(wrapped.getGrid()).thenReturn(grid);
        tested.grid(grid);
        verifyGroupIsOnTop();
    }

    @Test
    public void testUpdateGridSize() {
        AutoGrid grid = spy(new AutoGrid.Builder()
                                    .forBoundingBox(boundingBox)
                                    .build());
        when(wrapped.getGrid()).thenReturn(grid);
        ToolboxImpl cascade = tested.grid(grid);
        assertEquals(tested,
                     cascade);
        verify(wrapped,
               times(1)).grid(eq(grid));
        verify(grid,
               times(1)).setSize(eq(53d),
                                 eq(53d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIterate() {
        Iterator<DecoratedItem> iterator = mock(Iterator.class);
        when(wrapped.iterator()).thenReturn(iterator);
        assertEquals(iterator,
                     tested.iterator());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddItem() {
        DecoratedItem item = mock(DecoratedItem.class);
        tested.add(item);
        verify(wrapped,
               times(1)).add(eq(item));
    }

    @Test
    public void testShow() {
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.show(before,
                    after);
        verify(wrapped,
               times(1)).show(any(Runnable.class),
                              any(Runnable.class));
        verify(wrapped,
               never()).hide(any(Runnable.class),
                             any(Runnable.class));
        verify(before,
               times(1)).run();
        verify(after,
               times(1)).run();
        ArgumentCaptor<Point2D> pc = ArgumentCaptor.forClass(Point2D.class);
        verify(group,
               times(1)).setLocation(pc.capture());
        verifyGroupIsOnTop();
        Point2D point = pc.getValue();
        assertEquals(33d,
                     point.getX(),
                     0);
        assertEquals(0d,
                     point.getY(),
                     0);
    }

    @Test
    public void testHide() {
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.hide(before,
                    after);
        verify(wrapped,
               times(1)).hide(eq(before),
                              any(Runnable.class));
        verify(wrapped,
               never()).show(any(Runnable.class),
                             any(Runnable.class));
        verify(group, never()).moveToTop();
        verify(group, never()).moveToBottom();
        verify(group, never()).moveDown();
        verify(group, never()).moveUp();
        verify(before,
               times(1)).run();
        verify(after,
               times(1)).run();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(wrapped,
               times(1)).destroy();
    }

    private void makeItVisible() {
        doReturn(true).when(wrapped).isVisible();
    }

    private void verifyGroupIsOnTop() {
        verify(group, times(1)).moveToTop();
        verify(group, never()).moveToBottom();
        verify(group, never()).moveDown();
        verify(group, never()).moveUp();
    }
}
