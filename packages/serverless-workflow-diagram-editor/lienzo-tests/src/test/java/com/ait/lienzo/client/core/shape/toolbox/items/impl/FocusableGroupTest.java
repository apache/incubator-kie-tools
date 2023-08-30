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
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
public class FocusableGroupTest {

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
    private Group groupItemPrimitive;

    @Mock
    private Group group;

    @Mock
    private BoundingPoints boundingPoints;

    private FocusableGroup tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(groupItem.asPrimitive()).thenReturn(groupItemPrimitive);
        when(groupItemPrimitive.getAlpha()).thenReturn(0d);
        when(groupItemPrimitive.getComputedBoundingPoints()).thenReturn(boundingPoints);
        when(groupItemPrimitive.getBoundingBox()).thenReturn(boundingBox);
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
        tested = spy(new FocusableGroup(groupItem,
                                        group)
                             .useHideExecutor(hideExecutor)
                             .useShowExecutor(showExecutor));
    }

    @Test
    public void testInit() {
        assertNotNull(tested.getPrimitive());
        assertTrue(tested.getPrimitive() instanceof MultiPath);
        assertEquals(groupItem,
                     tested.getGroupItem());
        assertEquals(boundingBox,
                     tested.getBoundingBox().get());
        assertFalse(tested.isDecorated());
        assertFalse(tested.hasTooltip());
        assertFalse(tested.isVisible());
        verify(group,
               times(1)).setListening(eq(false));
        verify(groupItem,
               times(1)).add(eq(tested.getPrimitive()));
        verify(groupItemPrimitive,
               times(1)).add(eq(group));
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
    }

    @Test
    public void testHide() {
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.hide(before,
                    after);
        verify(tested,
               times(1)).cancelTimers();
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
    public void testDestroy() {
        tested.destroy();
        verify(tested,
               times(1)).cancelTimers();
        verify(groupItem,
               times(1)).destroy();
    }
}
