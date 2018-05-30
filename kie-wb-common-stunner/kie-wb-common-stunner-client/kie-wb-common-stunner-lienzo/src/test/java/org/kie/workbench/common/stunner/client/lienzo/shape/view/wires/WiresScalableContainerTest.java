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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.Attributes;
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
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private Attributes whAttributes;

    @Mock
    private IPrimitive<?> nonScalablePrimitive;

    @Mock
    private Attributes nonScalableAttributes;

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
        when(nonScalablePrimitive.getAttributes()).thenReturn(nonScalableAttributes);
        when(whPrimitive.asNode()).thenReturn(mock(Node.class));
        when(whPrimitive.getAttributes()).thenReturn(whAttributes);
        when(whAttributes.getDouble(Attribute.WIDTH.getProperty())).thenReturn(WIDTH);
        when(whAttributes.getDouble(Attribute.HEIGHT.getProperty())).thenReturn(HEIGHT);
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
        assertEquals(tested,
                     wsc);
        verify(whAttributes,
               times(1)).setX(eq(x));
        verify(whAttributes,
               times(1)).setY(eq(y));
        verify(whAttributes,
               times(1)).setWidth(eq(newWidth));
        verify(whAttributes,
               times(1)).setHeight(eq(newHeight));
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
        verify(whAttributes,
               times(1)).setX(eq(x));
        verify(whAttributes,
               times(1)).setY(eq(y));
        verify(whAttributes,
               times(1)).setWidth(eq(newSize));
        verify(whAttributes,
               times(1)).setHeight(eq(newSize));
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
        verify(whAttributes,
               times(1)).setX(eq(x));
        verify(whAttributes,
               times(1)).setY(eq(y));
        verify(whAttributes,
               never()).setWidth(anyDouble());
        verify(whAttributes,
               times(1)).setHeight(eq(newHeight));
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
        verify(whAttributes,
               times(1)).setX(eq(x));
        verify(whAttributes,
               times(1)).setY(eq(y));
        verify(whAttributes,
               times(1)).setWidth(eq(newWidth));
        verify(whAttributes,
               never()).setHeight(anyDouble());
    }
}
