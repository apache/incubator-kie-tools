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
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * Bezier curves are defined with two anchor points and two control points.
 * This class represents a cubic Bezier curve.
 */
public class BezierCurve extends AbstractMultiPointShape<BezierCurve>
{
    /**
     * Constructor. Creates an instance of a cubic bezier curve.
     * 
     * @param sx x value for the first anchor point
     * @param sy y value for the first anchor point
     * @param c1x x value for the first control point
     * @param c1y y value for the first control point
     * @param c2x x value for the second control point
     * @param c2y y value for the second control point
     * @param ex x value for the second anchor point
     * @param ey y value for the second anchor point
     */
    public BezierCurve(final double sx, final double sy, final double c1x, final double c1y, final double c2x, final double c2y, final double ex, final double ey)
    {
        this(new Point2D(sx, sy), new Point2D(c1x, c1y), new Point2D(c2x, c2y), new Point2D(ex, ey));
    }

    public BezierCurve(final double c1x, final double c1y, final double c2x, final double c2y, final double ex, final double ey)
    {
        this(0, 0, c1x, c1y, c2x, c2y, ex, ey);
    }

    public BezierCurve(final Point2D sp, final Point2D c1, final Point2D c2, final Point2D ep)
    {
        super(ShapeType.BEZIER_CURVE);

        setControlPoints(new Point2DArray(sp, c1, c2, ep));
    }

    public BezierCurve(final Point2D c1, final Point2D c2, final Point2D ep)
    {
        this(new Point2D(0, 0), c1, c2, ep);
    }

    protected BezierCurve(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.BEZIER_CURVE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        Point2DArray p = getPoint2DArray();
        final double x0 = p.get(0).getX();
        final double y0 = p.get(0).getY();

        final double x1 = p.get(1).getX();
        final double y1 = p.get(1).getY();

        final double x2 = p.get(2).getX();
        final double y2 = p.get(2).getY();

        final double x3 = p.get(3).getX();
        final double y3 = p.get(3).getY();

        final double[] xvals = new double[]{x0, x1, x2, x3};
        final double[] yvals = new double[]{y0, y1, y2, y3};

        final BoundingBox bbox = Geometry.getBoundingBoxOfCubicCurve(xvals, yvals);

        if (null != bbox)
        {
            return bbox;
        }
        return new BoundingBox(0, 0, 0, 0);
    }

    /**
     * Draws this Bezier Curve.
     * 
     * @param context the {@link Context2D} used to draw this bezier curve.
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final Point2DArray points = attr.getControlPoints();

        if ((points != null) && (points.size() == 4))
        {
            context.beginPath();

            final Point2D p0 = points.get(0);

            final Point2D p1 = points.get(1);

            final Point2D p2 = points.get(2);

            final Point2D p3 = points.get(3);

            context.moveTo(p0.getX(), p0.getY());

            context.bezierCurveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());

            return true;
        }
        return false;
    }

    /**
     * Gets this curve's control points.
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
     *            A {@link Point2DArray} containing the control points in the following order:
     *            first anchor, first control point, second control point, second anchor
     *       
     * @return this BezierCurve
     */
    public BezierCurve setControlPoints(final Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        return this;
    }

    @Override
    public BezierCurve setPoint2DArray(Point2DArray points)
    {
        return setControlPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray()
    {
        return getControlPoints();
    }

    @Override
    public boolean isControlPointShape()
    {
        return true;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return asAttributes(Attribute.CONTROL_POINTS);
    }

    public static class BezierCurveFactory extends ShapeFactory<BezierCurve>
    {
        public BezierCurveFactory()
        {
            super(ShapeType.BEZIER_CURVE);

            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public BezierCurve create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new BezierCurve(node, ctx);
        }
    }
}
