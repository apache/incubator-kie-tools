/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class Spline extends Shape<Spline>
{
    private int                         m_begindex = 0;

    private int                         m_endindex = 0;

    private boolean                     m_closed   = false;

    private PathPoint[]                 m_points   = null;

    private NFastArrayList<PathPoint[]> m_carray   = null;

    /**
     * Constructor. Creates an instance of a spline.
     */
    public Spline(Point2DArray points)
    {
        super(ShapeType.SPLINE);

        setControlPoints(points);
    }

    protected Spline(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.SPLINE, node, ctx);
    }

    /**
    * Draws this Spline
    * 
    * @param context the {@link Context2D} used to draw this spline.
    */
    @Override
    public boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        if (null == m_points)
        {
            m_points = convertToPathPoints(getControlPoints());
        }
        if (m_points.length < 3)
        {
            if (m_points.length > 1)
            {
                context.beginPath();

                context.moveTo(m_points[0].x, m_points[0].y);

                context.lineTo(m_points[1].x, m_points[1].y);
            }
            return true;
        }
        if (null == m_carray)
        {
            calculateControlPoints();
        }
        boolean lineFlatten = getLineFlatten();

        context.beginPath();

        context.moveTo(m_points[0].x, m_points[0].y);

        if (m_begindex == 1)
        {
            PathPoint point = m_carray.get(1)[0];

            context.quadraticCurveTo(point.x, point.y, m_points[1].x, m_points[1].y);
        }
        int i;

        for (i = m_begindex; i < (m_endindex - 1); i++)
        {
            boolean line = lineFlatten && ((i > 0 && Math.atan2(m_points[i].y - m_points[i - 1].y, m_points[i].x - m_points[i - 1].x) == Math.atan2(m_points[i + 1].y - m_points[i].y, m_points[i + 1].x - m_points[i].x)) || (i < m_points.length - 2 && Math.atan2(m_points[i + 2].y - m_points[i + 1].y, m_points[i + 2].x - m_points[i + 1].x) == Math.atan2(m_points[i + 1].y - m_points[i].y, m_points[i + 1].x - m_points[i].x)));

            if (line)
            {
                context.lineTo(m_points[i + 1].x, m_points[i + 1].y);
            }
            else
            {
                PathPoint p1 = m_carray.get(i)[1];

                PathPoint p2 = m_carray.get(i + 1)[0];

                context.bezierCurveTo(p1.x, p1.y, p2.x, p2.y, m_points[i + 1].x, m_points[i + 1].y);
            }
        }
        if (m_endindex == (m_points.length - 1))
        {
            PathPoint point = m_carray.get(i)[1];

            context.quadraticCurveTo(point.x, point.y, m_points[i + 1].x, m_points[i + 1].y);
        }
        return true;
    }

    @Override
    public void fill(Context2D context, Attributes attr, double alpha)
    {
        if (m_closed)
        {
            super.fill(context, attr, alpha);
        }
    }

    private final void calculateControlPoints()
    {
        double curveFactor = getCurveFactor();

        double angleFactor = getAngleFactor();

        m_begindex = 1;

        m_endindex = m_points.length - 1;

        if ((m_points[0].x == m_points[m_points.length - 1].x) && (m_points[0].y == m_points[m_points.length - 1].y))
        {
            m_begindex = 0;

            m_endindex = m_points.length;

            m_closed = true;
        }
        else
        {
            m_closed = false;
        }
        m_carray = new NFastArrayList<PathPoint[]>();

        for (int i = m_begindex; i < m_endindex; i++)
        {
            PathPoint p0 = ((i - 1) < 0) ? m_points[m_points.length - 2] : m_points[i - 1];

            PathPoint p1 = m_points[i];

            PathPoint p2 = ((i + 1) == m_points.length) ? m_points[1] : m_points[i + 1];

            double a = PathPoint.distance(p0, p1);

            if (a < 0.001)
            {
                a = 0.001;
            }
            double b = PathPoint.distance(p1, p2);

            if (b < 0.001)
            {
                b = 0.001;
            }
            double c = PathPoint.distance(p0, p2);

            if (c < 0.001)
            {
                c = 0.001;
            }
            double cos = (b * b + a * a - c * c) / (2 * b * a);

            if (cos < -1)
            {
                cos = -1;
            }
            else if (cos > 1)
            {
                cos = 1;
            }
            double aco = Math.acos(cos);

            PathPoint apt = new PathPoint(p0.x - p1.x, p0.y - p1.y);

            PathPoint bpt = new PathPoint(p1.x, p1.y);

            PathPoint cpt = new PathPoint(p2.x - p1.x, p2.y - p1.y);

            if (a > b)
            {
                apt.normalize(b);
            }
            else if (b > a)
            {
                cpt.normalize(a);
            }
            apt.offset(p1.x, p1.y);

            cpt.offset(p1.x, p1.y);

            double ax = bpt.x - apt.x;

            double ay = bpt.y - apt.y;

            double bx = bpt.x - cpt.x;

            double by = bpt.y - cpt.y;

            double rx = ax + bx;

            double ry = ay + by;

            if ((rx == 0) && (ry == 0))
            {
                rx = -bx;

                ry = by;
            }
            if ((ay == 0) && (by == 0))
            {
                rx = 0;

                ry = 1;
            }
            else if ((ax == 0) && (bx == 0))
            {
                rx = 1;

                ry = 0;
            }
            double theta = Math.atan2(ry, rx);

            double cdist = Math.min(a, b) * curveFactor;

            double scale = aco / Math.PI;

            cdist *= ((1 - angleFactor) + angleFactor * scale);

            double cangl = theta + Math.PI / 2;

            PathPoint cp2 = PathPoint.polar(cdist, cangl);

            PathPoint cp1 = PathPoint.polar(cdist, cangl + Math.PI);

            cp1.offset(p1.x, p1.y);

            cp2.offset(p1.x, p1.y);

            if (PathPoint.distance(cp2, p2) > PathPoint.distance(cp1, p2))
            {
                m_carray.add(i, PathPoint.toArray(cp2, cp1));
            }
            else
            {
                m_carray.add(i, PathPoint.toArray(cp1, cp2));
            }
        }
    }

    /**
     * Gets this spline's control points.
     * 
     * @return {@link Point2DArray}
     */
    public Point2DArray getControlPoints()
    {
        return getAttributes().getControlPoints();
    }

    /**
     * Sets the control points for this curve.
     * 
     * @param points
     *            A {@link Point2DArray} containing the control points
     *       
     * @return this Spline
     */
    public Spline setControlPoints(Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        m_points = null;

        m_carray = null;

        return this;
    }

    public double getCurveFactor()
    {
        return getAttributes().getCurveFactor();
    }

    public Spline setCurveFactor(double factor)
    {
        getAttributes().setCurveFactor(factor);

        m_points = null;

        m_carray = null;

        return this;
    }

    public double getAngleFactor()
    {
        return getAttributes().getAngleFactor();
    }

    public Spline setAngleFactor(double factor)
    {
        getAttributes().setAngleFactor(factor);

        m_points = null;

        m_carray = null;

        return this;
    }

    public boolean getLineFlatten()
    {
        return getAttributes().getLineFlatten();
    }

    public Spline setLineFlatten(boolean flat)
    {
        getAttributes().setLineFlatten(flat);

        return this;
    }

    @Override
    public IFactory<Spline> getFactory()
    {
        return new SplineFactory();
    }

    public static class SplineFactory extends ShapeFactory<Spline>
    {
        public SplineFactory()
        {
            super(ShapeType.SPLINE);

            addAttribute(Attribute.CURVE_FACTOR);

            addAttribute(Attribute.ANGLE_FACTOR);

            addAttribute(Attribute.LINE_FLATTEN);

            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public Spline create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new Spline(node, ctx);
        }
    }

    private final PathPoint[] convertToPathPoints(Point2DArray array)
    {
        if ((null == array) || (array.getLength() == 0))
        {
            return new PathPoint[0];
        }
        int size = 0;

        int leng = array.getLength();

        PathPoint[] points = new PathPoint[leng];

        for (int i = 0; i < leng; i++)
        {
            Point2D point = array.getPoint(i);

            double x = point.getX();

            double y = point.getY();

            if (size > 0)
            {
                if (((points[size - 1].x == x) && (points[size - 1].y == y)))
                {
                    continue;
                }
            }
            points[size] = new PathPoint(x, y);

            size++;
        }
        if (size == leng)
        {
            return points;
        }
        PathPoint[] nodups = new PathPoint[size];

        for (int i = 0; i < size; i++)
        {
            nodups[i] = points[i];
        }
        return nodups;
    }

    private static final class PathPoint
    {
        public double x;

        public double y;

        public PathPoint(double x, double y)
        {
            this.x = x;

            this.y = y;
        }

        public final void normalize(double length)
        {
            if (((x == 0) && (y == 0)) || (length == 0))
            {
                return;
            }
            double angle = Math.atan2(y, x);

            x = Math.cos(angle) * length;

            y = Math.sin(angle) * length;
        }

        public final void offset(double dx, double dy)
        {
            x += dx;

            y += dy;
        }

        public static final double distance(PathPoint a, PathPoint b)
        {
            double dx = b.x - a.x;

            double dy = b.y - a.y;

            return Math.sqrt((dx * dx) + (dy * dy));
        }

        public static final PathPoint polar(double length, double angle)
        {
            return new PathPoint(length * Math.sin(angle), length * Math.cos(angle));
        }

        public static final PathPoint[] toArray(PathPoint... points)
        {
            return points;
        }
    }
}
