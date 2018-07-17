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

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ButtonItemImplTest {

    private final BoundingBox boundingBox = new BoundingBox(0d,
                                                            0d,
                                                            100d,
                                                            200d);

    @Mock
    private AbstractFocusableGroupItem<?> groupItem;

    @Mock
    private IPrimitive groupItemPrim;

    @Mock
    private Group groupItemGroup;

    @Mock
    private BiConsumer<Group, Runnable> showExecutor;

    @Mock
    private BiConsumer<Group, Runnable> hideExecutor;

    @Mock
    private BoundingPoints boundingPoints;

    @Mock
    private HandlerRegistration clickReg;

    @Mock
    private HandlerRegistration dragStartReg;

    @Mock
    private HandlerRegistration dragMoveReg;

    @Mock
    private HandlerRegistration dragEndReg;

    @Mock
    private HandlerRegistrationManager registrations;

    private ButtonItemImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(groupItem.registrations()).thenReturn(registrations);
        when(groupItem.getPrimitive()).thenReturn(groupItemPrim);
        when(groupItemPrim.setListening(anyBoolean())).thenReturn(groupItemPrim);
        when(groupItemPrim.setDraggable(anyBoolean())).thenReturn(groupItemPrim);
        when(groupItemPrim.addNodeMouseClickHandler(any(NodeMouseClickHandler.class))).thenReturn(clickReg);
        when(groupItemPrim.addNodeDragStartHandler(any(NodeDragStartHandler.class))).thenReturn(dragStartReg);
        when(groupItemPrim.addNodeDragMoveHandler(any(NodeDragMoveHandler.class))).thenReturn(dragMoveReg);
        when(groupItemPrim.addNodeDragEndHandler(any(NodeDragEndHandler.class))).thenReturn(dragEndReg);
        when(groupItem.asPrimitive()).thenReturn(groupItemGroup);
        when(boundingPoints.getBoundingBox()).thenReturn(boundingBox);
        when(groupItem.getBoundingBox()).thenReturn(() -> boundingBox);
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
        tested =
                new ButtonItemImpl(groupItem)
                        .useHideExecutor(hideExecutor)
                        .useShowExecutor(showExecutor);
    }

    @Test
    public void testInit() {
        assertEquals(groupItemGroup,
                     tested.asPrimitive());
        assertEquals(groupItemPrim,
                     tested.getPrimitive());
        assertEquals(groupItem,
                     tested.getWrapped());
        assertEquals(boundingBox,
                     tested.getBoundingBox().get());
        assertFalse(tested.isVisible());
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
    public void testHide() {
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
    }

    @Test
    public void testClick() {
        NodeMouseClickHandler handler = mock(NodeMouseClickHandler.class);
        final ButtonItemImpl cascade = tested.onClick(handler);
        assertEquals(tested,
                     cascade);
        ;
        verify(groupItemPrim,
               times(1)).setListening(eq(true));
        verify(groupItemPrim,
               times(1)).addNodeMouseClickHandler(eq(handler));
        verify(registrations,
               times(1)).register(eq(clickReg));
        tested.destroy();
        verify(clickReg,
               times(1)).removeHandler();
    }

    @Test
    public void testDragStart() {
        NodeDragStartHandler handler = mock(NodeDragStartHandler.class);
        final ButtonItemImpl cascade = tested.onDragStart(handler);
        assertEquals(tested,
                     cascade);
        ;
        verify(groupItemPrim,
               times(1)).setDraggable(eq(true));
        verify(groupItemPrim,
               times(1)).addNodeDragStartHandler(eq(handler));
        verify(registrations,
               times(1)).register(eq(dragStartReg));
        tested.destroy();
        verify(dragStartReg,
               times(1)).removeHandler();
    }

    @Test
    public void testDragMove() {
        NodeDragMoveHandler handler = mock(NodeDragMoveHandler.class);
        final ButtonItemImpl cascade = tested.onDragMove(handler);
        assertEquals(tested,
                     cascade);
        ;
        verify(groupItemPrim,
               times(1)).setDraggable(eq(true));
        verify(groupItemPrim,
               times(1)).addNodeDragMoveHandler(eq(handler));
        verify(registrations,
               times(1)).register(eq(dragMoveReg));
        tested.destroy();
        verify(dragMoveReg,
               times(1)).removeHandler();
    }

    @Test
    public void testDragEmd() {
        NodeDragEndHandler handler = mock(NodeDragEndHandler.class);
        final ButtonItemImpl cascade = tested.onDragEnd(handler);
        assertEquals(tested,
                     cascade);
        ;
        verify(groupItemPrim,
               times(1)).setDraggable(eq(true));
        verify(groupItemPrim,
               times(1)).addNodeDragEndHandler(eq(handler));
        verify(registrations,
               times(1)).register(eq(dragEndReg));
        tested.destroy();
        verify(dragEndReg,
               times(1)).removeHandler();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(groupItem,
               times(1)).destroy();
    }
}
