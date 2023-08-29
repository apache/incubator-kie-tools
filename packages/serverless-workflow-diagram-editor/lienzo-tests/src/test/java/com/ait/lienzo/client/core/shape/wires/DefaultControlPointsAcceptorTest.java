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


package com.ait.lienzo.client.core.shape.wires;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultControlPointsAcceptorTest {

    @Mock
    private WiresConnector connector;

    private IControlPointsAcceptor.DefaultControlPointsAcceptor tested;

    @Before
    public void setUp() {
        tested = new IControlPointsAcceptor.DefaultControlPointsAcceptor(true);
    }

    @Test
    public void testAdd() {
        boolean result = tested.add(connector, 0, new Point2D(1, 3));
        assertTrue(result);
        verify(connector, times(1)).addControlPoint(eq(1d), eq(3d), eq(0));
        verify(connector, never()).destroyControlPoints(any(int[].class));
        verify(connector, never()).moveControlPoint(anyInt(), any(Point2D.class));
    }

    @Test
    public void testMove() {
        Point2DArray points = Point2DArray.fromArrayOfDouble(0d, 1d);
        boolean result = tested.move(connector, points);
        assertTrue(result);
        verify(connector, times(1)).moveControlPoint(eq(0), eq(new Point2D(0d, 1d)));
        verify(connector, never()).addControlPoint(anyDouble(), anyDouble(), anyInt());
        verify(connector, never()).destroyControlPoints(any(int[].class));
    }

    @Test
    public void testDelete() {
        boolean result = tested.delete(connector, 0);
        assertTrue(result);
        verify(connector, times(1)).destroyControlPoints(eq(new int[]{0}));
        verify(connector, never()).addControlPoint(anyDouble(), anyDouble(), anyInt());
        verify(connector, never()).moveControlPoint(anyInt(), any(Point2D.class));
    }
}
