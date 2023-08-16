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
import java.util.function.Consumer;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.decorator.BoxDecorator;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GroupButtonGridItemImplTest {

    private final BoundingBox buttonBoundingBox = BoundingBox.fromDoubles(0d,
                                                                          0d,
                                                                          100d,
                                                                          200d);

    private final BoundingBox toolboxBoundingBox = BoundingBox.fromDoubles(0d,
                                                                           0d,
                                                                           300d,
                                                                           123d);

    @Mock
    private BiConsumer<Group, Runnable> showExecutor;

    @Mock
    private BiConsumer<Group, Runnable> hideExecutor;

    @Mock
    private ButtonItemImpl button;

    @Mock
    private ToolboxImpl toolbox;

    @Mock
    private AbstractFocusableGroupItem buttonWrap;

    @Mock
    private ItemGridImpl toolboxWrap;

    @Mock
    private BoundingPoints buttonBoundingPoints;

    @Mock
    private BoundingPoints toolboxBoundingPoints;
    @Mock
    private AbstractDecoratedItem button1;

    @Mock
    private IPrimitive buttonPrim;
    @Mock
    private IPrimitive button1Prim;

    @Mock
    private HandlerRegistration moseEnterHandler;
    @Mock
    private HandlerRegistration moseExitHandler;

    private GroupButtonGridItemImpl tested;
    private Group buttonGroup;
    private Group toolboxGroup;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        buttonGroup = spy(new Group().setAlpha(0d));
        toolboxGroup = spy(new Group());
        when(buttonGroup.getComputedBoundingPoints()).thenReturn(buttonBoundingPoints);
        when(buttonGroup.getBoundingBox()).thenReturn(buttonBoundingBox);
        when(buttonBoundingPoints.getBoundingBox()).thenReturn(buttonBoundingBox);
        when(toolboxGroup.getComputedBoundingPoints()).thenReturn(toolboxBoundingPoints);
        when(toolboxGroup.getBoundingBox()).thenReturn(toolboxBoundingBox);
        when(toolboxBoundingPoints.getBoundingBox()).thenReturn(toolboxBoundingBox);
        when(button.getBoundingBox()).thenReturn(() -> buttonBoundingBox);
        when(button.asPrimitive()).thenReturn(buttonGroup);
        when(button.getPrimitive()).thenReturn(buttonPrim);
        when(button.getWrapped()).thenReturn(buttonWrap);
        when(buttonWrap.asPrimitive()).thenReturn(buttonGroup);
        when(buttonWrap.getBoundingBox()).thenReturn(() -> buttonBoundingBox);
        when(toolbox.getBoundingBox()).thenReturn(() -> toolboxBoundingBox);
        when(toolbox.asPrimitive()).thenReturn(toolboxGroup);
        when(toolbox.getPrimitive()).thenReturn(mock(IPrimitive.class));
        when(toolbox.getAt()).thenReturn(Direction.SOUTH_EAST);
        when(toolbox.getWrapped()).thenReturn(toolboxWrap);
        when(toolboxWrap.asPrimitive()).thenReturn(toolboxGroup);
        when(toolboxWrap.getBoundingBox()).thenReturn(() -> toolboxBoundingBox);
        when(button1.getPrimitive()).thenReturn(button1Prim);
        when(buttonPrim.addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class))).thenReturn(moseEnterHandler);
        when(buttonPrim.addNodeMouseExitHandler(any(NodeMouseExitHandler.class))).thenReturn(moseExitHandler);
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return button;
        }).when(button).show(any(Runnable.class),
                             any(Runnable.class));
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[0]).run();
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return button;
        }).when(button).hide(any(Runnable.class),
                             any(Runnable.class));
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
        tested = new GroupButtonGridItemImpl(button,
                                             toolbox)
                .useHideExecutor(hideExecutor)
                .useShowExecutor(showExecutor);
    }

    @Test
    public void testInit() {
        assertEquals(buttonGroup,
                     tested.asPrimitive());
        assertEquals(buttonWrap,
                     tested.getWrapped());
        assertEquals(buttonBoundingBox,
                     tested.getBoundingBox().get());
        assertFalse(tested.isVisible());
        verify(toolboxWrap,
               times(1)).useShowExecutor(eq(showExecutor));
        verify(toolboxWrap,
               times(1)).useHideExecutor(eq(hideExecutor));
    }

    @Test
    public void testAtForDropDown() {
        when(toolbox.getAt()).thenReturn(Direction.NORTH);
        GroupButtonGridItemImpl cascade = tested.at(Direction.NORTH);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).at(eq(Direction.NORTH));
    }

    @Test
    public void testAtForDropRight() {
        when(toolbox.getAt()).thenReturn(Direction.EAST);
        GroupButtonGridItemImpl cascade = tested.at(Direction.EAST);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).at(eq(Direction.EAST));
    }

    @Test
    public void testOffset() {
        Point2D o = new Point2D(50,
                                25);
        GroupButtonGridItemImpl cascade = tested.offset(o);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).offset(eq(o));
    }

    @Test
    public void testGrid() {
        Point2DGrid grid = mock(Point2DGrid.class);
        GroupButtonGridItemImpl cascade = tested.grid(grid);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).grid(eq(grid));
    }

    @Test
    public void testDecorateGrid() {
        BoxDecorator decorator = spy(ToolboxFactory.INSTANCE.decorators().box());
        MultiPath multiPath = spy(decorator.asPrimitive());
        when(decorator.asPrimitive()).thenReturn(multiPath);
        GroupButtonGridItemImpl cascade = tested.decorateGrid(decorator);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).decorate(eq(decorator));
        verify(decorator.asPrimitive(),
               times(1)).addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class));
        verify(decorator.asPrimitive(),
               times(1)).addNodeMouseExitHandler(any(NodeMouseExitHandler.class));
    }

    @Test
    public void testShow() {
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.show(before,
                    after);
        verify(button,
               times(1)).show(eq(before),
                              eq(after));
        verify(button,
               never()).hide(any(Runnable.class),
                             any(Runnable.class));
        verify(toolbox,
               never()).hide(any(Runnable.class),
                             any(Runnable.class));
        verify(toolbox,
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
        verify(toolbox,
               times(1)).hide(any(Runnable.class),
                              any(Runnable.class));
        verify(toolbox,
               never()).show(any(Runnable.class),
                             any(Runnable.class));
        verify(button,
               times(1)).hide();
        verify(button,
               never()).show(any(Runnable.class),
                             any(Runnable.class));
        verify(before,
               times(1)).run();
        verify(after,
               times(1)).run();
    }

    @Test
    public void testShowGrid() {
        tested.showGrid();
        verify(button,
               never()).show(any(Runnable.class),
                             any(Runnable.class));
        verify(button,
               never()).hide(any(Runnable.class),
                             any(Runnable.class));
        verify(toolbox,
               times(1)).show();
        verify(toolbox,
               never()).hide(any(Runnable.class),
                             any(Runnable.class));
    }

    @Test
    public void testHideGrid() {
        tested.hideGrid();
        verify(button,
               never()).show(any(Runnable.class),
                             any(Runnable.class));
        verify(button,
               never()).hide(any(Runnable.class),
                             any(Runnable.class));
        verify(toolbox,
               never()).show(any(Runnable.class),
                             any(Runnable.class));
        verify(toolbox,
               times(1)).hide(any(Runnable.class),
                              any(Runnable.class));
    }

    @Test
    public void testAddItem() {
        tested.add(button1);
        verify(toolbox,
               times(1)).add(eq(button1));
        verify(button1Prim,
               times(1)).addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class));
        verify(button1Prim,
               times(1)).addNodeMouseExitHandler(any(NodeMouseExitHandler.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIterate() {
        Iterator<DecoratedItem> iterator = mock(Iterator.class);
        when(toolbox.iterator()).thenReturn(iterator);
        assertEquals(iterator,
                     tested.iterator());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testClick() {
        Consumer<NodeMouseClickEvent> event = mock(Consumer.class);
        tested.onClick(event);
        verify(button,
               times(1)).onClick(eq(event));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMoveStart() {
        Consumer<NodeMouseMoveEvent> event = mock(Consumer.class);
        tested.onMoveStart(event);
        verify(button,
               times(1)).onMoveStart(eq(event));
    }

    @Test
    public void testFocus() {
        tested.focus();
        verify(buttonWrap,
               times(1)).focus();
        verify(toolbox,
               times(1)).show();
    }

    @Test
    public void testImmediateUnFocus() {
        tested.immediateUnFocus();
        verify(buttonWrap,
               times(1)).setUnFocusDelay(eq(0));
        verify(buttonWrap,
               times(1)).unFocus();
        verify(buttonWrap,
               times(2)).setUnFocusDelay(eq(GroupButtonGridItemImpl.TIMER_DELAY_MILLIS));
    }

    @Test
    public void testDestroy() {
        tested.add(button);
        tested.destroy();

        verify(moseEnterHandler,
               times(1)).removeHandler();
        verify(moseExitHandler,
               times(1)).removeHandler();
        verify(button,
               times(1)).destroy();
        verify(toolbox,
               times(1)).destroy();
    }
}
