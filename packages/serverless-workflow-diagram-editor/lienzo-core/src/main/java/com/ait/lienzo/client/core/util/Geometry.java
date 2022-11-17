/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.ait.lienzo.client.core.shape.AbstractMultiPathPartShape;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.QuadraticCurve;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import com.ait.lienzo.tools.client.collection.NFastDoubleArray;

/**
 * Static utility methods related to geometry and other math.
 */
public final class Geometry {

    private static final double NRRF_PRECISION = 0.000001;

    public static final double RADIANS_0 = toRadians(0);

    public static final double RADIANS_90 = toRadians(90);

    public static final double RADIANS_180 = toRadians(180);

    public static final double RADIANS_270 = toRadians(270);

    public static final double RADIANS_360 = toRadians(360);

    public static final double RADIANS_450 = toRadians(450);

    public static final double RADIANS_540 = toRadians(540);

    public static final double RADIANS_630 = toRadians(630);

    public static final double PI_180 = Math.PI / 180.0;

    public static final double TWO_PI = 2.000 * Math.PI;

    private Geometry() {
    }

    public static boolean closeEnough(final double a, final double b, final double slop) {
        return (Math.abs(a - b) < slop);
    }

    public static boolean closeEnough(final double a, final double b) {
        return (Math.abs(a - b) < NRRF_PRECISION);
    }

    public static boolean greaterOrCloseEnough(final double a, final double b) {
        return closeEnough(a, b) || (a > b);
    }

    public static boolean lesserOrCloseEnough(final double a, final double b) {
        return closeEnough(a, b) || (a < b);
    }

    public static BoundingBox getBoundingBoxOfArcTo(final Point2D p0, final Point2D p1, final Point2D p2, final double r) {
        final Point2DArray arcPoints = getCanvasArcToPoints(p0, p1, p2, r);

        final BoundingBox box = getBoundingBoxOfArc(arcPoints.get(0), arcPoints.get(1), arcPoints.get(2), r);

        if (!arcPoints.get(0).equals(p0)) {
            box.addPoint2D(p0);//p0 is always the start point of the path, but not necessary of the arc - depending on the radius
        }
        return box;
    }

    public static boolean clockwise(final double s, final double e) {
        if (s < e) {
            return true;
        } else if (s >= RADIANS_180 && s < RADIANS_360 && e >= RADIANS_0 && e < RADIANS_180) {
            return true;
        } else {
            return false;
        }
    }

    public static BoundingBox getBoundingBoxOfArc(final Point2D ps, final Point2D pc, final Point2D pe, final double r) {
        final double xs = ps.getX();

        final double ys = ps.getY();

        final double xe = pe.getX();

        final double ye = pe.getY();

        final Point2D p0 = new Point2D(xs > xe ? xs : xe, pc.getY());// the length doesn't matter, just take largest x

        double as = Geometry.getAngleBetweenTwoLines(ps, pc, p0);

        if (ps.getY() < pc.getY()) {
            // deduct from 360, if angle is above
            as = Geometry.RADIANS_360 - as;
        }
        double ae = Geometry.getAngleBetweenTwoLines(pe, pc, p0);

        if (pe.getY() < pc.getY()) {
            // deduct from 360, if angle is above
            ae = Geometry.RADIANS_360 - ae;
        }
        if (!clockwise(as, ae)) {
            // reverse to makeXY clockwise
            final double t = ae;
            ae = as;
            as = t;
        }
        if (ae < as) {
            // this only happens when as is before RADIANS_270 and and ae is after RADIANS_270
            ae += Geometry.RADIANS_360;
        }
        double xmin = 0;
        double xmax = 0;
        double ymin = 0;
        double ymax = 0;

        if (xs < xe) {
            xmin = xs;

            xmax = xe;
        } else {
            xmin = xe;

            xmax = xs;
        }
        if (ys < ye) {
            ymin = ys;

            ymax = ye;
        } else {
            ymin = ye;

            ymax = ys;
        }
        if (ae > RADIANS_90) {
            if (as < RADIANS_90) {
                ymax = pc.getY() + r;
            }
            if (ae > RADIANS_180) {
                if (as < RADIANS_180) {
                    xmin = pc.getX() - r;
                }
                if (ae > RADIANS_270) {
                    if (as < RADIANS_270) {
                        ymin = pc.getY() - r;
                    }
                    if (ae > RADIANS_360) {
                        xmax = pc.getX() + r;

                        if (ae > RADIANS_450) {
                            ymax = pc.getY() + r;

                            if (ae > RADIANS_540) {
                                xmin = pc.getX() - r;

                                if (ae > RADIANS_630) {
                                    ymin = pc.getY() - r;
                                }
                            }
                        }
                    }
                }
            }
        }
        return BoundingBox.fromDoubles(xmin, ymin, xmax, ymax);
    }

    private static final boolean areLinear(final NFastDoubleArray values) {
        final int sz = values.size();

        final double dx = values.get(1) - values.get(0);

        for (int i = 2; i < sz; i++) {
            final double rx = values.get(i) - values.get(i - 1);

            if (Math.abs(dx - rx) > 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param xval x coordinate values for [0]=segment_start [1]=cp1 [2]=cp2 [3]=segment_end
     * @param yval y coordinate values for [0]=segment_start [1]=cp1 [2]=cp2 [3]=segment_end
     * @param lx x coordinate values for [0]=line-start [1]=line_ed
     * @param ly y coordinate values for [0]=line-start [1]=line_ed
     * @return The intersection points in the cartesian axis
     */
    public static final Point2DArray intersectLineCurve(final double[] xval, final double[] yval, final double[] lx, final double[] ly) {
        final Point2DArray intersections = new Point2DArray();
        final double a = ly[1] - ly[0];
        final double b = lx[0] - lx[1];
        final double c = (lx[0] * (ly[0] - ly[1])) + (ly[0] * (lx[1] - lx[0]));

        final double[] bx = bezierCoeffs(xval[0], xval[1], xval[2], xval[3]);
        final double[] by = bezierCoeffs(yval[0], yval[1], yval[2], yval[3]);

        final double[] p = new double[4];

        p[0] = (a * bx[0]) + (b * by[0]); /*t^3*/
        p[1] = (a * bx[1]) + (b * by[1]); /*t^2*/
        p[2] = (a * bx[2]) + (b * by[2]); /*t*/
        p[3] = (a * bx[3]) + (b * by[3]) + c; /*1*/

        final double[] r = cubicRoots(p);

        //verify the roots are in bounds of the linear segment
        for (int i = 0; i < 3; i++) {
            final double t = r[i];

            final double ix = (bx[0] * Math.pow(t, 3)) + (bx[1] * Math.pow(t, 2)) + (bx[2] * t) + bx[3];
            final double iy = (by[0] * Math.pow(t, 3)) + (by[1] * Math.pow(t, 2)) + (by[2] * t) + by[3];

            // above is intersection point assuming infinitely long line segment,
            // makeXY sure we are also in bounds of the line
            double s;
            if ((lx[1] - lx[0]) != 0)           // if not vertical line
            {
                s = (ix - lx[0]) / (lx[1] - lx[0]);
            } else {
                s = (iy - ly[0]) / (ly[1] - ly[0]);
            }

            if (isBetween(0, t, 1) && isBetween(0, s, 1)) {
                intersections.push(new Point2D(ix, iy));
            }
        }

        return intersections;
    }

    public static boolean isBetween(final double min, final double value, final double max) {
        return greaterOrCloseEnough(value, min) && lesserOrCloseEnough(value, max);
    }

    public static double[] cubicRoots(final double[] p) {
        if (closeEnough(p[0], 0)) {
            return quadraticDerivitiveRoots(p);
        }

        final double[] roots = new double[3];

        final double a = p[1] / p[0];
        final double b = p[2] / p[0];
        final double c = p[3] / p[0];

        final double q = ((3 * b) - Math.pow(a, 2)) / 9;
        final double r = ((9 * a * b) - (27 * c) - (2 * Math.pow(a, 3))) / 54;
        final double d = Math.pow(q, 3) + Math.pow(r, 2);    // polynomial discriminant

        if (d >= 0) // complex or duplicate roots
        {
            final double sqrtD = Math.sqrt(d);
            final double s = sgn(r + sqrtD) * Math.pow(Math.abs(r + sqrtD), (1 / 3));
            final double t = sgn(r - sqrtD) * Math.pow(Math.abs(r - sqrtD), (1 / 3));

            roots[0] = s + t;                                   // real root
            roots[1] = (-a / 3) - ((s + t) / 2);                        // real part of complex root
            roots[2] = roots[1];                                // real part of complex root
            final double rootPair = Math.abs((Math.sqrt(3) * (s - t)) / 2); // complex part of root pair

            //discard complex roots
            if (rootPair != 0) {
                roots[1] = -1;
                roots[2] = -1;
            }
        } else {
            final double th = Math.acos(r / Math.sqrt(-Math.pow(q, 3)));
            roots[0] = (2 * Math.sqrt(-q) * Math.cos(th / 3)) - (a / 3);
            roots[1] = (2 * Math.sqrt(-q) * Math.cos((th + (2 * Math.PI)) / 3)) - (a / 3);
            roots[2] = (2 * Math.sqrt(-q) * Math.cos((th + (4 * Math.PI)) / 3)) - (a / 3);
        }

        // discard out of spec roots
        for (int i = 0; i < 3; i++) {
            if ((roots[i] < 0) || (roots[i] > 1.0)) {
                roots[i] = -1;
            }
        }

        // sort but place -1 at the end
        sortSpecial(roots);

        return roots;
    }

    public static final BoundingBox getBoundingBox(final QuadraticCurve curve) {
        if (curve == null) {
            return null;
        }
        Point2DArray points = curve.getControlPoints();
        return getBoundingBoxForQuadraticCurve(points);
    }

    public static BoundingBox getBoundingBoxForQuadraticCurve(final Point2DArray points) {
        NFastDoubleArray cubicPoints = quadraticToCubic(points.get(0).getX(), points.get(0).getY(),
                                                        points.get(1).getX(), points.get(1).getY(),
                                                        points.get(2).getX(), points.get(2).getY());

        double[] xval = new double[4];
        double[] yval = new double[4];
        for (int i = 0; i < (cubicPoints.size() / 2); i = i + 2) {
            xval[i] = cubicPoints.get(i);
            yval[i] = cubicPoints.get(i + 1);
        }

        return getBoundingBoxOfCubicCurve(xval, yval);
    }

    public static NFastDoubleArray quadraticToCubic(double x0, double y0, double cx, double cy, double x1, double y1) {
        double c0x = x0 + 2.0 / 3.0 * (cx - x0);
        double c0y = y0 + 2.0 / 3.0 * (cy - y0);
        double c1x = c0x + (x1 - x0) / 3.0;
        double c1y = c0y + (y1 - y0) / 3.0;
        return NFastDoubleArray.makeFromDoubles(x0, y0, c0x, c0y, c1x, c1y, x1, y1);
    }

    public static BoundingBox getBoundingBoxOfCubicCurve(double[] xval, double[] yval) {
        return getBoundingBoxOfCubicCurve(0, 0, xval, yval);
    }

    public static BoundingBox getBoundingBoxOfCubicCurve(final double computedLocationOffsetX, final double computedLocationOffsetY, double[] xval, double[] yval) {
        // https://stackoverflow.com/questions/2587751/an-algorithm-to-find-bounding-box-of-closed-bezier-curves/14429749#14429749

        double[] xroots = quadraticDerivitiveRoots(xval);
        double[] yroots = quadraticDerivitiveRoots(yval);

        BoundingBox box = new BoundingBox();
        box.add(xval[0], yval[0]);
        box.add(xval[3], yval[3]);

        addBezierPolynomial(xval, yval, xroots, box);
        addBezierPolynomial(xval, yval, yroots, box);
        return box;
    }

    private static void addBezierPolynomial(final double[] xval, final double[] yval, final double[] roots, final BoundingBox box) {
        for (int i = 0; i < roots.length; i++) {
            if (roots[i] >= 0) {
                double x = cubicBezierPoint(xval, roots[i]);
                double y = cubicBezierPoint(yval, roots[i]);
                box.add(x, y);
            }
        }
    }

    private static double cubicBezierPoint(double[] val, double t) {
        double s = 1 - t;
        double a = s * s * s;
        double b = 3 * s * s * t;
        double c = 3 * s * t * t;
        double d = t * t * t;

        return a * val[0] +
                b * val[1] +
                c * val[2] +
                d * val[3];
    }

    private static double[] quadraticDerivitiveRoots(double[] val) {
        double a = -3 * val[0] + 9 * val[1] - 9 * val[2] + 3 * val[3];
        double b = 6 * val[0] - 12 * val[1] + 6 * val[2];
        double c = 3 * val[1] - 3 * val[0];

        return quadraticRoots(a, b, c);
    }

    public static double[] quadraticRoots(final double a, final double b, final double c) {
        final double[] roots;
        if (closeEnough(a, 0)) {
            roots = linearRoots(b, c);
            return roots;
        }

        roots = new double[]{-1, -1, -1};
        final double dq = (b * b) - (4 * a * c); // quadratic discriminant
        if (dq > 0) {
            // used approach to avoid round-off, as per https://github.com/jdowner/bbox.js/blob/master/src/bbox.js
            final double rdq = Math.sqrt(dq);
            double t = (-b + rdq) / (2 * a);
            if (0 < t && t < 1) {
                roots[0] = t;
            }

            t = (-b - rdq) / (2 * a);
            if (0 < t && t < 1) {
                roots[1] = t;
            }
        } else if (closeEnough(dq, 0)) { // One real root.
            roots[0] = -b / (2 * a);
            roots[1] = roots[0];
        }

        return roots;
    }

    public static double[] linearRoots(final double a, final double b) {
        final double[] roots = new double[]{-1, -1, -1};
        if (!closeEnough(a, 0)) {
            final double t = -b / a;
            if (0 < t && t < 1) {
                roots[0] = t;
            }
        }
        return roots;
    }

    /**
     * sort, but place -1 at the end
     * @param d
     */
    public static void sortSpecial(final double[] d) {
        boolean flip;
        double temp;

        do {
            flip = false;
            for (int i = 0; i < (d.length - 1); i++) {
                if (((d[i + 1] >= 0) && (d[i] > d[i + 1])) ||
                        ((d[i] < 0) && (d[i + 1] >= 0))) {
                    flip = true;
                    temp = d[i];
                    d[i] = d[i + 1];
                    d[i + 1] = temp;
                }
            }
        }
        while (flip);
    }

    public static double[] bezierCoeffs(final double p0, final double p1, final double p2, final double p3) {
        final double[] z = new double[4];
        z[0] = -p0 + (3 * p1) + (-3 * p2) + p3;
        z[1] = ((3 * p0) - (6 * p1)) + (3 * p2);
        z[2] = (-3 * p0) + (3 * p1);
        z[3] = p0;

        return z;
    }

    public static int sgn(final double x) {
        return (x < 0.0) ? -1 : 1;
    }

    /**
     * Converts angle from degrees to radians.
     * @param angdeg
     * @return Angle converted from degrees to radians.
     */
    public static final double toRadians(final double angdeg) {
        return (angdeg / 180.0 * Math.PI);
    }

    /**
     * Converts angle from radians to degrees.
     * @param angrad
     * @return Angle converted from radians to degrees.
     */
    public static final double toDegrees(final double angrad) {
        return (angrad * 180.0 / Math.PI);
    }

    public static final double slope(final double x1, final double y1, final double x2, final double y2) {
        final double dx = (x2 - x1);

        final double dy = (y2 - y1);

        return (Math.abs(dx) > Math.abs(dy)) ? (dy / dx) : (dx / dy);
    }

    public static final double distance(final Point2D p0, final Point2D p1) {
        return distance(p0.getX(), p0.getY(), p1.getX(), p1.getY());
    }

    public static final double distance(final double x0, final double y0, double x1, double y1) {
        return distance(x1 - x0, y1 - y0);
    }

    public static final double distance(final double dx, final double dy) {
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    public static final double getVectorRatio(final double[] u, final double[] v) {
        return ((u[0] * v[0]) + (u[1] * v[1])) / (distance(u[0], u[1]) * distance(v[0], v[1]));
    }

    public static final double getVectorAngle(final double[] u, final double[] v) {
        return (((u[0] * v[1]) < (u[1] * v[0])) ? -1 : 1) * Math.acos(getVectorRatio(u, v));
    }

    /**
     * /**
     * Returns the length that is opposite a0
     * http://www.mathsisfun.com/algebra/trig-solving-asa-triangles.html
     * b/sinB = c/sin C
     * @param a0
     * @param s0
     * @param a1
     * @return
     */
    public static final double getLengthFromASA(final double a0, final double s0, final double a1) {
        return (s0 * Math.sin(a0)) / Math.sin(a1);
    }

    /**
     * Returns the angle between s0 and s1
     * http://www.mathsisfun.com/algebra/trig-solving-sss-triangles.html
     * @param s0
     * @param s1
     * @param s2
     * @return
     */
    public static final double getAngleFromSSS(final double s0, final double s1, final double s2) {
        return Math.acos(((s0 * s0) + (s1 * s1) - (s2 * s2)) / (2 * (s0 * s1)));
    }

    /**
     * Returns the angle between p2 -> p0 and p2 -> p2
     */
    public static final double getAngleBetweenTwoLines(final Point2D p0, final Point2D p1, final Point2D p2) {
        return getAngleFromSSS(p0.distance(p1), p1.distance(p2), p0.distance(p2));
    }

    /**
     * Returns the clockwise angle between three points.
     * It starts at p0, that goes clock-wise around c until it reaches p1
     * @param p0
     * @param c
     * @param p1
     * @return
     */
    public static double getClockwiseAngleBetweenThreePoints(Point2D p0, Point2D c, Point2D p1) {
        Point2D a = c.sub(p1);
        Point2D b = c.sub(p0);
        return Math.atan2(a.getY(), a.getX()) - Math.atan2(b.getY(), b.getX());
    }

    public static final boolean collinear(final Point2D p0, final Point2D p1, final Point2D p2) {
        return collinear(p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static final boolean collinear(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3) {
        return closeEnough((y1 - y2) * (x1 - x3), (y1 - y3) * (x1 - x2), 1e-9);
    }

    public static final boolean isOrthogonal(final Point2D p0, final Point2D p1, final Point2D p2) {
        return (p0.getX() == p1.getX() && p1.getY() == p2.getY() || p0.getY() == p1.getY() && p1.getX() == p2.getX());
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
     * <p>
     * For maths see Example 1 http://www.rasmus.is/uk/t/F/Su55k02.htm
     * @param list
     * @param points
     * @param radius
     */
    public static final void drawArcJoinedLines(final PathPartList list, final Point2DArray points, final double radius) {
        final int size = points.size();

        Point2D p0 = points.get(0);

        Point2D p2 = points.get(1);

        Point2D p0new = null;

        Point2D plast = points.get(size - 1);

        Point2D plastmin1 = points.get(size - 2);

        double closingRadius = 0;

        // check if start and finish have same point (i.e. is the line closed)

        boolean closed = false;

        if ((p0.getX() == plast.getX()) && (p0.getY() == plast.getY())) {
            closed = true;
        }
        if (closed && !Geometry.collinear(plastmin1, p0, p2)) {
            p0new = new Point2D(0, 0);

            plast = new Point2D(0, 0);

            closingRadius = closingArc(list, plastmin1, p0, p2, plast, p0new, radius);
        }
        for (int i = 2; i < size; i++) {
            Point2D p4 = points.get(i);

            if (Geometry.collinear(p0, p2, p4)) {
                list.L(p2.getX(), p2.getY());
            } else {
                drawLines(list, p0, p2, p4, radius);
            }
            p0 = p2;

            p2 = p4;
        }
        list.L(plast.getX(), plast.getY());

        if (p0new != null) {
            p0 = points.get(0);

            list.A(p0.getX(), p0.getY(), p0new.getX(), p0new.getY(), closingRadius);

            list.Z();
        }
    }

    public static final void drawArcJoinedLines(final PathPartList list, final PathPartList baseList, final Point2DArray basePoints, final double radius) {
        final int pointsSize = basePoints.size();

        final boolean closed = isClosed(baseList);

        for (int i = 0; i < pointsSize; i++) {
            final PathPartEntryJSO entry = baseList.get(i);

            final PathPartEntryJSO nextEntry = baseList.get(i + 1);

            Point2D p0 = basePoints.get(i - 1);

            final Point2D p2 = basePoints.get(i);

            Point2D p4 = basePoints.get(i + 1);

            if (closed) {
                if (i == 0) {
                    p0 = basePoints.get(pointsSize - 1);
                }
                if (i == pointsSize - 1) {
                    p4 = basePoints.get(0);
                }
            } else {
                if (i == 0 || i == pointsSize - 1) {
                    p0 = null;

                    p4 = null;
                }
            }
            boolean applyArcToList = false;

            if (isCorner(entry, nextEntry)) {
                if (p0 != null && p4 != null) {
                    if (!Geometry.collinear(p0, p2, p4)) {
                        applyArcToList = true;
                    }
                }
            }
            if (applyArcToList) {
                drawLines(list, p0, p2, p4, radius);
            } else {
                list.push(entry.copy());
            }
        }
        if (closed) {
            list.Z();
        }
    }

    private static final double closingArc(final PathPartList list, final Point2D p0, final Point2D p2, final Point2D p4, final Point2D plast, final Point2D p0new, final double radius) {
        final Point2D p1 = new Point2D(0, 0);

        final Point2D p3 = new Point2D(0, 0);

        final double closingRadius = adjustStartEndOffsets(p0, p2, p4, radius, p1, p3);

        list.M(p3.getX(), p3.getY());

        plast.setX(p1.getX());

        plast.setY(p1.getY());

        p0new.setX(p3.getX());

        p0new.setY(p3.getY());

        return closingRadius;
    }

    private static final void drawLines(final PathPartList list, final Point2D p0, final Point2D p2, final Point2D p4, double radius) {
        final Point2D p1 = new Point2D(0, 0);

        final Point2D p3 = new Point2D(0, 0);

        radius = adjustStartEndOffsets(p0, p2, p4, radius, p1, p3);

        if (list.size() == 0) {
            list.M(p1.getX(), p1.getY());
        } else {
            list.L(p1.getX(), p1.getY());
        }
        list.A(p2.getX(), p2.getY(), p3.getX(), p3.getY(), radius);
    }

    private static final double adjustStartEndOffsets(final Point2D p0, final Point2D p2, final Point2D p4, double radius, final Point2D p1, final Point2D p3) {
        final Point2D dv0 = p2.sub(p0);

        final Point2D dx0 = dv0.unit();

        final Point2D dv1 = p2.sub(p4);

        final Point2D dx1 = dv1.unit();

        double offset;

        if (isOrthogonal(p0, p2, p4)) {
            radius = getCappedOffset(p0, p2, p4, radius);

            offset = radius;
        } else {
            // for maths see Example 1 http://www.rasmus.is/uk/t/F/Su55k02.htm
            final double a0 = getAngleBetweenTwoLines(p0, p2, p4) / 2;

            offset = getLengthFromASA(RADIANS_90 - a0, radius, a0);

            final double cappedOffset = getCappedOffset(p0, p2, p4, offset);

            if (cappedOffset < offset) {
                // offset is larger than capped size. Adjust offset and recalculate new radius
                offset = cappedOffset;

                radius = getLengthFromASA(a0, offset, RADIANS_90 - a0);
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

    /**
     * this will check if the radius needs capping, and return a smaller value if it does
     */
    private static final double getCappedOffset(final Point2D p0, final Point2D p2, final Point2D p4, final double offset) {
        final double radius = Math.min(p2.sub(p0).getLength(), p2.sub(p4).getLength()) / 2;// it must be half, as there may be another radius on the other side, and they should not cross over.

        return ((offset > radius) ? radius : offset);
    }

    private static final boolean isCorner(PathPartEntryJSO e1, PathPartEntryJSO e2) {
        if ((e1 == null) || (e2 == null)) {
            return true;
        }
        final int c1 = e1.getCommand();

        final int c2 = e2.getCommand();

        if ((c1 == PathPartEntryJSO.MOVETO_ABSOLUTE) && (c2 == PathPartEntryJSO.LINETO_ABSOLUTE)) {
            return true;
        }
        if ((c1 == PathPartEntryJSO.LINETO_ABSOLUTE) && (c2 == PathPartEntryJSO.LINETO_ABSOLUTE)) {
            return true;
        }
        if ((c1 == PathPartEntryJSO.LINETO_ABSOLUTE) && (c2 == PathPartEntryJSO.CLOSE_PATH_PART)) {
            return true;
        }
        return false;
    }

    private static final boolean isClosed(PathPartList list) {
        final int listSize = list.size();

        if (listSize <= 2) {
            return false;
        }
        final PathPartEntryJSO part = list.get(listSize - 1);

        if (part.getCommand() == PathPartEntryJSO.CLOSE_PATH_PART) {
            return true;
        } else {
            return false;
        }
    }

    public static final Point2D intersectLineLine(final Point2D a0, final Point2D a1, final Point2D b0, final Point2D b1) {
        final double denominator = (b1.getY() - b0.getY()) * (a1.getX() - a0.getX()) - (b1.getX() - b0.getX()) * (a1.getY() - a0.getY());

        if (denominator != 0) {
            final double a = ((b1.getX() - b0.getX()) * (a0.getY() - b0.getY()) - (b1.getY() - b0.getY()) * (a0.getX() - b0.getX())) / denominator;

            final double b = ((a1.getX() - a0.getX()) * (a0.getY() - b0.getY()) - (a1.getY() - a0.getY()) * (a0.getX() - b0.getX())) / denominator;

            if (0 <= a && a <= 1 && 0 <= b && b <= 1) {
                return new Point2D(a0.getX() + a * (a1.getX() - a0.getX()), a0.getY() + a * (a1.getY() - a0.getY()));
            }
        }
        return null;
    }

    /**
     * Returns the points the line intersects the arcTo path. Note that as arcTo's points are actually two
     * lines form p1 at a tangent to the arc's circle, it can draw a line from p0 to the start of the arc
     * which forms another potential intersect point.
     * @param a0 start of the line
     * @param a1 end of the line
     * @param p0 p0 to p1 forms one line on the arc's circle tangent
     * @param p1 p1 to p2 forms one line on the arc's circle tangent
     * @param p2 p1 to p2 forms one line on the arc's circle tangent
     * @param r the radius of the arc
     * @return
     */
    public static final Point2DArray intersectLineArcTo(final Point2D a0, final Point2D a1, final Point2D p0, final Point2D p1, final Point2D p2, final double r) {
        final Point2DArray arcPoints = getCanvasArcToPoints(p0, p1, p2, r);

        final Point2DArray circleIntersectPoints = intersectLineCircle(a0, a1, arcPoints.get(1), r);

        final Point2DArray arcIntersectPoints = new Point2DArray();

        Point2D ps = arcPoints.get(0);

        Point2D pc = arcPoints.get(1);

        Point2D pe = arcPoints.get(2);

        if (!ps.equals(p0)) {
            // canvas draws a line form p0 to p1, this is a new potential intersection point
            final Point2D t = intersectLineLine(p0, ps, a0, a1);

            if (t != null) {
                arcIntersectPoints.push(t);
            }
        }
        if ((pe.sub(pc)).crossScalar((ps.sub(pc))) < 0) {
            // reverse to makeXY counterclockwise
            final Point2D t = pe;
            pe = ps;
            ps = t;
        }
        // As the intersect is on the circle, rather than the arc, it can return back two points.
        // However we know only one of those points is both on the arc and on the line.
        // This means a simple bounding box check on the intersect points and the line can be used.
        if (circleIntersectPoints.size() > 0) {
            final Point2D t = circleIntersectPoints.get(0);

            final boolean within = intersectPointWithinBounding(t, a0, a1);

            // check which points are on the arc. page 4 http://www.geometrictools.com/Documentation/IntersectionLine2Circle2.pdf
            if (within && t.sub(ps).dot(pe.sub(ps).perpendicular()) >= 0) {
                arcIntersectPoints.push(t);
            }
        }
        if (circleIntersectPoints.size() == 2) {
            final Point2D t = circleIntersectPoints.get(1);

            final boolean within = intersectPointWithinBounding(t, a0, a1);

            // check which points are on the arc. page 4 http://www.geometrictools.com/Documentation/IntersectionLine2Circle2.pdf
            if (within && t.sub(ps).dot(pe.sub(ps).perpendicular()) >= 0) {
                arcIntersectPoints.push(t);
            }
        }
        return arcIntersectPoints;
    }

    public static final boolean intersectPointWithinBounding(final Point2D p, final Point2D a0, final Point2D a1) {
        boolean withinX = false;

        if (a0.getX() < a1.getX()) {
            withinX = p.getX() >= a0.getX() && p.getX() <= a1.getX();
        } else {
            withinX = p.getX() >= a1.getX() && p.getX() <= a0.getX();
        }
        boolean withinY = false;

        if (a0.getY() < a1.getY()) {
            withinY = greaterOrCloseEnough(p.getY(), a0.getY()) && lesserOrCloseEnough(p.getY(), a1.getY());
        } else {
            withinY = greaterOrCloseEnough(p.getY(), a1.getY()) && lesserOrCloseEnough(p.getY(), a0.getY());
        }
        return withinX && withinY;
    }

    /**
     * @param a0 start of the line
     * @param a1 end of the line
     * @param pc centore of the circle
     * @param r radius of the circle
     * @return
     */
    public static final Point2DArray intersectLineCircle(final Point2D a0, final Point2D a1, final Point2D pc, final double r) {
        // http://stackoverflow.com/a/29067085
        // http://mathworld.wolfram.com/Circle-LineIntersection.html
        final Point2D p1 = a0.sub(pc);

        final Point2D p2 = a1.sub(pc);

        final Point2D d = p2.sub(p1);

        final double det = p1.crossScalar(p2);

        final double dSq = d.dot(d);

        final double discrimant = r * r * dSq - det * det;

        if (lesserOrCloseEnough(discrimant, 0)) {
            // line does not intersect
            return new Point2DArray();
        }
        if (closeEnough(discrimant, 0)) {
            // line only intersects once, so the start or end is inside of the circle
            return Point2DArray.fromArrayOfDouble(det * d.getY() / dSq + pc.getX(), -det * d.getX() / dSq + pc.getY());
        }
        final double discSqrt = Math.sqrt(discrimant);

        final double sgn = sgn(d.getY());
        final Point2DArray intr = Point2DArray.fromArrayOfDouble((((det * d.getY()) + (sgn * d.getX() * discSqrt)) / dSq) + pc.getX(), (((-det * d.getX()) + (Math.abs(d.getY()) * discSqrt)) / dSq) + pc.getY());

        return intr.pushXY((det * d.getY() - sgn * d.getX() * discSqrt) / dSq + pc.getX(), (-det * d.getX() - Math.abs(d.getY()) * discSqrt) / dSq + pc.getY());
    }

    public static double getRatio(final double pos, final double boxPos, final double boxWidth) {
        double xDistance = pos - boxPos;
        int xSgn = sgn(xDistance);
        return xSgn * (Math.abs(xDistance) / boxWidth);
    }

    public boolean intersectLineRectange(double l0, double l1) {
        return false;
    }

    /**
     * Canvas arcTo's have a variable center, as points a, b and c form two lines from the same point at a tangent to the arc's cirlce.
     * This returns the arcTo arc start, center and end points.
     * @param p0
     * @param p1
     * @param r
     * @return
     */
    public static final Point2DArray getCanvasArcToPoints(final Point2D p0, final Point2D p1, final Point2D p2, final double r) {
        // see tangents drawn from same point to a circle
        // http://www.mathcaptain.com/geometry/tangent-of-a-circle.html
        final double a0 = getAngleBetweenTwoLines(p0, p1, p2) / 2;

        final double ln = getLengthFromASA(RADIANS_90 - a0, r, a0);

        Point2D dv = p1.sub(p0);

        Point2D dx = dv.unit();

        Point2D dl = dx.mul(ln);

        Point2D ps = p1.sub(dl);// ps is arc start point

        dv = p1.sub(p2);

        dx = dv.unit();

        dl = dx.mul(ln);

        Point2D pe = p1.sub(dl);// ep is arc end point

        // this gets the direction as a unit, from p1 to the center
        Point2D midPoint = new Point2D((ps.getX() + pe.getX()) / 2, (ps.getY() + pe.getY()) / 2);

        dx = midPoint.sub(p1).unit();

        final Point2D pc = p1.add(dx.mul(distance(r, ln)));

        return Point2DArray.fromArrayOfPoint2D(ps, pc, pe);
    }

    public static final Point2DArray getCardinalIntersects(final AbstractMultiPathPartShape<?> shape, Direction[] requestedCardinals) {
        final Point2DArray cardinals = getCardinals(shape.getBoundingBox(), requestedCardinals);
        final Set<Point2D>[] intersections = getCardinalIntersects(shape, cardinals);
        return removeInnerPoints(cardinals.get(0), intersections);
    }

    public static Set<Point2D>[] getCardinalIntersects(AbstractMultiPathPartShape<?> shape, Point2DArray cardinals) {
        @SuppressWarnings("unchecked")
        final Set<Point2D>[] intersections = new Set[cardinals.size()];

        final NFastArrayList<PathPartList> paths = shape.getActualPathPartListArray();

        final int size = paths.size();

        for (int i = 0; i < size; i++) {
            getPathPointsProjectionIntersects(paths.get(i), cardinals, intersections, true);
        }
        return intersections;
    }

    /**
     * Finds the intersection of the connector's end segment on a path.
     * @param connection
     * @param path
     * @param c
     * @param pointIndex
     * @return
     */
    public static Point2D getPathIntersect(WiresConnection connection, MultiPath path, Point2D c, int pointIndex) {
        Point2DArray plist = connection.getConnector().getLine().getPoint2DArray();

        Point2D p = plist.get(pointIndex).copy();

        Point2D offsetP = path.getComputedLocation();

        p.offset(-offsetP.getX(), -offsetP.getY());

        // p may be within the path boundary, so work of a vector that guarantees a point outside
        double width = path.getBoundingBox().getWidth();
        if (c.equals(p)) {
            // this happens with the magnet is over the center of the opposite shape
            // so either the shapes are horizontall or vertically aligned.
            // this means we can just take the original centre point for the project
            // without this the project throw an error as you cannot unit() something of length 0,0
            p.offset(offsetP.getX(), offsetP.getY());
        }
        try {
            p = getProjection(c,
                              p,
                              width);

            Set<Point2D>[] set = Geometry.getCardinalIntersects(path, Point2DArray.fromArrayOfPoint2D(c, p));
            Point2DArray points = Geometry.removeInnerPoints(c,
                                                             set);

            return (points.size() > 1) ? points.get(1) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Point2DArray getIntersectPolyLinePath(Point2DArray points, PathPartList path, boolean closed) {
        Point2DArray intersectPoints = null;
        if (null != path) {
            int size = closed ? points.size() : points.size() - 1;
            for (int i = 0; i < size; i++) {
                Point2D p1 = points.get(i);
                Point2D p2;
                if (closed) {
                    p2 = (i < size - 1) ? points.get(i + 1) : points.get(0);
                } else {
                    p2 = points.get(i + 1);
                }
                Point2DArray segmentIntersectPoints = getIntersectLineSegmentPath(p1, p2, path);
                if (segmentIntersectPoints != null) {
                    if (intersectPoints == null) {
                        intersectPoints = new Point2DArray();
                    }
                    for (Point2D p : segmentIntersectPoints.asArray()) {
                        intersectPoints.push(p);
                    }
                }
            }
        }
        return intersectPoints;
    }

    public static Point2DArray getIntersectLineSegmentPath(Point2D l0, Point2D l1, PathPartList path) {
        // the line is on the root container, it's points must be translated to be within the group of the path

        Point2DArray line = Point2DArray.fromArrayOfPoint2D(l0, l1);
        final Set<Point2D>[] intersections = new Set[line.size()]; // this is a line, there won't be more than one

        getPathPointsProjectionIntersects(path, line, intersections, false);

        Point2DArray intersectPoints = null;
        if (intersections != null && intersections[1] != null && !intersections[1].isEmpty()) {
            intersectPoints = new Point2DArray();
            for (Point2D p : intersections[1]) {
                intersectPoints.push(p);
            }
        }

        return intersectPoints;
    }

    public static Point2D getPathPointsProjectionIntersects(MultiPath path, Point2D center, Point2D point) {
        Set<Point2D>[] intersections = getCardinalIntersects(path, Point2DArray.fromArrayOfPoint2D(center, point));
        return (Point2D) intersections[0].toArray()[0];
    }

    public static void getPathPointsProjectionIntersects(PathPartList path, Point2DArray points, Set<Point2D>[] intersections, boolean addCenter) {
        Point2D center = points.get(0);
        Point2D pathStart = new Point2D(0, 0);
        Point2D segmentStart = pathStart;

        int i = PathPartList.skipRedundantLeadingMoveTo(path);

        // A set is used as vertex's may intersect, so the start/end of two liens will intersect
        for (; i < path.size(); i++) {
            PathPartEntryJSO entry = path.get(i);
            double[] entryPoints = entry.getPoints();

            switch (entry.getCommand()) {
                case PathPartEntryJSO.MOVETO_ABSOLUTE: {
                    entryPoints = entry.getPoints();
                    Point2D m = new Point2D(entryPoints[0], entryPoints[1]);
                    if (i == 0) {
                        // This position is needed, if we close the path.
                        pathStart = m;
                    }
                    segmentStart = m;
                    break;
                }
                case PathPartEntryJSO.LINETO_ABSOLUTE: {
                    entryPoints = entry.getPoints();
                    double x0 = entryPoints[0];
                    double y0 = entryPoints[1];
                    Point2D end = new Point2D(x0, y0);
                    for (int j = 1; j < points.size(); j++) {
                        Point2D point = points.get(j);
                        Point2D intersectPoint = Geometry.intersectLineLine(center, point, segmentStart, end);
                        if (intersectPoint != null) {
                            addIntersect(intersections, j, intersectPoint);
                        }
                    }
                    segmentStart = end;
                    break;
                }
                case PathPartEntryJSO.CLOSE_PATH_PART: {
                    double x0 = pathStart.getX();
                    double y0 = pathStart.getY();
                    Point2D end = new Point2D(x0, y0);
                    for (int j = 1; j < points.size(); j++) {
                        Point2D point = points.get(j);
                        Point2D intersectPoint = Geometry.intersectLineLine(center, point, segmentStart, end);
                        if (intersectPoint != null) {
                            addIntersect(intersections, j, intersectPoint);
                        }
                    }
                    segmentStart = end;
                    break;
                }
                case PathPartEntryJSO.CANVAS_ARCTO_ABSOLUTE: {
                    entryPoints = entry.getPoints();

                    double x0 = entryPoints[0];
                    double y0 = entryPoints[1];
                    Point2D p0 = new Point2D(x0, y0);

                    double x1 = entryPoints[2];
                    double y1 = entryPoints[3];
                    Point2D p1 = new Point2D(x1, y1);
                    Point2D end = p1;

                    double r = entryPoints[4];
                    for (int j = 1; j < points.size(); j++) {
                        final Point2D point = points.get(j);
                        final Point2DArray intersectPoints = Geometry.intersectLineArcTo(center, point, segmentStart, p0, p1, r);

                        if (intersectPoints.size() > 0) {
                            for (Point2D p : intersectPoints.asArray()) {
                                addIntersect(intersections, j, p);
                            }
                        }
                    }
                    segmentStart = end;
                    break;
                }
                case PathPartEntryJSO.BEZIER_CURVETO_ABSOLUTE: {
                    final double x0 = segmentStart.getX();
                    final double y0 = segmentStart.getY();

                    final double x1 = entryPoints[0];
                    final double y1 = entryPoints[1];

                    final double x2 = entryPoints[2];
                    final double y2 = entryPoints[3];

                    final double x3 = entryPoints[4];
                    final double y3 = entryPoints[5];

                    final double[] xvals = new double[]{x0, x1, x2, x3};
                    final double[] yvals = new double[]{y0, y1, y2, y3};

                    final Point2D end = new Point2D(x3, y3);

                    for (int j = 1; j < points.size(); j++) {
                        final Point2D cardinal = points.get(j);

                        final double[] lx = new double[]{center.getX(), cardinal.getX()};
                        final double[] ly = new double[]{center.getY(), cardinal.getY()};
                        final Point2DArray intersectPoints = Geometry.intersectLineCurve(xvals, yvals, lx, ly);

                        if (intersectPoints.size() > 0) {
                            for (final Point2D p : intersectPoints.asArray()) {
                                addIntersect(intersections, j, p);
                            }
                        }
                    }
                    segmentStart = end;
                    break;
                }
            }
        }
        if (addCenter) {
            addIntersect(intersections, 0, center);
        }
    }

    public static Point2DArray getCardinalIntersects(final PathPartList path, Direction[] requestedCardinals) {
        final Point2DArray cardinals = getCardinals(path.getBoundingBox(), requestedCardinals);

        @SuppressWarnings("unchecked")
        final Set<Point2D>[] intersections = new Set[cardinals.size()];// c is removed, so -1

        getPathPointsProjectionIntersects(path, cardinals, intersections, true);

        return removeInnerPoints(cardinals.get(0), intersections);
    }

    public static boolean isPointAnIntersectGiveArcBetweenTwoLines(Point2D l1a, Point2D l1b, Point2D p, Point2D l2a, double r) {
        Point2D dv = l1b.sub(l1a);
        Point2D dx = dv.unit();
        Point2D dy = dx.perpendicular();

        Point2D arcCenter = l1b.add(dy.mul(r));

        double angle = getAngleBetweenTwoLines(l1b, arcCenter, l2a);
        double l = getLengthFromASA(angle, r, RADIANS_90);
        double l4 = Math.sqrt(l * l - r * r);

        Point2D intersection = dx.mul(l4);
        return intersection.equals(p);
    }

    public static final Point2DArray removeInnerPoints(final Point2D c, final Set<Point2D>[] pointSet) {
        final Point2DArray points = new Point2DArray();

        int i = 0;

        for (Set<Point2D> set : pointSet) {
            double furthestDistance = -1;

            if (set != null && !set.isEmpty()) {
                for (Point2D p : set) {
                    double currentDistance = p.distance(c);

                    if (currentDistance > furthestDistance) {
                        furthestDistance = currentDistance;

                        points.set(i, p);
                    }
                }
                i++;
            }
        }
        return points;
    }

    public static final void addIntersect(final Set<Point2D>[] intersections, final int index, final Point2D point) {
        Set<Point2D> iset = intersections[index];

        if (iset == null) {
            iset = new HashSet<>();

            intersections[index] = iset;
        }
        iset.add(point);
    }

    /**
     * Returns cardinal points for a given bounding box
     * @param box the bounding box
     * @return [C, N, NE, E, SE, S, SW, W, NW]
     */
    public static final Point2DArray getCardinals(final BoundingBox box, Direction[] requestedCardinals) {
        Set<Direction> set = new HashSet<>(Arrays.asList(requestedCardinals));

        Point2DArray points = new Point2DArray();

        final Point2D c = findCenter(box);
        final Point2D n = new Point2D(c.getX(), box.getY());
        final Point2D e = new Point2D(box.getX() + box.getWidth(), c.getY());
        final Point2D s = new Point2D(c.getX(), box.getY() + box.getHeight());
        final Point2D w = new Point2D(box.getX(), c.getY());
        final Point2D sw = new Point2D(w.getX(), s.getY());
        final Point2D se = new Point2D(e.getX(), s.getY());
        final Point2D ne = new Point2D(e.getX(), n.getY());
        final Point2D nw = new Point2D(w.getX(), n.getY());

        points.push(c);

        if (set.contains(Direction.NORTH)) {
            points.push(n);
        }

        if (set.contains(Direction.NORTH_EAST)) {
            points.push(ne);
        }

        if (set.contains(Direction.EAST)) {
            points.push(e);
        }

        if (set.contains(Direction.SOUTH_EAST)) {
            points.push(se);
        }

        if (set.contains(Direction.SOUTH)) {
            points.push(s);
        }

        if (set.contains(Direction.SOUTH_WEST)) {
            points.push(sw);
        }

        if (set.contains(Direction.WEST)) {
            points.push(w);
        }

        if (set.contains(Direction.NORTH_WEST)) {
            points.push(nw);
        }

        return points;
    }

    public static final Direction getQuadrant(final Point2D c, final Point2D p1) {
        return getQuadrant(c.getX(), c.getY(), p1.getX(), p1.getY());
    }

    /**
     * Returns the NESW quadrant the point is in.  The delta from the center
     * NE x > 0, y < 0
     * SE x > 0, y >= 0
     * SW x <= 0, y >= 0
     * NW x <= 0, y < 0
     * @param cx
     * @param cy*
     * @param x0
     * @param y0
     * @return
     */
    public static final Direction getQuadrant(final double cx, double cy, final double x0, final double y0) {
        if (x0 > cx && y0 < cy) {
            return Direction.NORTH_EAST;
        }
        if (x0 > cx && y0 >= cy) {
            return Direction.SOUTH_EAST;
        }
        if (x0 <= cx && y0 >= cy) {
            return Direction.SOUTH_WEST;
        }
        // if( x0 <= c.getX() && y0 < c.getY() )
        return Direction.NORTH_WEST;
    }

    public static final IPrimitive setScaleToFit(final IPrimitive<?> prim, final double wide, final double high) {
        final Point2D scale = prim.getScale();

        final BoundingBox bbox = prim.getBoundingBox();

        if (null != scale) {
            final double sx = scale.getX();

            final double sy = scale.getY();

            if ((sx != 1) || (sy != 1)) {
                return setScaleToFit(prim, wide, high, new BoundingPoints(bbox).transform(new Transform().scaleWithXY(sx, sy)).getBoundingBox());
            }
        }
        return setScaleToFit(prim, wide, high, bbox);
    }

    public static final IPrimitive setScaleToFit(final IPrimitive<?> prim, final double wide, final double high, final BoundingBox bbox) {
        prim.setScale(wide / bbox.getWidth(), high / bbox.getHeight());

        return prim;
    }

    /**
     * Finds intersecting point from the center of a path
     * @param x
     * @param y
     * @param path
     * @return the path's intersection point, or null if there's no intersection point
     */
    public static Point2D findIntersection(int x, int y, MultiPath path) {
        Point2D pointerPosition = new Point2D(x, y);
        BoundingBox box = path.getBoundingBox();
        Point2D center = findCenter(box);

        // length just needs to ensure the c to xy is outside of the path
        double length = box.getWidth() + box.getHeight();

        Point2D projectionPoint = getProjection(center, pointerPosition, length);

        Point2DArray points = new Point2DArray();
        points.push(center);
        points.push(projectionPoint);

        Set<Point2D>[] intersects = Geometry.getCardinalIntersects(path, points);

        Point2D nearest = null;
        for (Set<Point2D> set : intersects) {
            double nearesstDistance = length;

            if (set != null && !set.isEmpty()) {
                for (Point2D p : set) {
                    double currentDistance = p.distance(pointerPosition);

                    if (currentDistance < nearesstDistance) {
                        nearesstDistance = currentDistance;
                        nearest = p;
                    }
                }
            }
        }
        return nearest;
    }

    public static double findAngle(final Point2D p0,
                                   final Point2D p1) {
        return getClockwiseAngleBetweenThreePoints(new Point2D(p0.getX() + p1.getX(), p0.getY()), p0, p1);
    }

    public static Point2D findCenter(final Point2D p0,
                                     final Point2D p1) {
        return Geometry.findCenter(BoundingBox.fromArrayOfPoint2D(p0, p1));
    }

    public static Point2D findCenter(BoundingBox box) {
        return new Point2D(box.getX() + box.getWidth() / 2, box.getY() + box.getHeight() / 2);
    }

    public static Point2D getProjection(Point2D center, Point2D intersection, double length) {
        if (intersection.equals(center)) {
            return new Point2D(0, 0);
        }

        Point2D unit = intersection.sub(center).unit();

        return center.add(unit.mul(length));
    }

    /**
     * Find the closest point on a given line defined by the points on the {@link Point2DArray},given a reference point x,y.
     * @param x reference point x
     * @param y reference point y
     * @param linePoints points of segments that compose the line
     * @return
     */
    public static Point2D findClosestPointOnLine(double x, double y, Point2DArray linePoints) {
        Double lowestDistance = null;
        Point2D nearstPoint = null;
        for (int i = 0; i < linePoints.size() - 1; i++) {
            Point2D start = linePoints.get(i);
            Point2D end = linePoints.get(i + 1);
            Point2D point = findClosestPointOnLine(x, y, start, end);
            if (point == null) {
                continue;
            }
            double distance = Geometry.distance(point.getX(), point.getY(), x, y);
            if (lowestDistance == null || lowestDistance > distance) {
                lowestDistance = distance;
                nearstPoint = point;
            }
        }
        return nearstPoint;
    }

    /**
     * Find the closest point on a given line defined by two the points, given a reference point (x,y).
     * @param x reference point x
     * @param y reference point y
     * @param linePoint1 points of segments that compose the line
     * @param linePoint2 an offset to disconsider points close to @param linePoints
     * @return
     */
    public static Point2D findClosestPointOnLine(double x, double y, Point2D linePoint1, Point2D linePoint2) {
        final double x0 = linePoint1.getX();
        final double y0 = linePoint1.getY();
        final double x1 = linePoint2.getX();
        final double y1 = linePoint2.getY();
        final double dx = x1 - x0;
        final double dy = y1 - y0;
        final double t = ((x - x0) * dx + (y - y0) * dy) / (dx * dx + dy * dy);
        final double lineX = lerp(x0, x1, t);
        final double lineY = lerp(y0, y1, t);
        final double maxX = Math.max(x0, x1);
        final double maxY = Math.max(y0, y1);
        final double minX = Math.min(x0, x1);
        final double minY = Math.min(y0, y1);

        //avoid to return a point out of boundary
        if ((lineX > maxX || lineX < minX || lineY > maxY || lineY < minY)) {
            return null;
        }
        return new Point2D(lineX, lineY);
    }

    private static double lerp(double a, double b, double x) {
        return (a + x * (b - a));
    }
}