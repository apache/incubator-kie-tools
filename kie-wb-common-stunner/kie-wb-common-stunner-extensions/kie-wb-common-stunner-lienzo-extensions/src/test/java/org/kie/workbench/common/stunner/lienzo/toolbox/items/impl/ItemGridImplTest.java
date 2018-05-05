/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.lienzo.toolbox.items.impl;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.lienzo.toolbox.GroupItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.grid.FixedLayoutGrid;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.AbstractDecoratedItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ItemGridImplTest {

    private final BoundingBox boundingBox = new BoundingBox(0d,
                                                            0d,
                                                            100d,
                                                            200d);

    @Mock
    private GroupImpl groupItem;

    @Mock
    private GroupItem groupItemWrap;

    @Mock
    private Group group;

    @Mock
    private BiConsumer<Group, Command> showExecutor;

    @Mock
    private BiConsumer<Group, Command> hideExecutor;

    @Mock
    private NFastArrayList groupChildren;

    @Mock
    private BoundingPoints boundingPoints;

    @Mock
    private AbstractDecoratedItem button1;

    @Mock
    private IPrimitive button1Prim;

    @Mock
    private BoundingBox button1BB;

    @Mock
    private AbstractDecoratedItem button2;

    @Mock
    private IPrimitive button2Prim;

    @Mock
    private BoundingBox button2BB;

    @Mock
    private Command refreshCallback;

    private ItemGridImpl tested;
    private FixedLayoutGrid grid = new FixedLayoutGrid(10,
                                                       5,
                                                       Direction.NORTH,
                                                       2,
                                                       2);

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(groupItem.getPrimitive()).thenReturn((IPrimitive) group);
        when(groupItem.asPrimitive()).thenReturn(group);
        when(groupItem.setBoundingBox(any(Supplier.class))).thenReturn(groupItem);
        when(groupItem.getGroupItem()).thenReturn(groupItemWrap);
        when(button1Prim.getLocation()).thenReturn(new Point2D(1,
                                                               1));
        when(button2Prim.getLocation()).thenReturn(new Point2D(3,
                                                               3));
        when(button1BB.getWidth()).thenReturn(10d);
        when(button1BB.getHeight()).thenReturn(10d);
        when(button2BB.getWidth()).thenReturn(2.2d);
        when(button2BB.getHeight()).thenReturn(2.2d);
        when(button1.getBoundingBox()).thenReturn(() -> button1BB);
        when(button1.asPrimitive()).thenReturn(button1Prim);
        when(button2.asPrimitive()).thenReturn(button2Prim);
        when(button2.getBoundingBox()).thenReturn(() -> button2BB);
        when(group.getAlpha()).thenReturn(0d);
        when(group.getComputedBoundingPoints()).thenReturn(boundingPoints);
        when(group.getBoundingBox()).thenReturn(boundingBox);
        when(group.getChildNodes()).thenReturn(groupChildren);
        when(groupChildren.size()).thenReturn(1);
        when(boundingPoints.getBoundingBox()).thenReturn(boundingBox);
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            when(group.getAlpha()).thenReturn(1d);
            when(groupItem.isVisible()).thenReturn(true);
            return groupItem;
        }).when(groupItem).show(any(Command.class),
                                any(Command.class));
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            when(group.getAlpha()).thenReturn(0d);
            when(groupItem.isVisible()).thenReturn(false);
            return groupItem;
        }).when(groupItem).hide(any(Command.class),
                                any(Command.class));

        tested = new ItemGridImpl(groupItem)
                .useHideExecutor(hideExecutor)
                .useShowExecutor(showExecutor)
                .grid(grid)
                .add(button1)
                .add(button2)
                .onRefresh(refreshCallback);
    }

    @Test
    public void testInit() {
        assertEquals(group,
                     tested.asPrimitive());
        assertEquals(group,
                     tested.getPrimitive());
        assertEquals(groupItem,
                     tested.getWrapped());
        assertFalse(tested.isVisible());
        assertEquals(2,
                     tested.size());
        assertTrue(tested.iterator().hasNext());
        verify(button1,
               times(1)).hide();
        verify(button1,
               never()).show();
        verify(button2,
               times(1)).hide();
        verify(button2,
               never()).show();
        verify(groupItemWrap,
               times(1)).add(button1Prim);
        verify(groupItemWrap,
               times(1)).add(button2Prim);
        final BoundingBox boundingBox = tested.getBoundingBox().get();
        assertEquals(0,
                     boundingBox.getX(),
                     0);
        assertEquals(0,
                     boundingBox.getY(),
                     0);
        assertEquals(11,
                     boundingBox.getWidth(),
                     0);
        assertEquals(11,
                     boundingBox.getHeight(),
                     0);
    }

    @Test
    public void testShow() {
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.show(before,
                    after);
        verify(groupItem,
               times(1)).show(any(Command.class),
                              eq(after));
        verify(groupItem,
               never()).hide(any(Command.class),
                             any(Command.class));
        verify(before,
               times(1)).execute();
        verify(after,
               times(1)).execute();
        // Button1
        ArgumentCaptor<Point2D> p1Captor = ArgumentCaptor.forClass(Point2D.class);
        verify(button1Prim,
               times(3)).setLocation(p1Captor.capture());
        final Point2D p1 = p1Captor.getValue();
        assertEquals(0,
                     p1.getX(),
                     0);
        assertEquals(-15,
                     p1.getY(),
                     0);
        verify(button1,
               times(1)).show();
        // Button2.
        ArgumentCaptor<Point2D> p2Captor = ArgumentCaptor.forClass(Point2D.class);
        verify(button2Prim,
               times(2)).setLocation(p2Captor.capture());
        final Point2D p2 = p2Captor.getValue();
        assertEquals(15,
                     p2.getX(),
                     0);
        assertEquals(-15,
                     p2.getY(),
                     0);
        verify(button2,
               times(1)).show();
        verify(refreshCallback,
               times(1)).execute();
    }

    @Test
    public void testHide() {
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.hide(before,
                    after);
        verify(groupItem,
               times(1)).hide(eq(before),
                              any(Command.class));
        verify(groupItem,
               never()).show(any(Command.class),
                             any(Command.class));
        verify(before,
               times(1)).execute();
        verify(after,
               times(1)).execute();
        verify(button1,
               times(2)).hide();
        verify(button1,
               never()).show();
        verify(button1,
               never()).destroy();
        verify(button2,
               times(2)).hide();
        verify(button2,
               never()).show();
        verify(button2,
               never()).destroy();
        verify(refreshCallback,
               times(1)).execute();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        assertEquals(0,
                     tested.size());
        assertFalse(tested.iterator().hasNext());
        verify(button1,
               times(1)).destroy();
        verify(button2,
               times(1)).destroy();
        verify(groupItem,
               times(1)).destroy();
    }
}
