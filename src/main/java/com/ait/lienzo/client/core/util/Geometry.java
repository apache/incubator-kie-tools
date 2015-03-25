/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.util;

import com.ait.lienzo.client.core.shape.BezierCurve;
import com.ait.lienzo.client.core.shape.QuadraticCurve;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.NFastDoubleArrayJSO;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;

/**
 * Static utility methods related to geometry and other math.
 *
 */
public final class Geometry
{
    private static final double NRRF_PRECISION = 0.000001;

    public static final double  RADIANS_90     = toRadians(90);

    public static final double  RADIANS_180    = toRadians(180);

    public static final double  TWO_PI         = (2.000 * Math.PI);

    public static final double  PI_180         = (Math.PI / 180.0);

    private Geometry()
    {
    }

    public static final BoundingBox getBoundingBox(final QuadraticCurve curve)
    {
        if (curve == null)
        {
            return null;
        }
        return getBoundingBoxOfCurve(curve.getControlPoints());
    }

    public static final BoundingBox getBoundingBox(final BezierCurve curve)
    {
        if (curve == null)
        {
            return null;
        }
        return getBoundingBoxOfCurve(curve.getControlPoints());
    }

    public static final BoundingBox getBoundingBoxOfCurve(final Point2DArray points)
    {
        if (null == points)
        {
            return null;
        }
        int size = points.size();

        if (size < 3)
        {
            return null;
        }
        double minx = Double.MAX_VALUE;

        double miny = Double.MAX_VALUE;

        double maxx = Double.MIN_VALUE;

        double maxy = Double.MIN_VALUE;

        final NFastDoubleArrayJSO xval = NFastDoubleArrayJSO.make();

        final NFastDoubleArrayJSO yval = NFastDoubleArrayJSO.make();

        for (int i = 0; i < size; i++)
        {
            final Point2D p = points.get(i);

            xval.push(p.getX());

            yval.push(p.getY());
        }
        final NFastDoubleArrayJSO inflections = getInflections(points, xval, yval);

        size = inflections.size();

        for (int i = 0; i < size; i++)
        {
            final double t = inflections.get(i);

            final double x = getValue(t, xval);

            final double y = getValue(t, yval);

            minx = Math.min(x, minx);

            maxx = Math.max(x, maxx);

            miny = Math.min(y, miny);

            maxy = Math.max(y, maxy);
        }
        return new BoundingBox(minx, miny, maxx, maxy);
    }

    private static final NFastDoubleArrayJSO getInflections(final Point2DArray points, final NFastDoubleArrayJSO xval, final NFastDoubleArrayJSO yval)
    {
        int size = points.size();

        int ordr = size - 1;

        NFastDoubleArrayJSO root;

        NFastDoubleArrayJSO tval = NFastDoubleArrayJSO.make();

        tval.push(0.0);

        tval.push(1.0);

        root = findAllRoots(1, xval);

        size = root.size();

        for (int i = 0; i < size; i++)
        {
            final double t = root.get(i);

            if (0 < t && t < 1)
            {
                tval.push(t);
            }
        }
        root = findAllRoots(1, yval);

        size = root.size();

        for (int i = 0; i < size; i++)
        {
            final double t = root.get(i);

            if (0 < t && t < 1)
            {
                tval.push(t);
            }
        }
        if (ordr > 2)
        {
            root = findAllRoots(2, xval);

            size = root.size();

            for (int i = 0; i < size; i++)
            {
                final double t = root.get(i);

                if (0 < t && t < 1)
                {
                    tval.push(t);
                }
            }
            root = findAllRoots(2, yval);

            size = root.size();

            for (int i = 0; i < size; i++)
            {
                final double t = root.get(i);

                if (0 < t && t < 1)
                {
                    tval.push(t);
                }
            }
        }
        return tval.uniq();
    }

    private static final NFastDoubleArrayJSO findAllRoots(final int derivative, final NFastDoubleArrayJSO values)
    {
        final NFastDoubleArrayJSO root = NFastDoubleArrayJSO.make();

        if (areLinear(values) || derivative == 1 && values.size() == 3)
        {
            if (derivative > 1)
            {
                return root;
            }
            final double beg = getDerivative(1, 0, values);

            final double end = getDerivative(1, 1, values);

            if ((beg > 0) && (end > 0))
            {
                return root;
            }
            if ((beg < 0) && (end < 0))
            {
                return root;
            }
            root.push(map(0, beg, end, 0, 1));

            return root;
        }
        for (double t = 0; t <= 1.0; t += 0.01)
        {
            final double r = Math.round(findRoots(derivative, t, values) / NRRF_PRECISION) * NRRF_PRECISION;

            if (root.contains(r))
            {
                continue;
            }
            root.push(r);
        }
        return root;
    }

    private static final double findRoots(int derivative, double t, NFastDoubleArrayJSO values)
    {
        return findRoots(derivative, t, values, 0);
    }

    private static final double findRoots(int derivative, double t, NFastDoubleArrayJSO values, double offset)
    {
        return findRootsRecursive(derivative, t, values, offset, 0);
    }

    /**
     * Newton-Raphson root finding (with depth capping).
     * Iteratively compute x(n+1) = x(n) - f(x)/f'(x),
     * until (x(n+1) - x(n)) approaches zero with a
     * satisfactory precision.
     */
    private static final double findRootsRecursive(final int derivative, final double t, final NFastDoubleArrayJSO values, final double offset, final int depth)
    {
        final double d0 = getDerivative(derivative + 0, t, values) - offset;

        final double df = getDerivative(derivative + 1, t, values);

        double t2 = t - (d0 / df);

        if (df == 0)
        {
            t2 = t - d0;
        }
        if (depth > 12)
        {
            if (Math.abs(t - t2) < NRRF_PRECISION)
            {
                return t2;
            }
            return -1;
        }
        if (Math.abs(t - t2) > NRRF_PRECISION)
        {
            return findRootsRecursive(derivative, t2, values, offset, depth + 1);
        }
        return t2;
    }

    private static final double map(final double value, final double istart, final double istop, final double ostart, final double ostop)
    {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    private static final double getDerivative(final int derivative, final double t, final NFastDoubleArrayJSO values)
    {
        final int n = values.size() - 1;

        if (n == 0)
        {
            return 0;
        }
        if (derivative == 0)
        {
            double value = 0;

            for (int k = 0; k <= n; k++)
            {
                value += binomials(n, k) * Math.pow(1 - t, n - k) * Math.pow(t, k) * values.get(k);
            }
            return value;
        }
        else
        {
            final NFastDoubleArrayJSO lowers = NFastDoubleArrayJSO.make();

            for (int k = 0; k < n; k++)
            {
                lowers.push(n * (values.get(k + 1) - values.get(k)));
            }
            return getDerivative(derivative - 1, t, lowers);
        }
    }

    private static final boolean areLinear(final NFastDoubleArrayJSO values)
    {
        final int sz = values.size();

        final double dx = values.get(1) - values.get(0);

        for (int i = 2; i < sz; i++)
        {
            final double rx = values.get(i) - values.get(i - 1);

            if (Math.abs(dx - rx) > 2)
            {
                return false;
            }
        }
        return true;
    }

    private static double[][] binomial_coefficients = { { 1 }, { 1, 1 } };

    private static final double binomials(final int n, final int k)
    {
        while (n >= binomial_coefficients.length)
        {
            int s = binomial_coefficients.length;

            double[][] update_coefficients = new double[s + 1][];

            for (int i = 0; i < s; i++)
            {
                update_coefficients[i] = binomial_coefficients[i];
            }
            double[] curr = binomial_coefficients[s - 1];

            double[] next = new double[s + 1];

            update_coefficients[s] = next;

            next[0] = 1;

            for (int i = 1; i < curr.length; i++)
            {
                next[i] = curr[i] + curr[i - 1];
            }
            next[s] = 1;

            binomial_coefficients = update_coefficients;
        }
        return binomial_coefficients[n][k];
    }

    private static final double polyterm(final int n, final int k, final double t)
    {
        return Math.pow((1 - t), n - k) * Math.pow(t, k);
    }

    private static final double getValue(final double t, final NFastDoubleArrayJSO values)
    {
        final int n = values.size() - 1;

        double value = 0;

        for (int k = 0; k <= n; k++)
        {
            double v = values.get(k);

            if (v == 0)
            {
                continue;
            }
            value += binomials(n, k) * polyterm(n, k, t) * v;
        }
        return value;
    }

    /**
     * Converts angle from degrees to radians.
     * 
     * @param angdeg
     * 
     * @return Angle converted from degrees to radians.
     */
    public static final double toRadians(final double angdeg)
    {
        return Math.toRadians(angdeg);
    }

    /**
     * Converts angle from radians to degrees.
     * 
     * @param angrad
     * 
     * @return Angle converted from radians to degrees.
     */
    public static final double toDegrees(final double angrad)
    {
        return Math.toDegrees(angrad);
    }

    public static final double slope(final Point2D a, final Point2D b)
    {
        return slope(b.getX(), a.getX(), b.getY(), a.getY());
    }

    public static final double slope(final double x1, final double y1, final double x2, final double y2)
    {
        final double dx = (x2 - x1);

        final double dy = (y2 - y1);

        return (Math.abs(dx) > Math.abs(dy)) ? (dy / dx) : (dx / dy);
    }

    public static final double distance(final double dx, final double dy)
    {
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public static final double getVectorRatio(final double[] u, final double[] v)
    {
        return ((u[0] * v[0]) + (u[1] * v[1])) / (distance(u[0], u[1]) * distance(v[0], v[1]));
    }

    public static final double getVectorAngle(final double[] u, final double[] v)
    {
        return (((u[0] * v[1]) < (u[1] * v[0])) ? -1 : 1) * Math.acos(getVectorRatio(u, v));
    }

    /**
     * Returns the length that forms angle1 with length1. ASA triangle
     * http://www.mathsisfun.com/algebra/trig-solving-asa-triangles.html
     * b/sinB = c/sin C
     * @param a0
     * @param s0
     * @param a1
     * @return
     */
    public static final double getLengthFromASA(final double a0, final double s0, final double a1)
    {
        return (s0 * Math.sin(a1)) / Math.sin(RADIANS_180 - a0 - a1);
    }

    /**
     * Returns the angle between s0 and s1
     * http://www.mathsisfun.com/algebra/trig-solving-sss-triangles.html
     * @param s0
     * @param s1
     * @param s2
     * @return
     */
    public static final double getAngleFromSSS(final double s0, final double s1, final double s2)
    {
        return Math.acos(((s0 * s0) + (s1 * s1) - (s2 * s2)) / (2 * (s0 * s1)));
    }

    public static final double getAngleBetweenTwoLines(final Point2D s0, final Point2D e0, final Point2D s1, final Point2D e1)
    {
        return getAngleFromSSS(s0.distance(e0), s1.distance(e1), s0.distance(e1));
    }
}
