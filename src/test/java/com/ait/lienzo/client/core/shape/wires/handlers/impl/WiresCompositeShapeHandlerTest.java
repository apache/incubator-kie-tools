/*
 *
 *    Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.Collections;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresCompositeShapeHandlerTest {

    @Mock
    private WiresCompositeControl control;

    @Mock
    private WiresCompositeControl.Context context;

    @Mock
    private WiresShapeHighlight<PickerPart.ShapePart> highlight;

    @Mock
    private WiresManager manager;

    @Mock
    private DragContext dragContext;

    @Mock
    private WiresLayerIndex index;

    private WiresCompositeShapeHandler tested;

    private WiresShape parent;

    @Before
    public void setup() {
        parent = new WiresShape(new MultiPath().circle(10));
        when(control.getSharedParent()).thenReturn(parent);
        when(control.getContext()).thenReturn(context);
        when(context.getShapes()).thenReturn(Collections.<WiresShape>emptyList());
        when(context.getConnectors()).thenReturn(Collections.<WiresConnector>emptyList());
        tested = spy(new WiresCompositeShapeHandler(new Supplier<WiresLayerIndex>() {
            @Override
            public WiresLayerIndex get() {
                return index;
            }
        }, control, highlight, manager));
        doReturn(index).when(tested).getIndex();
    }

    @Test
    public void testOnStartDrag() {
        when(dragContext.getDragStartX()).thenReturn(10);
        when(dragContext.getDragStartY()).thenReturn(5);
        tested.startDrag(dragContext);
        verify(control, times(1)).onMoveStart(eq(10d),
                                              eq(5d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuiltIndexOnStartDrag() {
        when(dragContext.getDragStartX()).thenReturn(10);
        when(dragContext.getDragStartY()).thenReturn(5);
        WiresShape shape = new WiresShape(new MultiPath());
        when(context.getShapes()).thenReturn(Collections.singletonList(shape));
        tested.startDrag(dragContext);
        verify(index, times(1)).exclude(eq(shape));
        ArgumentCaptor<Supplier> indexCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(control, times(1)).useIndex(indexCaptor.capture());
        Supplier<WiresLayerIndex> value = indexCaptor.getValue();
        assertEquals(index, value.get());
    }

    @Test
    public void testOnAdjustHighlightDocking() {
        final Point2D dxy = new Point2D(2, 5);
        when(control.isAllowed()).thenReturn(true);
        tested.startDrag(dragContext);
        final boolean adjusted = tested.adjust(dxy);
        assertFalse(adjusted);
        verify(control, times(1)).onMove(eq(2d),
                                         eq(5d));
        verify(highlight, times(1)).highlight(eq(parent),
                                              eq(PickerPart.ShapePart.BODY));
        verify(highlight, never()).error(any(WiresShape.class),
                                         any(PickerPart.ShapePart.class));
        verify(highlight, never()).restore();
    }

    @Test
    public void testOnAdjustHighlightError() {
        final Point2D dxy = new Point2D(2, 5);
        when(control.isAllowed()).thenReturn(false);
        tested.startDrag(dragContext);
        final boolean adjusted = tested.adjust(dxy);
        assertFalse(adjusted);
        verify(control, times(1)).onMove(eq(2d),
                                         eq(5d));
        verify(highlight, times(1)).error(eq(parent),
                                          eq(PickerPart.ShapePart.BODY));
        verify(highlight, never()).highlight(any(WiresShape.class),
                                             any(PickerPart.ShapePart.class));
        verify(highlight, never()).restore();
    }

    @Test
    public void testOnAdjustRestore() {
        final Point2D dxy = new Point2D(2, 5);
        when(control.getSharedParent()).thenReturn(null);
        tested.startDrag(dragContext);
        final boolean adjusted = tested.adjust(dxy);
        assertFalse(adjusted);
        verify(control, times(1)).onMove(eq(2d),
                                         eq(5d));
        verify(highlight, never()).highlight(any(WiresShape.class),
                                             any(PickerPart.ShapePart.class));
        verify(highlight, never()).error(any(WiresShape.class),
                                         any(PickerPart.ShapePart.class));
        verify(highlight, times(1)).restore();
    }

    @Test
    public void testOnEndDragSuccess() {

        final int adjustedX = 10;
        final int adjustedY = 5;
        final Point2D distanceAdjusted = new Point2D(adjustedX, adjustedY);
        final NodeDragEndEvent endEvent = mock(NodeDragEndEvent.class);

        when(dragContext.getDistanceAdjusted()).thenReturn(distanceAdjusted);
        when(dragContext.getDragStartX()).thenReturn(adjustedX);
        when(dragContext.getDragStartY()).thenReturn(adjustedY);
        when(endEvent.getDragContext()).thenReturn(dragContext);
        when(control.onMove(anyDouble(), anyDouble())).thenReturn(false);
        when(control.accept()).thenReturn(true);

        tested.startDrag(dragContext);
        tested.onNodeDragEnd(endEvent);

        verify(control).onMoveStart(eq(10d), eq(5d));
        verify(control).onMoveComplete();
        verify(control).execute();
        verify(highlight, atLeastOnce()).restore();
        verify(control, never()).reset();

        // Verify index is being cleared.
        verify(index, times(1)).clear();
    }

    @Test
    public void testOnEndDragFailed() {

        final int adjustedX = 10;
        final int adjustedY = 5;
        final Point2D distanceAdjusted = new Point2D(adjustedX, adjustedY);
        final NodeDragEndEvent endEvent = mock(NodeDragEndEvent.class);

        when(dragContext.getDistanceAdjusted()).thenReturn(distanceAdjusted);
        when(dragContext.getDragStartX()).thenReturn(adjustedX);
        when(dragContext.getDragStartY()).thenReturn(adjustedY);
        when(endEvent.getDragContext()).thenReturn(dragContext);
        when(control.onMove(anyDouble(), anyDouble())).thenReturn(false);
        when(control.accept()).thenReturn(false);

        tested.startDrag(dragContext);
        tested.onNodeDragEnd(endEvent);

        verify(control).onMoveStart(eq(10d), eq(5d));
        verify(control).onMoveComplete();
        verify(control).reset();
        verify(highlight, atLeastOnce()).restore();
        verify(control, never()).execute();
    }

    @Test
    public void testReset() {
        tested.startDrag(dragContext);
        tested.reset();
        verify(dragContext, times(1)).reset();
        verify(control, times(1)).reset();
        verify(highlight, atLeastOnce()).restore();
        // Verify index is being cleared.
        verify(index, times(1)).clear();
    }
}
