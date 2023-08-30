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


package org.kie.workbench.common.stunner.svg.client.shape.view;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGPrimitiveShapeTest {

    private SVGPrimitiveShape tested;
    private Shape<?> shape;

    @Before
    public void setup() throws Exception {
        shape = new Rectangle(10, 10)
                .setID("theShape")
                .setAlpha(0.5d)
                .setX(100d)
                .setY(200d)
                .setFillColor("fillColor")
                .setFillAlpha(0.1d)
                .setStrokeColor("strokeColor")
                .setStrokeAlpha(0.2d)
                .setStrokeWidth(0.3d);
        tested = new SVGPrimitiveShape(shape);
    }

    @Test
    public void testShapeAttributes() {
        assertEquals(shape, tested.get());
        assertEquals("theShape", tested.getUUID());
        assertEquals(100, tested.getShapeX(), 0d);
        assertEquals(200, tested.getShapeY(), 0d);
        assertEquals(0.5, tested.getAlpha(), 0d);
        assertEquals("fillColor", tested.getFillColor());
        assertEquals(0.1, tested.getFillAlpha(), 0d);
        assertEquals("strokeColor", tested.getStrokeColor());
        assertEquals(0.2, tested.getStrokeAlpha(), 0d);
        assertEquals(0.3, tested.getStrokeWidth(), 0d);
        tested.setDragEnabled(true);
        assertTrue(shape.isDraggable());
    }

    @Test
    public void testMoveMethods() {
        Shape<?> instance = mock(Shape.class);
        tested = new SVGPrimitiveShape(instance);
        tested.moveToTop();
        verify(instance, times(1)).moveToTop();
        tested.moveToBottom();
        verify(instance, times(1)).moveToBottom();
        tested.moveDown();
        verify(instance, times(1)).moveDown();
        tested.moveUp();
        verify(instance, times(1)).moveUp();
    }

    @Test
    public void testShadow() {
        Shape<?> instance = mock(Shape.class);
        tested = new SVGPrimitiveShape(instance);
        tested.setShadow("c1",
                         1,
                         2,
                         3);
        ArgumentCaptor<Shadow> shadowArgumentCaptor = ArgumentCaptor.forClass(Shadow.class);
        verify(instance, times(1)).setShadow(shadowArgumentCaptor.capture());
        Shadow shadow = shadowArgumentCaptor.getValue();
        assertEquals("c1", shadow.getColor());
        assertEquals(1, shadow.getBlur());
        assertEquals(2, shadow.getOffset().getX(), 0);
        assertEquals(3, shadow.getOffset().getY(), 0);
    }

    @Test
    public void testRemoveShadow() {
        Shape<?> instance = mock(Shape.class);
        tested = new SVGPrimitiveShape(instance);
        tested.removeShadow();
        verify(instance, times(1)).setShadow(eq(null));
    }
}
