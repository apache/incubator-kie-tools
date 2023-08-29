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


package com.ait.lienzo.client.core.shape.toolbox;

import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GroupItemTest {

    @Mock
    private Group group;

    @Mock
    private IPrimitive<?> aPrimitive;

    @Mock
    private BiConsumer<Group, Runnable> showExecutor;

    @Mock
    private BiConsumer<Group, Runnable> hideExecutor;

    private GroupItem tested;

    @Before
    public void setUp() {
        this.tested =
                new GroupItem(group)
                        .useShowExecutor(showExecutor)
                        .useHideExecutor(hideExecutor);
    }

    @Test
    public void testInit() {
        verify(group,
               times(1)).setAlpha(eq(0d));
        assertFalse(tested.isVisible());
        assertEquals(group,
                     tested.asPrimitive());
    }

    @Test
    public void testAdd() {
        tested.add(aPrimitive);
        verify(group,
               times(1)).add(eq(aPrimitive));
    }

    @Test
    public void testRemove() {
        tested.remove(aPrimitive);
        verify(group,
               times(1)).remove(eq(aPrimitive));
    }

    @Test
    public void testShow() {
        when(group.getAlpha()).thenReturn(0d);
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.show(before,
                    after);
        verify(before,
               times(1)).run();
        verify(showExecutor,
               times(1)).accept(eq(group),
                                eq(after));
        verify(hideExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
    }

    @Test
    public void testNoNeedToShow() {
        when(group.getAlpha()).thenReturn(1d);
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.show(before,
                    after);
        verify(before,
               never()).run();
        verify(showExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
        verify(hideExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
    }

    @Test
    public void testHide() {
        when(group.getAlpha()).thenReturn(1d);
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.hide(before,
                    after);
        verify(before,
               times(1)).run();
        verify(hideExecutor,
               times(1)).accept(eq(group),
                                eq(after));
        verify(showExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
    }

    @Test
    public void testNoNeedToHide() {
        when(group.getAlpha()).thenReturn(0d);
        final Runnable before = mock(Runnable.class);
        final Runnable after = mock(Runnable.class);
        tested.hide(before,
                    after);
        verify(before,
               never()).run();
        verify(showExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
        verify(hideExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
    }

    @Test
    public void testDestroy() {
        group = spy(new Group());
        tested = new GroupItem(group);
        IPrimitive<?> p = spy(new Group());
        NFastArrayList<IPrimitive<?>> children = new NFastArrayList<>();
        children.add(p);
        when(group.getChildNodes()).thenReturn(children);
        tested.destroy();
        verify(p, times(1)).removeFromParent();
        verify(group, times(1)).removeFromParent();
    }
}
