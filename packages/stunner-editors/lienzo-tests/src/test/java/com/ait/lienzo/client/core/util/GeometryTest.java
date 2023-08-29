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


package com.ait.lienzo.client.core.util;

import com.ait.lienzo.client.core.shape.QuadraticCurve;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class GeometryTest {

    @Test
    public void testGreaterOrCloseEnough() {
        assertTrue(Geometry.greaterOrCloseEnough(3.1d, 3d));
        assertTrue(Geometry.greaterOrCloseEnough(3.000001d, 3d));
        assertFalse(Geometry.greaterOrCloseEnough(3d, 3.000001d));
    }

    @Test
    public void testLesserOrCloseEnough() {
        assertFalse(Geometry.lesserOrCloseEnough(3.1d, 3d));
        assertFalse(Geometry.lesserOrCloseEnough(3.000001d, 3d));
        assertTrue(Geometry.lesserOrCloseEnough(3d, 3.000001d));
    }

    @Test
    public void testSortSpecial() {
        double[] input = {1.1d, 3d, 0.5d, -1d};
        Geometry.sortSpecial(input);
        assertEquals(0.5d, input[0], 0);
        assertEquals(1.1d, input[1], 0);
        assertEquals(3.0d, input[2], 0);
        assertEquals(-1d, input[3], 0);
    }

    @Test
    public void testSgn() {
        assertEquals(-1, Geometry.sgn(-3));
        assertEquals(1, Geometry.sgn(3));
    }

    @Test
    public void testBezierCoeffs() {
        double[] result = Geometry.bezierCoeffs(15.3d, 255.6d, 143.6d, 65d);
        assertEquals(385.70000000000005d, result[0], 0);
        assertEquals(-1056.8999999999999d, result[1], 0);
        assertEquals(720.9d, result[2], 0);
        assertEquals(15.3d, result[3], 0);
        double[] result1 = Geometry.bezierCoeffs(453.2d, 22.12d, 13.46d, .65d);
        assertEquals(-426.57d, result1[0], 0);
        assertEquals(1267.26d, result1[1], 0);
        assertEquals(-1293.24d, result1[2], 0);
        assertEquals(453.2d, result1[3], 0);
    }

    @Test
    public void testLinearRoots() {
        double[] result = Geometry.linearRoots(15.4323123d, 1.234412d);
        assertEquals(-1d, result[0], 0);
        assertEquals(-1d, result[1], 0);
        assertEquals(-1d, result[2], 0);
        double[] result1 = Geometry.linearRoots(0.0000001d, -0.0000002d);
        assertEquals(-1d, result1[0], 0);
        assertEquals(-1d, result1[1], 0);
        assertEquals(-1d, result1[2], 0);
        double[] result2 = Geometry.linearRoots(15.4d, -1.2d);
        assertEquals(0.07792207792207792d, result2[0], 0);
        assertEquals(-1d, result2[1], 0);
        assertEquals(-1d, result2[2], 0);
    }

    @Test
    public void testQuadraticRoots() {
        double[] result = Geometry.quadraticRoots(15.4323123d, 1.234412d, 7.354d);
        assertEquals(-1d, result[0], 0);
        assertEquals(-1d, result[1], 0);
        assertEquals(-1d, result[2], 0);
        double[] result1 = Geometry.quadraticRoots(0.0000001d, 0d, 7.354d);
        assertEquals(-1d, result1[0], 0);
        assertEquals(-1d, result1[1], 0);
        assertEquals(-1d, result1[2], 0);
        double[] result2 = Geometry.quadraticRoots(-15.4323123d, 1.234412d, 7.354);
        assertEquals(-1d, result2[0], 0);
        assertEquals(0.7314654055347382d, result2[1], 0);
        assertEquals(-1d, result2[2], 0);
        double[] result3 = Geometry.quadraticRoots(0.00001d, 0.00001d, 0.00001d);
        assertEquals(-0.5d, result3[0], 0);
        assertEquals(-0.5d, result3[1], 0);
        assertEquals(-1d, result3[2], 0);
    }

    @Test
    public void testQuadraticToCubic() {
        double[] result = Geometry.quadraticToCubic(1.2d, 5.3d, 34.3d, 56.5d, 34.2, 45.221d).toArray();
        assertEquals(1.2d, result[0], 0);
        assertEquals(5.3d, result[1], 0);
        assertEquals(23.266666666666662d, result[2], 0);
        assertEquals(39.43333333333333d, result[3], 0);
        assertEquals(34.266666666666666d, result[4], 0);
        assertEquals(52.74033333333333d, result[5], 0);
        assertEquals(34.2d, result[6], 0);
        assertEquals(45.221d, result[7], 0);
    }

    @Test
    public void testGetBoundingBoxForQuadraticCurve() {
        BoundingBox result = Geometry.getBoundingBoxForQuadraticCurve(Point2DArray.fromArrayOfPoint2D(new Point2D(1.13d, 3.454d),
                                                                                                      new Point2D(234.4d, 2.2d),
                                                                                                      new Point2D(0.34d, 111d)));
        assertEquals(0d, result.getMinX(), 0);
        assertEquals(0d, result.getMinY(), 0);
        assertEquals(69.66118680175845d, result.getMaxX(), 0);
        assertEquals(3.454d, result.getMaxY(), 0);
    }

    @Test
    public void testGetBoundingBox() {
        BoundingBox result = Geometry.getBoundingBox(new QuadraticCurve(1.3d, 2.4d, 6.54d, 67.5d));
        assertEquals(0d, result.getMinX(), 0);
        assertEquals(0d, result.getMinY(), 0);
        assertEquals(0.38518518518518524d, result.getMaxX(), 0);
        assertEquals(0.7111111111111111d, result.getMaxY(), 0);
    }

    @Test
    public void testCubicRoots() {
        double[] result = Geometry.cubicRoots(new double[]{1.2d, 5.3d, 34.3d, 56.5d});
        assertEquals(0d, result[0], 0);
        assertEquals(-1d, result[1], 0);
        assertEquals(-1d, result[2], 0);
        double[] result1 = Geometry.cubicRoots(new double[]{1.2d, 1.3d, 0d, 0d});
        assertEquals(0.6388888888888888d, result1[0], 0);
        assertEquals(0.6388888888888888d, result1[1], 0);
        assertEquals(-1d, result1[2], 0);
    }

    @Test
    public void testNonIntersectLineCurve() {
        final double[] xval = new double[]{1.2d, 1.2d, 0d, 0d};
        final double[] yval = new double[]{0d, 0d, 2.1d, 2.1d};
        final double[] lx = new double[]{0d, 0d};
        final double[] ly = new double[]{0d, 0d};
        Point2DArray result = Geometry.intersectLineCurve(xval, yval, lx, ly);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testIntersectLineCurve() {
        final double[] xval = new double[]{50, 230, 150, 50};
        final double[] yval = new double[]{20, 30, 60, 100};
        final double[] lx = new double[]{0, 250};
        final double[] ly = new double[]{0, 100};
        Point2DArray result = Geometry.intersectLineCurve(xval, yval, lx, ly);
        assertEquals(2, result.size());
        assertEquals(50d, result.get(0).getX(), 0.00001);
        assertEquals(20d, result.get(0).getY(), 0.00001);
        assertEquals(new Point2D(144.49725985049355d, 57.79890394019752d), result.get(1));
    }

    @Test
    public void testIntersectLineCircle() {
        Point2DArray result = Geometry.intersectLineCircle(new Point2D(0d, 0d),
                                                           new Point2D(1d, 1d),
                                                           new Point2D(0d, 1d),
                                                           1);
        assertFalse(result.isEmpty());
        assertEquals(1d, result.get(0).getX(), 0);
        assertEquals(1d, result.get(0).getY(), 0);
        assertEquals(0d, result.get(1).getX(), 0);
        assertEquals(0d, result.get(1).getY(), 0);
    }

    @Test
    public void testNonIntersectLineCircle() {
        Point2DArray result = Geometry.intersectLineCircle(new Point2D(0d, 0d),
                                                           new Point2D(1d, 1d),
                                                           new Point2D(0d, 1d),
                                                           0.5);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testIntersectPointWithinBounding() {
        boolean result = Geometry.intersectPointWithinBounding(new Point2D(0d, 0d),
                                                               new Point2D(0d, 0d),
                                                               new Point2D(1d, 1d));
        assertTrue(result);
    }

    @Test
    public void testNonIntersectPointWithinBounding() {
        boolean result = Geometry.intersectPointWithinBounding(new Point2D(0d, 0d),
                                                               new Point2D(1d, 1d),
                                                               new Point2D(0d, 1d));
        assertFalse(result);
    }

    @Test
    public void testGetProjectionWhenNoIntersection() {
        Point2D intersection = new Point2D(1, 2);
        Point2D result = Geometry.getProjection(intersection, intersection, 0);
        assertEquals(new Point2D(0, 0), result);
    }
}