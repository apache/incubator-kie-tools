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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Curves;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * Quadratic curves, a type of Bezier curve, are defined by a context point, a control point, and an ending point.
 */
public class QuadraticCurve extends AbstractMultiPointShape<QuadraticCurve>
{
    /**
     * Constructor. Creates an instance a quadratic curve.
     * 
     * @param sx context point X coordinate
     * @param sy context point Y coordinate
     * @param cx control point X coordinate
     * @param cy control point Y coordinate
     * @param ex end point X coordinate
     * @param ey end point Y coordinate
     */
    public QuadraticCurve(final double sx, final double sy, final double cx, final double cy, final double ex, final double ey)
    {
        this(new Point2D(sx, sy), new Point2D(cx, cy), new Point2D(ex, ey));
    }

    public QuadraticCurve(final double cx, final double cy, final double ex, final double ey)
    {
        this(0, 0, cx, cy, ex, ey);
    }

    public QuadraticCurve(final Point2D sp, final Point2D cp, final Point2D ep)
    {
        super(ShapeType.QUADRATIC_CURVE);

        setControlPoints(new Point2DArray(sp, cp, ep));
    }

    public QuadraticCurve(final Point2D cp, final Point2D ep)
    {
        this(new Point2D(0, 0), cp, ep);
    }

    protected QuadraticCurve(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.QUADRATIC_CURVE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final BoundingBox bbox = Curves.getBoundingBox(this);

        if (null != bbox)
        {
            return bbox;
        }
        return new BoundingBox(0, 0, 0, 0);
    }

    /**
     * Draws this quadratic curve
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final Point2DArray points = attr.getControlPoints();

        if ((points != null) && (points.size() == 3))
        {
            context.beginPath();

            final Point2D p0 = points.get(0);

            final Point2D p1 = points.get(1);

            final Point2D p2 = points.get(2);

            context.moveTo(p0.getX(), p0.getY());

            context.quadraticCurveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY());

            return true;
        }
        return false;
    }

    /**
     * Gets all points, which includes the context, control, and end point.
     * 
     * @return {@link Point2DArray}
     */
    public Point2DArray getControlPoints()
    {
        return getAttributes().getControlPoints();
    }

    /**
     * Sets the points for this quadratic curve.  The argument, points, must be a 3-element
     * {@link Point2DArray} containing: context, control, and end point
     * 
     * @param points
     * @return this QuadraticCurve
     */
    public QuadraticCurve setControlPoints(final Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        return this;
    }

    @Override
    public QuadraticCurve setPoint2DArray(Point2DArray points)
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
    public IFactory<QuadraticCurve> getFactory()
    {
        return new QuadraticCurveFactory();
    }

    public static class QuadraticCurveFactory extends ShapeFactory<QuadraticCurve>
    {
        public QuadraticCurveFactory()
        {
            super(ShapeType.QUADRATIC_CURVE);

            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public QuadraticCurve create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new QuadraticCurve(node, ctx);
        }
    }
}
