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

package org.kie.workbench.common.dmn.client.shape.view;

import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DirectionalLineTest {

    public static final Point2D P_START = new Point2D(0, 0);
    public static final Point2D P_END = new Point2D(15.5, 50.2);
    private DirectionalLine tested;

    @Before
    public void setup() {
        tested = new DirectionalLine(P_START, P_END);
    }

    @Test
    public void testBoundingBoxGeneration() throws Exception {
        final BoundingBox bb1 = tested.getBoundingBox();
        assertEquals(0d, bb1.getMinX(), 0d);
        assertEquals(0d, bb1.getMinY(), 0d);
        assertEquals(15.5d, bb1.getMaxX(), 0d);
        assertEquals(50.2d, bb1.getMaxY(), 0d);
        // Assert that BB must be re-generated after refreshing as well.
        tested.refresh();
        final BoundingBox bb2 = tested.getBoundingBox();
        assertEquals(0d, bb2.getMinX(), 0d);
        assertEquals(0d, bb2.getMinY(), 0d);
        assertEquals(15.5d, bb2.getMaxX(), 0d);
        assertEquals(50.2d, bb2.getMaxY(), 0d);
    }

    @Test
    public void testParsePoints() throws Exception {
        final Attributes attr = mock(Attributes.class);
        final Point2DArray points = new Point2DArray(0d, 0d)
                .push(20d, 30d);
        when(attr.getControlPoints()).thenReturn(points);
        final boolean parsed = tested.parse(attr);
        assertTrue(parsed);
    }

    @Test
    public void testSkipParse() throws Exception {
        final Attributes attr = mock(Attributes.class);
        final Point2DArray points = new Point2DArray();
        when(attr.getControlPoints()).thenReturn(points);
        final boolean parsed = tested.parse(attr);
        assertFalse(parsed);
    }

    @Test
    public void testAdjustPoint() {
        final double X = 100d;
        final double Y = 200d;
        final double DELTA_X = 5d;
        final double DELTA_Y = 10d;
        //DeltaX and DeltaY are unused by IDirectionalMultiPointShape.adjustPoint(..) for DM
        final Point2D p = tested.adjustPoint(X, Y, DELTA_X, DELTA_Y);
        assertEquals(X, p.getX(), 0d);
        assertEquals(Y, p.getY(), 0d);
    }
}
