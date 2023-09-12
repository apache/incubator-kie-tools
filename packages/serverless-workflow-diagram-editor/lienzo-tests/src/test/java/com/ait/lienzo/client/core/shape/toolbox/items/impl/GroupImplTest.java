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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.TooltipItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

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
public class GroupImplTest {

    private final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                    0d,
                                                                    100d,
                                                                    200d);

    @Mock
    private GroupItem groupItem;

    @Mock
    private BiConsumer<Group, Runnable> showExecutor;

    @Mock
    private BiConsumer<Group, Runnable> hideExecutor;

    @Mock
    private Group group;

    @Mock
    private DecoratorItem decorator;

    private NFastArrayList groupChildren;

    @Mock
    private TooltipItem tooltip;

    @Mock
    private BoundingPoints boundingPoints;

    private GroupImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(group.getAlpha()).thenReturn(0d);
        when(group.getComputedBoundingPoints()).thenReturn(boundingPoints);
        when(group.getBoundingBox()).thenReturn(boundingBox);
        groupChildren = new NFastArrayList<>();
        groupChildren.add(new Object());
        when(group.getChildNodes()).thenReturn(groupChildren);
        when(boundingPoints.getBoundingBox()).thenReturn(boundingBox);
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
        tested = new GroupImpl(groupItem,
                               group)
                .useHideExecutor(hideExecutor)
                .useShowExecutor(showExecutor)
                .decorate(decorator)
                .tooltip(tooltip);
    }

    @Test
    public void testInit() {
        assertEquals(group,
                     tested.getPrimitive());
        assertEquals(groupItem,
                     tested.getGroupItem());
        assertEquals(boundingBox,
                     tested.getBoundingBox().get());
        assertTrue(tested.isDecorated());
        assertTrue(tested.hasTooltip());
        assertFalse(tested.isVisible());
    }

    @Test
    public void testShow() {
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.show(before,
                    after);
        verify(groupItem,
               times(1)).show(any(Runnable.class),
                              eq(after));
        verify(groupItem,
               never()).hide(any(Runnable.class),
                             any(Runnable.class));
        verify(before,
               times(1)).run();
        verify(after,
               times(1)).run();
        verify(decorator,
               times(1)).show();
        verify(tooltip,
               times(1)).show();
    }

    @Test
    public void testHide() {
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.hide(before,
                    after);
        verify(groupItem,
               times(1)).hide(eq(before),
                              any(Runnable.class));
        verify(groupItem,
               never()).show(any(Runnable.class),
                             any(Runnable.class));
        verify(before,
               times(1)).run();
        verify(after,
               times(1)).run();
        verify(decorator,
               times(3)).hide();
        verify(tooltip,
               times(2)).hide();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        assertFalse(tested.isDecorated());
        assertFalse(tested.hasTooltip());
        verify(decorator,
               times(1)).destroy();
        verify(tooltip,
               times(1)).destroy();
        verify(groupItem,
               times(1)).destroy();
        verify(group,
               times(1)).removeFromParent();
    }

    @Test
    public void testAdd() {
        IPrimitive<?> p = mock(IPrimitive.class);
        tested.add(p);
        verify(groupItem, times(1)).add(eq(p));
    }
}
