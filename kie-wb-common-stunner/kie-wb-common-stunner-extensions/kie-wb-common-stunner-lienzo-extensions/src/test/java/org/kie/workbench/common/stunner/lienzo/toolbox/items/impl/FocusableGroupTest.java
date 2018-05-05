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

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.lienzo.toolbox.GroupItem;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

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

    private final BoundingBox boundingBox = new BoundingBox(0d,
                                                            0d,
                                                            100d,
                                                            200d);

    @Mock
    private GroupItem groupItem;

    @Mock
    private BiConsumer<Group, Command> showExecutor;

    @Mock
    private BiConsumer<Group, Command> hideExecutor;

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
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return groupItem;
        }).when(groupItem).show(any(Command.class),
                                any(Command.class));
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return groupItem;
        }).when(groupItem).hide(any(Command.class),
                                any(Command.class));
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
    }

    @Test
    public void testHide() {
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.hide(before,
                    after);
        verify(tested,
               times(1)).cancelTimers();
        verify(groupItem,
               times(1)).hide(any(Command.class),
                              eq(after));
        verify(groupItem,
               never()).show(any(Command.class),
                             any(Command.class));
        verify(before,
               times(1)).execute();
        verify(after,
               times(1)).execute();
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
