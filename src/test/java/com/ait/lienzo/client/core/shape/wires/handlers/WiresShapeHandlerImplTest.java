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
package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeHandlerImplTest {

    @Mock
    private WiresShapeControl control;

    @Mock
    private WiresParentPickerControl parentPickerControl;

    @Mock
    private WiresDockingControl dockingControl;

    @Mock
    private WiresContainmentControl containmentControl;

    @Mock
    private WiresShapeHighlight<PickerPart.ShapePart> highlight;

    @Mock
    private WiresManager manager;

    @Mock
    private DragContext dragContext;

    private WiresShapeHandlerImpl tested;
    private Layer layer;
    private WiresShape shape;
    private WiresShape parent;

    @Before
    public void setup() {
        shape = new WiresShape(new MultiPath().circle(10));
        parent = new WiresShape(new MultiPath().circle(10));
        layer = new Layer();
        layer.add(shape.getGroup());
        layer.add(parent.getGroup());
        when(control.getDockingControl()).thenReturn(dockingControl);
        when(control.getContainmentControl()).thenReturn(containmentControl);
        when(control.getParentPickerControl()).thenReturn(parentPickerControl);
        when(parentPickerControl.getShape()).thenReturn(shape);
        when(parentPickerControl.getParent()).thenReturn(parent);
        when(parentPickerControl.getParentShapePart()).thenReturn(PickerPart.ShapePart.BODY);
        tested = new WiresShapeHandlerImpl(control,
                                           highlight,
                                           manager);
    }

    @Test
    public void testOnStartDrag() {
        when(dragContext.getDragStartX()).thenReturn(10);
        when(dragContext.getDragStartY()).thenReturn(5);
        tested.startDrag(dragContext);
        verify(control, times(1)).onMoveStart(eq(10d),
                                              eq(5d));
        verify(highlight, times(1)).highlight(eq(parent),
                                              eq(PickerPart.ShapePart.BODY));
    }

    @Test
    public void testOnAdjustHighlightDocking() {
        final Point2D dxy = new Point2D(2, 5);
        when(dockingControl.isAllow()).thenReturn(true);
        tested.startDrag(dragContext);
        final boolean adjusted = tested.adjust(dxy);
        assertFalse(adjusted);
        verify(control, times(1)).onMove(eq(2d),
                                         eq(5d));
        verify(highlight, times(1)).highlight(eq(parent),
                                              eq(PickerPart.ShapePart.BORDER));
    }

    @Test
    public void testOnAdjustHighlightContainment() {
        final Point2D dxy = new Point2D(2, 5);
        when(containmentControl.isAllow()).thenReturn(true);
        tested.startDrag(dragContext);
        final boolean adjusted = tested.adjust(dxy);
        assertFalse(adjusted);
        verify(control, times(1)).onMove(eq(2d),
                                         eq(5d));
        verify(highlight, atLeastOnce()).highlight(eq(parent),
                                                   eq(PickerPart.ShapePart.BODY));
    }

    @Test
    public void testOnAdjustHighlightError() {
        final Point2D dxy = new Point2D(2, 5);
        when(dockingControl.isAllow()).thenReturn(false);
        when(containmentControl.isAllow()).thenReturn(false);
        tested.startDrag(dragContext);
        final boolean adjusted = tested.adjust(dxy);
        assertFalse(adjusted);
        verify(control, times(1)).onMove(eq(2d),
                                         eq(5d));
        verify(highlight, times(1)).error(eq(parent),
                                          eq(PickerPart.ShapePart.BODY));
    }

    @Test
    public void testOnEndDragSuccess() {
        when(dragContext.getDragStartX()).thenReturn(10);
        when(dragContext.getDragStartY()).thenReturn(5);
        final NodeDragEndEvent endEvent = mock(NodeDragEndEvent.class);
        when(endEvent.getDragContext()).thenReturn(dragContext);
        when(control.onMoveComplete()).thenReturn(true);
        when(control.accept()).thenReturn(true);
        tested.startDrag(dragContext);
        tested.onNodeDragEnd(endEvent);
        verify(control, times(1)).onMoveStart(eq(10d),
                                              eq(5d));
        verify(control, times(1)).onMoveComplete();
        verify(control, times(1)).execute();
        verify(highlight, atLeastOnce()).restore();
        verify(control, never()).reset();
    }

    @Test
    public void testOnEndDragFailed() {
        when(dragContext.getDragStartX()).thenReturn(10);
        when(dragContext.getDragStartY()).thenReturn(5);
        final NodeDragEndEvent endEvent = mock(NodeDragEndEvent.class);
        when(endEvent.getDragContext()).thenReturn(dragContext);
        when(control.onMoveComplete()).thenReturn(true);
        when(control.accept()).thenReturn(false);
        tested.startDrag(dragContext);
        tested.onNodeDragEnd(endEvent);
        verify(control, times(1)).onMoveStart(eq(10d),
                                              eq(5d));
        verify(control, times(1)).onMoveComplete();
        verify(control, times(1)).reset();
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
    }
}
