/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class BoundsProviderFactoryTest
{
    private static final double PADDING = BoundsProviderFactory.FunctionalBoundsProvider.PADDING;

    @Test
    public void testBoundsSuppliesForPrimitiveShapes()
    {
        Layer     layer = new Layer();
        Rectangle prim1 = new Rectangle(50, 50);
        Circle    prim2 = new Circle(10);
        layer.add(prim1);
        layer.add(prim2);
        Bounds bounds = new BoundsProviderFactory.PrimitivesBoundsProvider().get(layer);
        assertEquals(-35d, bounds.getX(), 0);
        assertEquals(-35d, bounds.getY(), 0);
        assertEquals(85d + PADDING, bounds.getWidth(), 0);
        assertEquals(85d + PADDING, bounds.getHeight(), 0);
    }

    @Test
    public void testBoundsSuppliesForWiresShapes()
    {
        Layer        layer        = new Layer();
        WiresManager wiresManager = WiresManager.get(layer);
        WiresShape   shape1       = new WiresShape(new MultiPath().rect(0d, 0d, 50d, 50d));
        WiresShape   shape2       = new WiresShape(new MultiPath().circle(10d));
        wiresManager.register(shape1);
        wiresManager.register(shape2);
        Bounds bounds = new BoundsProviderFactory.WiresBoundsProvider().get(layer);
        assertEquals(0d, bounds.getX(), 0);
        assertEquals(0d, bounds.getY(), 0);
        assertEquals(50d + PADDING, bounds.getWidth(), 0);
        assertEquals(50d + PADDING, bounds.getHeight(), 0);
    }

    @Test
    public void testBuildBounds()
    {
        Bounds bounds = BoundsProviderFactory.buildBounds(new BoundingBox(-1d, -2d, 12d, 33.3d));
        assertEquals(-1d, bounds.getX(), 0d);
        assertEquals(-2d, bounds.getY(), 0d);
        assertEquals(13d, bounds.getWidth(), 0d);
        assertEquals(35.3d, bounds.getHeight(), 0d);
    }

    @Test
    public void testComputeAspectRatio()
    {
        Bounds bounds = BoundsProviderFactory.computeBoundsAspectRatio(0.5, new BoundingBox(-1d, -2d, 12d, 33.3d));
        assertEquals(-1d, bounds.getX(), 0d);
        assertEquals(-2d, bounds.getY(), 0d);
        assertEquals(17.65d, bounds.getWidth(), 0d);
        assertEquals(35.3d, bounds.getHeight(), 0d);
    }
}
