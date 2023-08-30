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

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.toolbox.GroupItem;
import com.ait.lienzo.client.core.shape.toolbox.items.AbstractDecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.TooltipItem;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ItemImplTest {

    private final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                    0d,
                                                                    100d,
                                                                    200d);

    @Mock
    private GroupItem groupItem;

    @Mock
    private AbstractFocusableGroupItem.FocusGroupExecutor focusGroupExecutor;

    @Mock
    private BiConsumer<Group, Runnable> hideExecutor;

    @Mock
    private Shape shape;

    @Mock
    private BoundingPoints boundingPoints;

    @Mock
    private HandlerRegistration mouseEnterHandlerRegistration;

    @Mock
    private HandlerRegistration mouseExitHandlerRegistration;

    private ItemImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(shape.setListening(anyBoolean())).thenReturn(shape);

        doCallRealMethod().when(shape).addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class));
        doCallRealMethod().when(shape).addNodeMouseExitHandler(any(NodeMouseExitHandler.class));

        when(shape.addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class))).thenReturn(mouseEnterHandlerRegistration);
        when(shape.addNodeMouseExitHandler(any(NodeMouseExitHandler.class))).thenReturn(mouseExitHandlerRegistration);

        when(shape.getComputedBoundingPoints()).thenReturn(boundingPoints);
        when(shape.getBoundingBox()).thenReturn(boundingBox);
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
        tested = new ItemImpl(groupItem,
                              shape)
                .setFocusDelay(0)
                .setUnFocusDelay(0)
                .useHideExecutor(hideExecutor)
                .useFocusGroupExecutor(focusGroupExecutor);
    }

    @Test
    public void testInit() {
        assertEquals(shape,
                     tested.getPrimitive());
        assertEquals(groupItem,
                     tested.getGroupItem());
        assertEquals(boundingBox,
                     tested.getBoundingBox().get());
        assertFalse(tested.isDecorated());
        assertFalse(tested.isVisible());
        assertFalse(tested.hasTooltip());
        verify(groupItem,
               times(1)).add(eq(shape));
        verify(shape,
               times(2)).setListening(eq(true));
        verify(shape,
               times(1)).addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class));
        verify(shape,
               times(1)).addNodeMouseExitHandler(any(NodeMouseExitHandler.class));
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
    public void Runnable() {
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
        verify(focusGroupExecutor,
               times(1)).unFocus();
        verify(focusGroupExecutor,
               never()).focus();
        verify(focusGroupExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
    }

    @Test
    public void testFocus() {
        tested.focus();
        verify(focusGroupExecutor,
               times(1)).focus();
        verify(focusGroupExecutor,
               never()).unFocus();
        verify(focusGroupExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
    }

    @Test
    public void testUnFocus() {
        tested.unFocus();
        verify(focusGroupExecutor,
               times(1)).unFocus();
        verify(focusGroupExecutor,
               never()).focus();
        verify(focusGroupExecutor,
               never()).accept(any(Group.class),
                               any(Runnable.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(groupItem,
               times(1)).destroy();
        verify(shape,
               times(1)).removeFromParent();
        verify(mouseEnterHandlerRegistration,
               times(1)).removeHandler();
        verify(mouseExitHandlerRegistration,
               times(1)).removeHandler();
    }

    @Test
    public void testFocusExecutorDoingFocus() {
        final AbstractDecoratorItem decorator = mock(AbstractDecoratorItem.class);
        final Group decPrimitive = mock(Group.class);
        final Group groupItemPrimitive = mock(Group.class);
        final TooltipItem<?> tooltip = mock(TooltipItem.class);
        when(groupItem.asPrimitive()).thenReturn(groupItemPrimitive);
        when(decorator.asPrimitive()).thenReturn(decPrimitive);
        tested =
                spy(new ItemImpl(groupItem,
                                 shape)
                            .setFocusDelay(0)
                            .setUnFocusDelay(0)
                            .decorate(decorator)
                            .tooltip(tooltip));

        final AbstractFocusableGroupItem<ItemImpl>.FocusGroupExecutor focusExecutor =
                spy(tested.getFocusGroupExecutor());
        when(tested.getFocusGroupExecutor()).thenReturn(focusExecutor);
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return null;
        }).when(focusExecutor).accept(any(Group.class),
                                      any(Runnable.class));

        focusExecutor.focus();
        verify(focusExecutor,
               times(1)).setAlpha(AbstractFocusableGroupItem.ALPHA_FOCUSED);
        verify(decorator,
               times(1)).show();
        verify(decPrimitive,
               times(1)).moveToBottom();
        verify(tooltip,
               times(1)).show();
    }

    @Test
    public void testFocusExecutorDoingUnFocus() {
        final AbstractDecoratorItem decorator = mock(AbstractDecoratorItem.class);
        final Group decPrimitive = mock(Group.class);
        final Group groupItemPrimitive = mock(Group.class);
        when(groupItem.asPrimitive()).thenReturn(groupItemPrimitive);
        when(decorator.asPrimitive()).thenReturn(decPrimitive);
        final TooltipItem<?> tooltip = mock(TooltipItem.class);
        tested =
                new ItemImpl(groupItem,
                             shape)
                        .setFocusDelay(0)
                        .setUnFocusDelay(0)
                        .decorate(decorator)
                        .tooltip(tooltip);
        final AbstractFocusableGroupItem<ItemImpl>.FocusGroupExecutor focusExecutor =
                spy(tested.getFocusGroupExecutor());
        doAnswer(invocationOnMock -> {
            ((Runnable) invocationOnMock.getArguments()[1]).run();
            return null;
        }).when(focusExecutor).accept(any(Group.class),
                                      any(Runnable.class));
        focusExecutor.unFocus();
        verify(focusExecutor,
               times(1)).setAlpha(AbstractFocusableGroupItem.ALPHA_UNFOCUSED);
        verify(decorator,
               times(3)).hide();
        verify(tooltip,
               times(2)).hide();
    }
}
