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

package com.ait.lienzo.client.core.shape;

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.google.gwt.json.client.JSONObject;

public class Spline extends AbstractMultiPointShape<Spline>
{
    private boolean            m_fill = false;

    private final PathPartList m_list = new PathPartList();

    /**
     * Constructor. Creates an instance of a spline.
     */
    public Spline(final Point2DArray points)
    {
        super(ShapeType.SPLINE);

        setControlPoints(points);
    }

    protected Spline(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.SPLINE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        if (m_list.size() < 1)
        {
            parse(getAttributes());
        }
        return m_list.getBoundingBox();
    }

    /**
    * Draws this Spline
    * 
    * @param context the {@link Context2D} used to draw this spline.
    */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        if (m_list.size() < 1)
        {
            parse(attr);
        }
        if (m_list.size() < 1)
        {
            return false;
        }
        m_fill = context.path(m_list);

        return true;
    }

    @Override
    protected boolean fill(Context2D context, Attributes attr, double alpha)
    {
        if (m_fill)
        {
            return super.fill(context, attr, alpha);
        }
        return false;
    }

    private final void parse(final Attributes attr)
    {
        final PathPoint[] points = getPathPoints(attr.getControlPoints());

        final int size = points.length;

        if (size < 3)
        {
            if (size > 1)
            {
                m_list.M(points[0].x, points[0].y).L(points[1].x, points[1].y);
            }
            return;
        }
        final double curveFactor = attr.getCurveFactor();

        final double angleFactor = attr.getAngleFactor();

        boolean closed = false;

        int begindex = 1;

        int endindex = size - 1;

        if ((points[0].x == points[size - 1].x) && (points[0].y == points[size - 1].y))
        {
            begindex = 0;

            endindex = size;

            closed = true;
        }
        else
        {
            closed = false;
        }
        final NFastArrayList<PathPoint[]> carray = new NFastArrayList<PathPoint[]>();

        for (int i = begindex; i < endindex; i++)
        {
            final PathPoint p0 = ((i - 1) < 0) ? points[size - 2] : points[i - 1];

            final PathPoint p1 = points[i];

            final PathPoint p2 = ((i + 1) == size) ? points[1] : points[i + 1];

            final double a = Math.max(PathPoint.distance(p0, p1), 0.001);

            final double b = Math.max(PathPoint.distance(p1, p2), 0.001);

            final PathPoint apt = new PathPoint(p0.x - p1.x, p0.y - p1.y);

            final PathPoint bpt = new PathPoint(p1.x, p1.y);

            final PathPoint cpt = new PathPoint(p2.x - p1.x, p2.y - p1.y);

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

            final double ax = bpt.x - apt.x;

            final double ay = bpt.y - apt.y;

            final double bx = bpt.x - cpt.x;

            final double by = bpt.y - cpt.y;

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
            double cdist = Math.min(a, b) * curveFactor;

            if (angleFactor != 0)
            {
                final double c = Math.max(PathPoint.distance(p0, p2), 0.001);

                cdist *= ((1 - angleFactor) + angleFactor * (Math.acos(Math.min(Math.max((b * b + a * a - c * c) / (2 * b * a), -1), 1)) / Math.PI));
            }
            final double cangl = Math.atan2(ry, rx) + Math.PI / 2;

            final PathPoint cp2 = PathPoint.polar(cdist, cangl);

            final PathPoint cp1 = PathPoint.polar(cdist, cangl + Math.PI);

            cp1.offset(p1.x, p1.y);

            cp2.offset(p1.x, p1.y);

            if (PathPoint.distance(cp2, p2) > PathPoint.distance(cp1, p2))
            {
                carray.set(i, PathPoint.toArray(cp2, cp1));
            }
            else
            {
                carray.set(i, PathPoint.toArray(cp1, cp2));
            }
        }
        final boolean lineFlatten = attr.getLineFlatten();

        m_list.M(points[0].x, points[0].y);

        if (begindex == 1)
        {
            final PathPoint point = carray.get(1)[0];

            m_list.Q(point.x, point.y, points[1].x, points[1].y);
        }
        int i;

        for (i = begindex; i < (endindex - 1); i++)
        {
            boolean line = lineFlatten && ((i > 0 && Math.atan2(points[i].y - points[i - 1].y, points[i].x - points[i - 1].x) == Math.atan2(points[i + 1].y - points[i].y, points[i + 1].x - points[i].x)) || (i < size - 2 && Math.atan2(points[i + 2].y - points[i + 1].y, points[i + 2].x - points[i + 1].x) == Math.atan2(points[i + 1].y - points[i].y, points[i + 1].x - points[i].x)));

            if (line)
            {
                m_list.L(points[i + 1].x, points[i + 1].y);
            }
            else
            {
                final PathPoint p1 = carray.get(i)[1];

                final PathPoint p2 = carray.get(i + 1)[0];

                m_list.C(p1.x, p1.y, p2.x, p2.y, points[i + 1].x, points[i + 1].y);
            }
        }
        if (endindex == (size - 1))
        {
            final PathPoint point = carray.get(i)[1];

            m_list.Q(point.x, point.y, points[i + 1].x, points[i + 1].y);
        }
        if (closed)
        {
            m_list.Z();
        }
    }

    private final static PathPoint[] getPathPoints(final Point2DArray array)
    {
        if ((null == array) || (array.size() < 2))
        {
            return new PathPoint[0];
        }
        final Point2DArray unique = array.noAdjacentPoints();

        final int size = unique.size();

        if (size < 2)
        {
            return new PathPoint[0];
        }
        final PathPoint[] points = new PathPoint[size];

        for (int i = 0; i < size; i++)
        {
            final Point2D point = unique.get(i);

            points[i] = new PathPoint(point.getX(), point.getY());
        }
        return points;
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
    public Spline setControlPoints(final Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        return refresh();
    }

    @Override
    public Spline setPoint2DArray(Point2DArray points)
    {
        return setControlPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray()
    {
        return getControlPoints();
    }

    public double getCurveFactor()
    {
        return getAttributes().getCurveFactor();
    }

    public Spline setCurveFactor(final double factor)
    {
        getAttributes().setCurveFactor(factor);

        return refresh();
    }

    public double getAngleFactor()
    {
        return getAttributes().getAngleFactor();
    }

    public Spline setAngleFactor(final double factor)
    {
        getAttributes().setAngleFactor(factor);

        return refresh();
    }

    public boolean getLineFlatten()
    {
        return getAttributes().getLineFlatten();
    }

    public Spline setLineFlatten(final boolean flat)
    {
        getAttributes().setLineFlatten(flat);

        return refresh();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return asAttributes(Attribute.CONTROL_POINTS, Attribute.CURVE_FACTOR, Attribute.ANGLE_FACTOR, Attribute.LINE_FLATTEN);
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
        public Spline create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Spline(node, ctx);
        }
    }

    private static final class PathPoint
    {
        public double x;

        public double y;

        PathPoint(double x, double y)
        {
            this.x = x;

            this.y = y;
        }

        final void normalize(final double length)
        {
            if (((x == 0) && (y == 0)) || (length == 0))
            {
                return;
            }
            final double scale = length / Math.sqrt((x * x) + (y * y));

            x *= scale;

            y *= scale;
        }

        final void offset(final double dx, final double dy)
        {
            x += dx;

            y += dy;
        }

        protected static final double distance(final PathPoint a, final PathPoint b)
        {
            final double dx = b.x - a.x;

            final double dy = b.y - a.y;

            return Math.sqrt((dx * dx) + (dy * dy));
        }

        static final PathPoint polar(final double length, final double angle)
        {
            return new PathPoint(length * Math.cos(angle), length * Math.sin(angle));
        }

        static final PathPoint[] toArray(final PathPoint... points)
        {
            return points;
        }
    }
}
