/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PolyLineTest {

    @Mock
    private Point2DArray point2DArray;

    private Attributes attributes;

    private PolyLine polyLine;

    @Before
    public void setup() {
        polyLine = new PolyLine();
        attributes = makeAttributes();
    }

    @Test
    public void testParseWhenPointsIsNull() {

        doReturn(null).when(attributes).getPoints();

        final boolean parse = polyLine.parse(attributes);

        assertFalse(parse);
    }

    @Test
    public void testParseWhenPointsSizeIsOne() {

        doReturn(point2DArray).when(attributes).getPoints();

        when(point2DArray.size()).thenReturn(1);
        when(point2DArray.noAdjacentPoints()).thenReturn(point2DArray);

        final boolean parse = polyLine.parse(attributes);

        assertTrue(parse);
        assertNull(polyLine.getTailOffsetPoint());
        assertNull(polyLine.getHeadOffsetPoint());
    }

    @Test
    public void testParseWhenPointsSizeIsGreaterThanOne() {

        final Point2D point1 = new Point2D(1, 1);
        final Point2D point2 = new Point2D(2, 2);

        doReturn(point2DArray).when(attributes).getPoints();

        when(point2DArray.size()).thenReturn(2);
        when(point2DArray.noAdjacentPoints()).thenReturn(point2DArray);
        when(point2DArray.get(0)).thenReturn(point1);
        when(point2DArray.get(1)).thenReturn(point2);

        final boolean parse = polyLine.parse(attributes);

        assertTrue(parse);
        assertEquals(point1, polyLine.getHeadOffsetPoint());
        assertEquals(point2, polyLine.getTailOffsetPoint());
    }

    private Attributes makeAttributes() {
        return spy(new Attributes(null));
    }
}
