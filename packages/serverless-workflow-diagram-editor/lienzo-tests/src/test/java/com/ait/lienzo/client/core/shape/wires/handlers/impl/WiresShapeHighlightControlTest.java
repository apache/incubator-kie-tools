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


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresContainmentControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeHighlightControlTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresLayerIndex index;

    @Mock
    private WiresShapeHighlight<PickerPart.ShapePart> highlight;

    @Mock
    private WiresShapeControl delegate;

    @Mock
    private WiresParentPickerControl parentPickerControl;

    @Mock
    private WiresDockingControl dockingControl;

    @Mock
    private WiresContainmentControl containmentControl;

    private WiresShapeHighlightControl tested;
    private WiresLayer wiresLayer;
    private Layer layer;
    private WiresShape shape;
    private WiresShape parent;

    @Before
    public void setup() {
        layer = spy(new Layer());
        wiresLayer = new WiresLayer(layer);
        shape = new WiresShape(new MultiPath().circle(10));
        parent = new WiresShape(new MultiPath().rect(0, 0, 100, 100));
        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(delegate.getParentPickerControl()).thenReturn(parentPickerControl);
        when(delegate.getContainmentControl()).thenReturn(containmentControl);
        when(delegate.getDockingControl()).thenReturn(dockingControl);
        when(parentPickerControl.getIndex()).thenReturn(index);
        when(parentPickerControl.getShape()).thenReturn(shape);
        when(parentPickerControl.getParent()).thenReturn(parent);
        when(parentPickerControl.getParentShapePart()).thenReturn(PickerPart.ShapePart.BODY);
        tested = new WiresShapeHighlightControl(wiresManager,
                                                () -> index,
                                                highlight,
                                                () -> delegate);
    }

    @Test
    public void testOnMoveStart() {
        checkOnMoveStart(parent, PickerPart.ShapePart.BODY);
    }

    @Test
    public void testOnMoveStartHighlightBorder() {
        shape.setDockedTo(parent);
        checkOnMoveStart(parent, PickerPart.ShapePart.BORDER);
    }

    @SuppressWarnings("unchecked")
    private void checkOnMoveStart(WiresShape parent, PickerPart.ShapePart shapePart) {
        double x = 1.1d;
        double y = 2.2d;
        tested.onMoveStart(x, y);
        verify(index, times(1)).exclude(eq(shape));
        verify(index, times(1)).build(eq(wiresLayer));
        verify(index, never()).clear();
        verify(index, never()).findShapeAt(anyInt(), anyInt());
        ArgumentCaptor<Supplier> indexCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(delegate, times(1)).useIndex(indexCaptor.capture());
        assertEquals(index, indexCaptor.getValue().get());
        verify(delegate, times(1)).onMoveStart(eq(x), eq(y));
        verify(delegate, never()).onMove(anyDouble(), anyDouble());
        verify(delegate, never()).onMoveComplete();
        verify(highlight, never()).restore();
        verify(highlight, times(1)).highlight(eq(parent), eq(shapePart));
    }

    @Test
    public void testOnMove() {
        when(containmentControl.isAllow()).thenReturn(true);
        when(dockingControl.isAllow()).thenReturn(false);
        checkOnMove(parent, PickerPart.ShapePart.BODY);
    }

    @Test
    public void testOnMoveHighlightBorder() {
        when(containmentControl.isAllow()).thenReturn(false);
        when(dockingControl.isAllow()).thenReturn(true);
        checkOnMove(parent, PickerPart.ShapePart.BORDER);
    }

    @Test
    public void testOnMoveNoParent() {
        when(containmentControl.isAllow()).thenReturn(false);
        when(dockingControl.isAllow()).thenReturn(false);
        checkOnMove(null, null);
    }

    private void checkOnMove(WiresShape parent, PickerPart.ShapePart shapePart) {
        double x = 1.1d;
        double y = 2.2d;
        tested.onMove(x, y);
        verify(delegate, times(1)).onMove(eq(x), eq(y));
        verify(delegate, never()).onMoveStart(anyDouble(), anyDouble());
        verify(delegate, never()).onMoveComplete();
        verify(highlight, never()).restore();
        if (null != parent) {
            verify(highlight, times(1)).highlight(eq(parent), eq(shapePart));
        }
        verify(index, never()).clear();
        verify(index, never()).build(any(WiresLayer.class));
        verify(index, never()).exclude(any(WiresLayer.class));
        verify(index, never()).findShapeAt(anyInt(), anyInt());
    }

    @Test
    public void testOnMoveComplete() {
        when(delegate.accept()).thenReturn(true);
        tested.onMoveComplete();

        verify(delegate, times(1)).onMoveComplete();
        verify(delegate, never()).onMoveStart(anyDouble(), anyDouble());
        verify(delegate, never()).onMove(anyDouble(), anyDouble());

        verify(delegate, times(1)).execute();
        verify(highlight, times(1)).restore();
        verify(highlight, never()).highlight(any(WiresShape.class), any(PickerPart.ShapePart.class));
        verify(index, times(1)).clear();
        verify(index, never()).build(any(WiresLayer.class));
        verify(index, never()).exclude(any(WiresLayer.class));
        verify(index, never()).findShapeAt(anyInt(), anyInt());
    }

    @Test
    public void testAccept() {
        tested.accept();
        verify(delegate, times(1)).accept();
        verify(delegate, never()).execute();
        verify(delegate, never()).clear();
        verify(delegate, never()).reset();
        verify(delegate, never()).destroy();
    }

    @Test
    public void testExecute() {
        tested.execute();
        verify(delegate, times(1)).execute();
        verify(delegate, never()).accept();
        verify(delegate, never()).clear();
        verify(delegate, never()).reset();
        verify(delegate, never()).destroy();
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(delegate, times(1)).clear();
        verify(delegate, never()).execute();
        verify(delegate, never()).accept();
        verify(delegate, never()).reset();
        verify(delegate, never()).destroy();
    }

    @Test
    public void testReset() {
        tested.reset();
        verify(delegate, times(1)).reset();
        verify(delegate, never()).execute();
        verify(delegate, never()).clear();
        verify(delegate, never()).accept();
        verify(delegate, never()).destroy();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(delegate, times(1)).destroy();
        verify(delegate, never()).execute();
        verify(delegate, never()).reset();
        verify(delegate, never()).clear();
        verify(delegate, never()).accept();
    }

    @Test
    public void testOnMouseClick() {
        MouseEvent mouseEvent = mock(MouseEvent.class);
        tested.onMouseClick(mouseEvent);
        verify(delegate, times(1)).onMouseClick(eq(mouseEvent));
        verify(delegate, never()).onMouseDown(any(MouseEvent.class));
        verify(delegate, never()).onMouseUp(any(MouseEvent.class));
    }

    @Test
    public void testOnMouseDown() {
        MouseEvent mouseEvent = mock(MouseEvent.class);
        tested.onMouseDown(mouseEvent);
        verify(delegate, times(1)).onMouseDown(eq(mouseEvent));
        verify(delegate, never()).onMouseClick(any(MouseEvent.class));
        verify(delegate, never()).onMouseUp(any(MouseEvent.class));
    }

    @Test
    public void testOnMouseUp() {
        MouseEvent mouseEvent = mock(MouseEvent.class);
        tested.onMouseUp(mouseEvent);
        verify(delegate, times(1)).onMouseUp(eq(mouseEvent));
        verify(delegate, never()).onMouseDown(any(MouseEvent.class));
        verify(delegate, never()).onMouseClick(any(MouseEvent.class));
    }

    @Test
    public void testGetAdjust() {
        Point2D adjust = new Point2D(1, 2);
        when(delegate.getAdjust()).thenReturn(adjust);
        assertEquals(adjust, tested.getAdjust());
    }

    @Test
    public void testIsOutOfBounds() {
        when(delegate.isOutOfBounds(1d, 2d)).thenReturn(true);
        boolean outOfBounds = tested.isOutOfBounds(1d, 2d);
        assertTrue(outOfBounds);
        verify(delegate, times(1)).isOutOfBounds(eq(1d), eq(2d));
    }

    @Test
    public void tesUseIndex() {
        Supplier<WiresLayerIndex> indexSupplier = new Supplier<WiresLayerIndex>() {

            @Override
            public WiresLayerIndex get() {
                return index;
            }
        };
        tested.useIndex(indexSupplier);
        verify(delegate, times(1)).useIndex(eq(indexSupplier));
    }

    @Test
    public void tesSetADControl() {
        AlignAndDistributeControl control = mock(AlignAndDistributeControl.class);
        tested.setAlignAndDistributeControl(control);
        verify(delegate, times(1)).setAlignAndDistributeControl(eq(control));
    }

    @Test
    public void testDelegateGetters() {
        WiresMagnetsControl magnetsControl = mock(WiresMagnetsControl.class);
        AlignAndDistributeControl adControl = mock(AlignAndDistributeControl.class);
        WiresDockingControl dockingControl = mock(WiresDockingControl.class);
        WiresContainmentControl containmentControl = mock(WiresContainmentControl.class);
        WiresParentPickerControl parentPickerControl = mock(WiresParentPickerControl.class);
        when(delegate.getMagnetsControl()).thenReturn(magnetsControl);
        when(delegate.getAlignAndDistributeControl()).thenReturn(adControl);
        when(delegate.getDockingControl()).thenReturn(dockingControl);
        when(delegate.getContainmentControl()).thenReturn(containmentControl);
        when(delegate.getParentPickerControl()).thenReturn(parentPickerControl);
        assertEquals(magnetsControl, tested.getMagnetsControl());
        assertEquals(adControl, tested.getAlignAndDistributeControl());
        assertEquals(dockingControl, tested.getDockingControl());
        assertEquals(containmentControl, tested.getContainmentControl());
        assertEquals(parentPickerControl, tested.getParentPickerControl());
    }
}
