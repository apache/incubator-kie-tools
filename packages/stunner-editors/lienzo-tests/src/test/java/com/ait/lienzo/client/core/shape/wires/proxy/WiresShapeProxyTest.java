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


package com.ait.lienzo.client.core.shape.wires.proxy;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImpl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresShapeProxyTest {

    @Mock
    private WiresManager wiresManager;

    @Mock
    private WiresControlFactory wiresControlFactory;

    @Mock
    private WiresShapeHighlight shapeHighlight;

    @Mock
    private WiresLayer wiresLayer;

    @Mock
    private Consumer<WiresShape> shapeAcceptor;

    @Mock
    private Consumer<WiresShape> shapeDestroyer;

    @Mock
    private WiresShapeControlImpl shapeControl;

    @Mock
    private WiresParentPickerControlImpl parentPickerControl;

    @Mock
    private AlignAndDistributeControl alignAndDistributeControl;

    @Mock
    private WiresLayerIndex wiresLayerIndex;

    private WiresShapeProxy tested;
    private WiresShape shape;
    private Layer layer;

    @Before
    public void setup() {
        layer = spy(new Layer());
        when(wiresManager.getControlFactory()).thenReturn(wiresControlFactory);
        when(wiresManager.getLayer()).thenReturn(wiresLayer);
        when(wiresLayer.getLayer()).thenReturn(layer);

        shape = spy(new WiresShape(new MultiPath().rect(0, 0, 10, 10)));
        shape.setControl(shapeControl);

        when(shapeControl.getParentPickerControl()).thenReturn(parentPickerControl);
        when(shapeControl.getAlignAndDistributeControl()).thenReturn(alignAndDistributeControl);
        when(parentPickerControl.getShape()).thenReturn(shape);
        when(parentPickerControl.getIndex()).thenReturn(wiresLayerIndex);
        when(wiresControlFactory.newIndex(eq(wiresManager))).thenReturn(wiresLayerIndex);
        when(wiresControlFactory.newShapeHighlight(eq(wiresManager))).thenReturn(shapeHighlight);

        tested = new WiresShapeProxy(wiresManager,
                                     () -> shape,
                                     shapeAcceptor,
                                     shapeDestroyer);
    }

    @Test
    public void testStart() {
        double x = 1.1d;
        double y = 2.3d;
        tested.start(x, y);
        assertStart(x, y);
    }

    @Test
    public void testStartInSomeParent() {
        double x = 2d;
        double y = 5d;
        WiresContainer parent = mock(WiresContainer.class);
        when(shape.getParent()).thenReturn(parent);
        Point2D parentLoc = new Point2D(1d, 2d);
        when(parent.getComputedLocation()).thenReturn(parentLoc);
        tested.start(x, y);
        verify(shape, times(1)).removeFromParent();
        verify(wiresLayer, times(1)).add(eq(shape));
        assertStart(3d, 7d);
    }

    @Test
    public void testMove() {
        double sx = 1.1d;
        double sy = 2.3d;
        Point2D startLocation = new Point2D(sx, sy);
        double dx = 0.1d;
        double dy = 0.2d;
        Point2D offset = startLocation.copy().offset(dx, dy);
        tested.start(sx, sy);
        tested.move(dx, dy);
        verify(shapeControl, times(1)).onMove(eq(dx), eq(dy));
        assertEquals(offset, shape.getLocation());
        verify(layer, atLeastOnce()).batch();
    }

    @Test
    public void testEndSuccess() {
        when(shapeControl.isAccepted()).thenReturn(true);
        assertEnd();
        verify(shapeAcceptor, times(1)).accept(eq(shape));
        verify(shapeDestroyer, never()).accept(any(WiresShape.class));
    }

    @Test
    public void testEndFailed() {
        when(shapeControl.isAccepted()).thenReturn(false);
        assertEnd();
        verify(shapeDestroyer, times(1)).accept(eq(shape));
        verify(shapeAcceptor, never()).accept(any(WiresShape.class));
    }

    private void assertEnd() {
        double sx = 1.1d;
        double sy = 2.3d;
        double dx = 0.1d;
        double dy = 0.2d;
        tested.start(sx, sy);
        tested.move(dx, dy);
        tested.end();
        verify(shapeControl, times(1)).onMoveComplete();
        verify(wiresLayerIndex, atLeastOnce()).clear();
        verify(shapeHighlight, atLeastOnce()).restore();
        verify(layer, atLeastOnce()).batch();
    }

    private void assertStart(double x, double y) {
        Point2D location = new Point2D(x, y);
        assertEquals(location, shape.getLocation());
//        InOrder updateShapeAndThenCallControl = Mockito.inOrder(shape, alignAndDistributeControl, shapeControl, layer);
//        updateShapeAndThenCallControl.verify(shape, times(1)).setLocation(eq(location));
//        updateShapeAndThenCallControl.verify(alignAndDistributeControl, times(1)).refresh(eq(false), eq(true));
//        updateShapeAndThenCallControl.verify(shapeControl, times(1)).onMoveStart(eq(x), eq(y));
//        updateShapeAndThenCallControl.verify(layer, atLeastOnce()).batch();
    }
}
