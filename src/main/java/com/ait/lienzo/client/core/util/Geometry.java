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

import java.util.HashSet;
import java.util.Set;

import com.ait.lienzo.client.core.shape.BezierCurve;
import com.ait.lienzo.client.core.shape.QuadraticCurve;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;

/**
 * Static utility methods related to geometry and other math.
 *
 */
public final class Geometry
{
    private static final double NRRF_PRECISION = 0.000001;

    public static final double  RADIANS_0      = toRadians(0);

    public static final double  RADIANS_90     = toRadians(90);

    public static final double  RADIANS_180    = toRadians(180);

    public static final double  RADIANS_270    = toRadians(270);

    public static final double  RADIANS_360    = toRadians(360);

    public static final double  RADIANS_450    = toRadians(450);

    public static final double  RADIANS_540    = toRadians(540);

    public static final double  RADIANS_630    = toRadians(630);

    public static final double  PI_180         = Math.PI / 180.0;

    public static final double  TWO_PI         = 2.000 * Math.PI;

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

    public static BoundingBox getBoundingBoxOfArcTo(Point2D p0, Point2D p1, Point2D p2, double r)
    {
        Point2DArray arcPoints = getCanvasArcToPoints(p0, p1, p2, r);
        BoundingBox box = getBoundingBoxOfArc(arcPoints.get(0), arcPoints.get(1), arcPoints.get(2), r);
        if ( !arcPoints.get(0).equals(p0) )
        {
            box.add(p0); //p0 is always the start point of the path, but not necessary of the arc - depending on the radius
        }
        return box;
    }

    public static BoundingBox getBoundingBoxOfArc(Point2D ps, Point2D pc, Point2D pe, double r)
    {
        double xs = ps.getX();
        double ys = ps.getY();
        double xe  = pe.getX();
        double ye = pe.getY();;

        Point2D p0 = new Point2D(xs > xe ? xs : xe, pc.getY()); // the length doesn't matter, just take largest x

        double as = Geometry.getAngleBetweenTwoLines(ps, pc, p0);
        if ( ps.getY() < pc.getY() )
        {
            // deduct from 360, if angle is above
            as = Geometry.RADIANS_360 - as;
        }

        double ae = Geometry.getAngleBetweenTwoLines(pe, pc, p0);
        if ( pe.getY() < pc.getY() )
        {
            // deduct from 360, if angle is above
            ae = Geometry.RADIANS_360 - ae;
        }

        if ( ae < as ) {
            if ( as - ae <= RADIANS_180 )
            {
                // less than 180, so it's counter clockwise, just reverse.
                double t = ae;
                ae = as;
                as = t;
            }
            else
            {
                // this is clockwise, so add 360, to ensure it continues to be clockwise and continuous numbers
                ae += Geometry.RADIANS_360;
            }
        }

        double xmin = 0, xmax = 0;
        double ymin = 0, ymax = 0;

        if (xs<xe)
        {
            xmin=xs;
            xmax=xe;
        }
        else
        {
            xmin=xe;
            xmax=xs;
        }

        if (ys<ye)
        {
            ymin=ys;
            ymax=ye;
        }
        else
        {
            ymin=ye;
            ymax=ys;
        }

        if (ae>RADIANS_90 )
        {
            if (as<RADIANS_90)
            {
                ymax=pc.getY()+r;
            }
            if (ae>RADIANS_180)
            {
                if (as<RADIANS_180)
                {
                    xmin=pc.getX()-r;
                }
                if (ae>RADIANS_270)
                {
                    if (as<RADIANS_270)
                    {
                        ymin=pc.getY()-r;
                    }
                    if (ae>RADIANS_360)
                    {
                        xmax=pc.getX()+r;
                        if (ae>RADIANS_450)
                        {
                            ymax=pc.getY()+r;
                            if (ae>RADIANS_540)
                            {
                                xmin=pc.getX()-r;
                                if (ae>RADIANS_630)
                                {
                                    ymin =pc.getY()-r;
                                }
                            }
                        }
                    }
                }
            }
        }

        BoundingBox box = new BoundingBox(xmin, ymin, xmax, ymax);

        return box;
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
        return (angdeg / 180.0 * Math.PI);
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
        return (angrad * 180.0 / Math.PI);
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

    public static Point2DArray getPoints(NFastDoubleArrayJSO doubles, Point2D p0)
    {
        Point2DArray array = new Point2DArray();

        array.push(p0);

        final int size = doubles.size();

        for (int i = 0; i < size; i += 2)
        {
            array.push(new Point2D(doubles.get(i), doubles.get(i + 1)));
        }
        return array;
    }

    /**
     /**
     * Returns the length that is opposite a0
     * http://www.mathsisfun.com/algebra/trig-solving-asa-triangles.html
     * b/sinB = c/sin C
     * @param a0
     * @param s0
     * @param a1
     * @return
     */
    public static final double getLengthFromASA(final double a0, final double s0, final double a1)
    {
        return (s0 * Math.sin(a0)) / Math.sin(RADIANS_180 - a0 - a1);
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

    public static final double getAngleBetweenTwoLines(final Point2D p0, final Point2D p1, final Point2D p2)
    {
        return getAngleFromSSS(p0.distance(p1), p1.distance(p2), p0.distance(p2));
    }

    public static final boolean collinear(final Point2D p0, final Point2D p1, final Point2D p2)
    {
        if (p0.equals(p1) || p1.equals(p2))
        {
            return true;
        }
        Point2D u1 = p1.sub(p0).unit();

        Point2D u2 = p2.sub(p1).unit();

        return u1.equals(u2);
    }

    public static final boolean isOrthogonal(final Point2D p0, final Point2D p1, final Point2D p2)
    {
        if (p0.getX() == p1.getX() && p1.getY() == p2.getY() || p0.getY() == p1.getY() && p1.getX() == p2.getX())
        {
            return true;
        }
        return false;
    }

    /**
     * This will build a PathPartList of lines and arcs from the Point2DArray.
     * The radius is the size of the arc for the line joins.
     * For each join the radius is capped at 1/2 the length of the smallest line in the three points
     * Collinear points are detected and handled as a straight line
     * If p0 and plast are the same it will close of the shape with an arc.
     * If p0 and plast are not the same, they will be left as lines starting at p0
     * and ending at plast.
     * For convention p0,p2 and p4 are used for the three points in the line.
     * p1 and p2 refer to the calculated arc offsets for the new start and end points of the two lines.
     *
     * For maths see Example 1 http://www.rasmus.is/uk/t/F/Su55k02.htm
     * @param list
     * @param points
     * @param radius
     */
    public static final void drawArcJoinedLines(final PathPartList list, final Point2DArray points, final double radius)
    {
        final int size = points.size();

        Point2D p0 = points.get(0);

        Point2D p2 = points.get(1);

        Point2D p0new = null;

        Point2D plast = points.get(size - 1);

        Point2D plastmin1 = points.get(size - 2);

        double closingRadius = 0;

        // check if start and finish have same point (i.e. is the line closed)

        boolean closed = false;

        if ((p0.getX() == plast.getX()) && (p0.getY() == plast.getY()))
        {
            closed = true;
        }
        if (closed && false == Geometry.collinear(plastmin1, p0, p2))
        {
            p0new = new Point2D(0, 0);

            plast = new Point2D(0, 0);

            closingRadius = closingArc(list, plastmin1, p0, p2, plast, p0new, radius);
        }
        for (int i = 2; i < size; i++)
        {
            Point2D p4 = points.get(i);

            if (Geometry.collinear(p0, p2, p4))
            {
                list.L(p2.getX(), p2.getY());
            }
            else
            {
                drawLines(list, p0, p2, p4, radius);
            }
            p0 = p2;

            p2 = p4;
        }
        list.L(plast.getX(), plast.getY());

        if (p0new != null)
        {
            p0 = points.get(0);

            list.A(p0.getX(), p0.getY(), p0new.getX(), p0new.getY(), closingRadius);

            list.Z();
        }
    }

    private static double closingArc(final PathPartList list, Point2D p0, Point2D p2, Point2D p4, Point2D plast, Point2D p0new, double radius)
    {
        Point2D p1 = new Point2D();

        Point2D p3 = new Point2D();

        double closingRadius = adjustStartEndOffsets(p0, p2, p4, radius, p1, p3);

        list.M(p3.getX(), p3.getY());

        plast.setX(p1.getX());

        plast.setY(p1.getY());

        p0new.setX(p3.getX());

        p0new.setY(p3.getY());

        return closingRadius;
    }

    private static void drawLines(final PathPartList list, final Point2D p0, final Point2D p2, final Point2D p4, double radius)
    {
        Point2D p1 = new Point2D();

        Point2D p3 = new Point2D();

        radius = adjustStartEndOffsets(p0, p2, p4, radius, p1, p3);

        list.L(p1.getX(), p1.getY());

        list.A(p2.getX(), p2.getY(), p3.getX(), p3.getY(), radius);
    }

    private static double adjustStartEndOffsets(final Point2D p0, final Point2D p2, final Point2D p4, double radius, final Point2D p1, final Point2D p3)
    {
        Point2D dv0 = p2.sub(p0);

        Point2D dx0 = dv0.unit();

        Point2D dv1 = p2.sub(p4);

        Point2D dx1 = dv1.unit();

        double offset;

        if (isOrthogonal(p0, p2, p4))
        {
            radius = getCappedOffset(p0, p2, p4, radius);

            offset = radius;
        }
        else
        {
            // for maths see Example 1 http://www.rasmus.is/uk/t/F/Su55k02.htm
            double a0 = getAngleBetweenTwoLines(p0, p2, p4) / 2;

            offset = getLengthFromASA(RADIANS_90 - a0, radius, RADIANS_90);

            double cappedOffset = getCappedOffset(p0, p2, p4, offset);

            if (cappedOffset < offset)
            {
                // offset is larger than capped size. Adjust offset and recalculate new radius
                offset = cappedOffset;

                radius = getLengthFromASA(a0, offset, RADIANS_90);
            }
        }
        Point2D t = p2.sub(dx0.mul(offset));

        p1.setX(t.getX());

        p1.setY(t.getY());

        t = p2.sub(dx1.mul(offset));

        p3.setX(t.getX());

        p3.setY(t.getY());

        return radius;
    }

    /** this will check if the radius needs capping, and return a smaller value if it does */
    private static double getCappedOffset(final Point2D p0, final Point2D p2, final Point2D p4, double offset)
    {
        double l1 = p2.sub(p0).getLength();

        double l2 = p2.sub(p4).getLength();

        double smallest = (l1 < l2) ? l1 : l2;

        double maxRadius = smallest / 2; // it must be half, as there may be another radius on the other side, and they should not cross over.

        offset = offset > maxRadius ? maxRadius : offset;

        return offset;
    }

    public static final Point2DArray EMPTY = new Point2DArray();

    public static Point2D intersectLineLine(Point2D a0, Point2D a1, Point2D b0, Point2D b1 )
    {
        double denominator = (b1.getY() - b0.getY()) * (a1.getX() - a0.getX()) - (b1.getX() - b0.getX()) * (a1.getY() - a0.getY());

        if ( denominator != 0 ) {
            double numerator1 = (b1.getX() - b0.getX()) * (a0.getY() - b0.getY()) - (b1.getY() - b0.getY()) * (a0.getX() - b0.getX());
            double numerator2 = (a1.getX() - a0.getX()) * (a0.getY() - b0.getY()) - (a1.getY() - a0.getY()) * (a0.getX() - b0.getX());

            double a = numerator1 / denominator;
            double b = numerator2 / denominator;

            if ( 0 <= a && a <= 1 && 0 <= b && b <= 1 ) {
                return new Point2D( a0.getX() + a * (a1.getX() - a0.getX()), a0.getY() + a * (a1.getY() - a0.getY()));
            }
        }

        return null;
    }

    /**
     * Returns the points the line intersects the arcTo path. Note that as arcTo's points are actually two
     * lines form p1 at a tangent to the arc's circle, it can draw a line from p0 to the start of the arc
     * which forms another potential intersect point.
     *
     * @param a0 start of the line
     * @param a1 end of the line
     * @param p0 p0 to p1 forms one line on the arc's circle tangent
     * @param p1 p1 to p2 forms one line on the arc's circle tangent
     * @param p2 p1 to p2 forms one line on the arc's circle tangent
     * @param r the radius of the arc
     * @return
     */
    public static Point2DArray intersectLineArcTo(Point2D a0, Point2D a1, Point2D p0, Point2D p1, Point2D p2, double r)
    {
        Point2DArray arcPoints = getCanvasArcToPoints(p0, p1, p2, r);

        Point2DArray circleIntersectPoints = intersectLineCircle(a0, a1, arcPoints.get(1), r);

        Point2DArray arcIntersectPoints = new Point2DArray();


        Point2D ps = arcPoints.get(0);
        Point2D pe = arcPoints.get(2);

        Point2D left;
        Point2D right;
        if ( ps.getX() < pe.getX() )
        {
            left = ps;
            right = pe;
        }
        else
        {
            left = pe;
            right = ps;
        }

        if ( !ps.equals(p0))
        {
            // canvas draws a line form p0 to p1, this is a new potential intersection point
            Point2D t = intersectLineLine(p0, ps, a0, a1);
            if  ( t != null )
            {
                arcIntersectPoints.push(t);
            }
        }

        // As the intersect is on the circle, rather than the arc, it can return back two points.
        // However we know only one of those points is both on the arc and on the line.
        // This means a simple bounding box check on the intersect points and the line can be used.
        if ( circleIntersectPoints.size() > 0 )
        {
            Point2D t = circleIntersectPoints.get(0);
            boolean within = intersectLineBounding(t, a0, a1);

            // check which points are on the arc. page 4 http://www.geometrictools.com/Documentation/IntersectionLine2Circle2.pdf
            if ( within && t.sub( right ).dot(left.sub(right).perpendicular() ) >= 0 )
            {
                arcIntersectPoints.push(t);
            }
        }

        if ( circleIntersectPoints.size() == 2 )
        {
            Point2D t = circleIntersectPoints.get(1);
            boolean within = intersectLineBounding(t, a0, a1);

            // check which points are on the arc. page 4 http://www.geometrictools.com/Documentation/IntersectionLine2Circle2.pdf
            if ( within && t.sub(right).dot(left.sub(right).perpendicular() ) >= 0 )
            {
                arcIntersectPoints.push(t);
            }
        }

        return arcIntersectPoints;
    }

    private static boolean intersectLineBounding(Point2D p, Point2D a0, Point2D a1)
    {
        boolean withinX = false;
        if ( a0.getX() < a1.getX() )
        {
            withinX = p.getX() >= a0.getX() && p.getX() <= a1.getX();
        }
        else
        {
            withinX = p.getX() >= a1.getX() && p.getX() <= a0.getX();
        }

        boolean withinY = false;
        if ( a0.getY() < a1.getY() )
        {
            withinY = p.getY() >= a0.getY() && p.getY() <= a1.getY();
        }
        else
        {
            withinY = p.getY() >= a1.getY() && p.getY() <= a0.getY();
        }

        return withinX && withinY;
    }

    /**
     *
     * @param a0 start of the line
     * @param a1 end of the line
     * @param pc centore of the circle
     * @param r radius of the circle
     * @return
     */
    public static Point2DArray intersectLineCircle(Point2D a0, Point2D a1, Point2D pc, double r)
    {
        // http://stackoverflow.com/a/29067085
        // http://mathworld.wolfram.com/Circle-LineIntersection.html
        Point2D p1 = a0.sub(pc);
        Point2D p2 = a1.sub(pc);

        Point2D d = p2.sub(p1);

        double det = (p1.getX() * p2.getY()) - (p2.getX() * p1.getY());

        double dSq = (d.getX() * d.getX()) + (d.getY() * d.getY());

        double discrimant = r * r * dSq - det * det;

        if (discrimant < 0) {
            // line does not intersect
            return EMPTY;
        }

        if (discrimant == 0) {
            // line only intersects once, so the start or end is inside of the circle
            Point2DArray t = new Point2DArray();
            Point2D t0 = new Point2D(det * d.getY() / dSq + pc.getX(), -det * d.getX() / dSq + pc.getY());
            t.push( t0 );
            return t;
        }

        double discSqrt = Math.sqrt(discrimant);

        double sgn = 1;
        if (d.getY() < 0) {
            sgn = -1;
        }

        Point2DArray t = new Point2DArray();
        Point2D t0 = new Point2D((det * d.getY() + sgn * d.getX() * discSqrt) / dSq + pc.getX(), (-det * d.getX() + Math.abs(d.getY()) * discSqrt) / dSq + pc.getY());
        Point2D t1 = new Point2D((det * d.getY() - sgn * d.getX() * discSqrt) / dSq + pc.getX(), (-det * d.getX() - Math.abs(d.getY()) * discSqrt) / dSq + pc.getY());


        t.push( t0 );
        t.push( t1 );

        return t;
    }


    /**
     * Canvas arcTo's have a variable center, as points a, b and c form two lines from the same point at a tangent to the arc's cirlce.
     * This returns the arcTo arc start, center and end points.
     *
     * @param p0
     * @param p1
     * @param r
     * @return
     */
    public static Point2DArray getCanvasArcToPoints(Point2D p0, Point2D p1, Point2D p2, double r)
    {
        // see tangents drawn from same point to a circle
        // http://www.mathcaptain.com/geometry/tangent-of-a-circle.html
        double a0 = getAngleBetweenTwoLines(p0, p1, p2) / 2;
        double a1 = RADIANS_90 - a0;

        double l = getLengthFromASA(a1, r, RADIANS_90);

        Point2D left;
        Point2D right;
        boolean reverse = false;
        if ( p0.getX() < p2.getX() )
        {
            left = p0;
            right = p2;
        }
        else
        {
            left = p2;
            right = p0;
            reverse = true;
        }

        Point2D dv = p1.sub(left);
        Point2D dx = dv.unit();
        Point2D dl = dx.mul(l);
        Point2D ps = p1.sub(dl); // ps is arc start point

        Point2D pc = ps.add(dx.perpendicular().mul(r));

        dv = p1.sub(right);
        dx = dv.unit();
        dl = dx.mul(l);
        Point2D pe = p1.sub(dl); // ep is arc end point

        Point2DArray points = new Point2DArray();
        if ( !reverse )
        {
            points.push(ps, pc, pe);
        }
        else
        {
            points.push(pe, pc, ps);
        }
        return points;
    }

    public static Point2D[] getCardinalIntersects(PathPartList path)
    {
        Point2D[] cardinals = getCardinals(path);
        Point2D center = cardinals[0];

        // Entry 0 is M
        PathPartEntryJSO entry = path.get(0);
        NFastDoubleArrayJSO points = entry.getPoints();
        Point2D m = new Point2D( points.get(0), points.get(1) );
        Point2D start = m;

        Set<Point2D>[] intersections = new Set[cardinals.length-1]; // c is removed, so -1

        // A set is used as vertex's may intersect, so the start/end of two liens will intersect
        for ( int i = 1; i < path.size(); i++)
        {
            entry = path.get(i);
            switch (entry.getCommand())
            {
                case PathPartEntryJSO.LINETO_ABSOLUTE:
                {
                    points = entry.getPoints();
                    double x0 = points.get(0);
                    double y0 = points.get(1);
                    Point2D end = new Point2D(x0, y0);
                    for (int j = 1; j < cardinals.length; j++)
                    {
                        Point2D cardinal = cardinals[j];
                        Point2D intersectPoint = Geometry.intersectLineLine(center, cardinal, start, end);
                        if (intersectPoint != null)
                        {
                            addIntersect(intersections, j-1, intersectPoint);  // c is removed, so -1
                        }
                    }
                    start = end;
                    break;
                }
                case PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE:
                {
                    points = entry.getPoints();

                    double x0 = points.get(0);
                    double y0 = points.get(1);
                    Point2D p0 = new Point2D( x0, y0 );

                    double x1 = points.get(2);
                    double y1 = points.get(3);
                    Point2D p1 = new Point2D( x1, y1 );
                    Point2D end = p1;

                    double r = points.get(4);
                    for (int j = 1; j < cardinals.length; j++)
                    {
                        Point2D cardinal = cardinals[j];
                        Point2DArray intersectPoints = Geometry.intersectLineArcTo(center, cardinal, start, p0, p1, r);

                        if ( intersectPoints.size() > 0 )
                        {
                            for ( Point2D p : intersectPoints )
                            {
                                addIntersect(intersections, j-1, p); // c is removed, so -1
                            }

                        }
                    }
                    start = end;
                }
                break;
            }
        }
        return removeInnerPoints(center, intersections);
    }

    public static Point2D[] removeInnerPoints(Point2D c, Set<Point2D>[] pointSet)
    {
        Point2D[] points = new Point2D[pointSet.length];
        int i = 0;
        for ( Set<Point2D> set : pointSet)
        {
            double furthestDistance = 0;
            if ( set != null && !set.isEmpty() )
            {
                for ( Point2D p : set )
                {
                    double currentDistance = p.distance( c );
                    if ( currentDistance > furthestDistance )
                    {
                        furthestDistance = currentDistance;
                        points[i] = p;
                    }
                }
                i++;
            }
        }
        return points;
    }

    public static void addIntersect(Set<Point2D>[] cardinalList, int index, Point2D point)
    {
        Set<Point2D> intersects = cardinalList[index];
        if ( intersects == null )
        {
            intersects = new HashSet<Point2D>();
            cardinalList[index] = intersects;
        }

        intersects.add(point);
    }

    public static Point2D[] getCardinals(PathPartList path)
    {
        BoundingBox box = path.getBoundingBox();
        Point2D c = new Point2D(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2);
        Point2D n = new Point2D(c.getX(), box.getY());
        Point2D e = new Point2D(box.getX()+ box.getWidth() , c.getY());
        Point2D s = new Point2D(c.getX(), box.getY() + box.getHeight() );
        Point2D w = new Point2D(box.getX(), c.getY() );

        Point2D ne = new Point2D(e.getX(), n.getY());
        Point2D se = new Point2D(e.getX(), s.getY());
        Point2D sw = new Point2D(w.getX(), s.getY());
        Point2D nw = new Point2D(w.getX(), n.getY());

        Point2D[] ordinals = new Point2D[] { c, n, ne, e, se, s, sw, w, nw };

        return ordinals;
    }


    /**
     * Returns the NESW quadrant the point is in.  The delta from the center
     * NE x > 0, y < 0
     * SE x > 0, y >= 0
     * SW x <= 0, y >= 0
     * NW x <= 0, y < 0
     *
     * @param x0
     * @param y0
     * @param c
     * @return
     */
    private static int getQuadrant(double x0, double y0, Point2D c)
    {
        int xy;
        if ( x0 > c.getX()&&  y0 < c.getY() )
        {
            xy = 0;
        }
        else if  ( x0 > c.getX() && y0 >= c.getY() )
        {
            xy = 1;
        }
        else if  (  x0 <= c.getX() && y0 >= c.getY() )
        {
            xy = 2;
        }
        else //if  ( x0 <= c.getX() && y0 < c.getY() )
        {
            xy = 3;
        }

        return xy;
    }
}
