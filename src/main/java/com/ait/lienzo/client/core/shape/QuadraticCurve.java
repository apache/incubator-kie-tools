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
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * Quadratic curves, a type of Bezier curve, are defined by a context point, a control point, and an ending point.
 */
public class QuadraticCurve extends Shape<QuadraticCurve>
{
    /**
     * Constructor. Creates an instance a quadratic curve.
     * 
     * @param x context point X coordinate
     * @param y context point Y coordinate
     * @param controlX control point X coordinate
     * @param controlY control point Y coordinate
     * @param endX end point X coordinate
     * @param endY end point Y coordinate
     */
    public QuadraticCurve(double x, double y, double controlX, double controlY, double endX, double endY)
    {
        super(ShapeType.QUADRATIC_CURVE);

        setControlPoints(new Point2DArray(new Point2D(x, y), new Point2D(controlX, controlY), new Point2D(endX, endY)));
    }

    public QuadraticCurve(double controlX, double controlY, double endX, double endY)
    {
        this(0, 0, controlX, controlY, endX, endY);
    }

    protected QuadraticCurve(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.QUADRATIC_CURVE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return getControlPoints().getBoundingBox();
    }

    /**
     * Draws this quadratic curve
     * 
     * @param context
     */
    @Override
    public boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        Point2DArray points = getControlPoints();

        if ((points != null) && (points.size() == 3))
        {
            context.beginPath();

            Point2D p0 = points.getPoint(0);

            Point2D p1 = points.getPoint(1);

            Point2D p2 = points.getPoint(2);

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
    public QuadraticCurve setControlPoints(Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        return this;
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
        public QuadraticCurve create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new QuadraticCurve(node, ctx);
        }
    }
}
