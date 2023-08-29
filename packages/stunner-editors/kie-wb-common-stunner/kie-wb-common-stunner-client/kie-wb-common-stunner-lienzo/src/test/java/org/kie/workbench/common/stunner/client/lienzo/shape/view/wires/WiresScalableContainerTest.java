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


package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresScalableContainerTest {

    private static final double WIDTH = 123.45d;
    private static final double HEIGHT = 2346.04d;
    private static final double BB_WIDTH = 234.5d;
    private static final double BB_HEIGHT = 45.05d;

    @Mock
    private IPrimitive<?> whPrimitive;

    @Mock
    private IPrimitive<?> nonScalablePrimitive;

    @Mock
    private Group tranformableContainer;

    @Mock
    private BoundingBox boundingBox;

    private WiresScalableContainer tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(tranformableContainer.getBoundingBox()).thenReturn(boundingBox);
        when(tranformableContainer.setX(anyDouble())).thenReturn(tranformableContainer);
        when(tranformableContainer.setY(anyDouble())).thenReturn(tranformableContainer);
        when(tranformableContainer.setScale(anyDouble(),
                                            anyDouble())).thenReturn(tranformableContainer);
        when(boundingBox.getWidth()).thenReturn(BB_WIDTH);
        when(boundingBox.getHeight()).thenReturn(BB_HEIGHT);
        when(whPrimitive.asNode()).thenReturn(mock(Node.class));
        this.tested = new WiresScalableContainer(tranformableContainer);
    }

    @Test
    public void testScaleScalableChildForWidthAndHeight() {
        final WiresScalableContainer wsc = tested.addScalable(whPrimitive);
        final double newWidth = 403.5d;
        final double newHeight = 6531.102d;
        final double x = 12;
        final double y = 20.5d;
        tested.scaleTo(x,
                       y,
                       newWidth,
                       newHeight);
        assertEquals(tested, wsc);
        verify(tranformableContainer, times(1)).setX(eq(x));
        verify(tranformableContainer, times(1)).setY(eq(y));
        verify(tranformableContainer, times(1)).setScale(eq(new Point2D(1.720682302771855, 144.97451720310767)));
    }

    @Test
    public void testScaleNonScalableChild() {
        final WiresScalableContainer wsc = tested.addScalable(nonScalablePrimitive);
        final double newSize = 403.5d;
        final double x = 12;
        final double y = 20.5d;
        tested.scaleTo(x,
                       y,
                       newSize,
                       newSize);
        assertEquals(tested,
                     wsc);
        verify(tranformableContainer,
               times(1)).add(eq(nonScalablePrimitive));
        verify(tranformableContainer,
               times(1)).setX(eq(x));
        verify(tranformableContainer,
               times(1)).setY(eq(y));
        ArgumentCaptor<Point2D> scaleRatioCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(tranformableContainer,
               times(1)).setScale(scaleRatioCaptor.capture());
        Point2D scaleRatio = scaleRatioCaptor.getValue();
        assertEquals(newSize / BB_WIDTH, scaleRatio.getX(), 0);
        assertEquals(newSize / BB_HEIGHT, scaleRatio.getY(), 0);
    }

    @Test
    public void testScaleTwiceSoCatchingRatio() {
        final WiresScalableContainer wsc = tested.addScalable(nonScalablePrimitive);
        final double newSize = 403.5d;
        final double x = 12;
        final double y = 20.5d;
        tested.scaleTo(x,
                       y,
                       newSize,
                       newSize);
        tested.scaleTo(x,
                       y,
                       newSize,
                       newSize);
        assertEquals(tested,
                     wsc);
        verify(tranformableContainer,
               times(1)).add(eq(nonScalablePrimitive));
        verify(tranformableContainer,
               times(1)).setX(eq(x));
        verify(tranformableContainer,
               times(1)).setY(eq(y));
        ArgumentCaptor<Point2D> scaleRatioCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(tranformableContainer,
               times(1)).setScale(scaleRatioCaptor.capture());
        Point2D scaleRatio = scaleRatioCaptor.getValue();
        assertEquals(newSize / BB_WIDTH, scaleRatio.getX(), 0);
        assertEquals(newSize / BB_HEIGHT, scaleRatio.getY(), 0);
    }

    @Test
    public void testScaleAll() {
        final WiresScalableContainer wsc0 = tested.addScalable(whPrimitive);
        final WiresScalableContainer wsc2 = tested.addScalable(nonScalablePrimitive);
        final double newSize = 403.5d;
        final double x = 12;
        final double y = 20.5d;
        tested.scaleTo(x,
                       y,
                       newSize,
                       newSize);
        assertEquals(tested,
                     wsc0);
        assertEquals(tested,
                     wsc2);
        verify(tranformableContainer,
               times(1)).add(eq(nonScalablePrimitive));
        verify(tranformableContainer,
               times(1)).setX(eq(x));
        verify(tranformableContainer,
               times(1)).setY(eq(y));
        ArgumentCaptor<Point2D> scaleRatioCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(tranformableContainer,
               times(1)).setScale(scaleRatioCaptor.capture());
        Point2D scaleRatio = scaleRatioCaptor.getValue();
        assertEquals(newSize / BB_WIDTH, scaleRatio.getX(), 0);
        assertEquals(newSize / BB_HEIGHT, scaleRatio.getY(), 0);
    }

    @Test
    public void testScalablePrimitiveButWithNotZero() {
        final WiresScalableContainer wsc = tested.addScalable(whPrimitive);
        final double newWidth = 0d;
        final double newHeight = 6531.102d;
        final double x = 12;
        final double y = 20.5d;
        tested.scaleTo(x,
                       y,
                       newWidth,
                       newHeight);
        assertEquals(tested,
                     wsc);
        verify(tranformableContainer, times(1)).setX(eq(x));
        verify(tranformableContainer, times(1)).setY(eq(y));
        verify(tranformableContainer, times(1)).setScale(eq(new Point2D(0.0, 144.97451720310767)));
    }

    @Test
    public void testScalablePrimitiveButHeightNotZero() {
        final WiresScalableContainer wsc = tested.addScalable(whPrimitive);
        final double newWidth = 6531.102d;
        final double newHeight = 0d;
        final double x = 12;
        final double y = 20.5d;
        tested.scaleTo(x,
                       y,
                       newWidth,
                       newHeight);
        assertEquals(tested,
                     wsc);
        verify(tranformableContainer, times(1)).setX(eq(x));
        verify(tranformableContainer, times(1)).setY(eq(y));
        verify(tranformableContainer, times(1)).setScale(eq(new Point2D(27.851181236673774, 0.0)));
    }
}
