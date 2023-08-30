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


package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerBoundsProviderFactoryTest {

    private Layer layer;
    private WiresLayer wiresLayer;
    private WiresShape shape1;
    private WiresShape shape2;

    @Before
    public void init() {
        layer = new Layer();
        wiresLayer = new WiresLayer(layer);
        shape1 = spy(new WiresShape(new MultiPath().circle(50))
                .setLocation(new Point2D(100, 33)));
        shape2 = spy(new WiresShape(new MultiPath().circle(230))
                .setLocation(new Point2D(15, 120)));
        doNothing().when(shape1).shapeMoved();
        doNothing().when(shape2).shapeMoved();
        wiresLayer.add(shape1);
        wiresLayer.add(shape2);
    }

    @Test
    public void testNewProvider() {
        BoundsProviderFactory.WiresBoundsProvider provider = StunnerBoundsProviderFactory.newProvider();
        Bounds bounds = provider.build(provider.getAll(wiresLayer));
        assertEquals(0d, bounds.getX(), 0d);
        assertEquals(0d, bounds.getY(), 0d);
        assertEquals(1260d, bounds.getWidth(), 0d);
        assertEquals(580d + StunnerBoundsProviderFactory.PADDING, bounds.getHeight(), 0d);
    }

    @Test
    public void testComputeSizesPreservingRatio() {
        final int width = 330;
        final int height = 110;
        final int computeWidth = StunnerBoundsProviderFactory.computeWidth(height);
        final int computeHeight = StunnerBoundsProviderFactory.computeHeight(width);
        assertEquals(220, computeWidth);
        assertEquals(165, computeHeight);
    }
}
