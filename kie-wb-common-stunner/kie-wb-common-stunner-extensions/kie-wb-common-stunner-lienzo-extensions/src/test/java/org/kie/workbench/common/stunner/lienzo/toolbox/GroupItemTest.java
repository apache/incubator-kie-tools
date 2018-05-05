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

package org.kie.workbench.common.stunner.lienzo.toolbox;

import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private BiConsumer<Group, Command> showExecutor;

    @Mock
    private BiConsumer<Group, Command> hideExecutor;

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
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.show(before,
                    after);
        verify(before,
               times(1)).execute();
        verify(showExecutor,
               times(1)).accept(eq(group),
                                eq(after));
        verify(hideExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
    }

    @Test
    public void testNoNeedToShow() {
        when(group.getAlpha()).thenReturn(1d);
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.show(before,
                    after);
        verify(before,
               never()).execute();
        verify(showExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
        verify(hideExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
    }

    @Test
    public void testHide() {
        when(group.getAlpha()).thenReturn(1d);
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.hide(before,
                    after);
        verify(before,
               times(1)).execute();
        verify(hideExecutor,
               times(1)).accept(eq(group),
                                eq(after));
        verify(showExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
    }

    @Test
    public void testNoNeedToHide() {
        when(group.getAlpha()).thenReturn(0d);
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.hide(before,
                    after);
        verify(before,
               never()).execute();
        verify(showExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
        verify(hideExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
    }

    @Test
    public void testDestroy() {
        IPrimitive<?> p = mock(IPrimitive.class);
        NFastArrayList<IPrimitive<?>> children = new NFastArrayList<>();
        children.add(p);
        when(group.getChildNodes()).thenReturn(children);
        tested.destroy();
        verify(p, times(1)).removeFromParent();
    }
}
