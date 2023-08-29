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


package org.kie.workbench.common.stunner.client.lienzo.canvas.command;

import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.StunnerWiresShapeView;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoResizeNodeCommandTest {

    @Mock
    private Element node;

    @Mock
    private Shape shape;

    @Mock
    private StunnerWiresShapeView shapeView;

    @Mock
    private MagnetManager.Magnets magnets;

    @Mock
    private WiresMagnet magnet;

    private LienzoResizeNodeCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(shape.getShapeView()).thenReturn(shapeView);
        when(shapeView.getMagnets()).thenReturn(magnets);
        when(magnets.getMagnet(eq(0))).thenReturn(magnet);
        when(magnet.getX()).thenReturn(3d);
        when(magnet.getY()).thenReturn(4d);
        BoundingBox boundingBox = new BoundingBox(1, 2, 75, 50);
        tested = new LienzoResizeNodeCommand(node, boundingBox);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateResizeCommand() {
        tested.getOnResize().accept(shape);
        verify(shapeView, times(1)).refresh();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMagnetLocationProvider() {
        Point2D point = tested.getMagnetLocationProvider().apply(shape, 0);
        assertEquals(Point2D.create(3d, 4d), point);
    }
}
