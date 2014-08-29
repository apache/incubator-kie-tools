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
 * Bezier curves are defined with two anchor points and two control points.
 * This class represents a cubic Bezier curve.
 */
public class BezierCurve extends Shape<BezierCurve>
{
    /**
     * Constructor. Creates an instance of a cubic bezier curve.
     * 
     * @param x x value for the first anchor point
     * @param y y value for the first anchor point
     * @param controlX1 x value for the first control point
     * @param controlY1 y value for the first control point
     * @param controlX2 x value for the second control point
     * @param controlY2 y value for the second control point
     * @param endX x value for the second anchor point
     * @param endY y value for the second anchor point
     */
    public BezierCurve(double x, double y, double controlX1, double controlY1, double controlX2, double controlY2, double endX, double endY)
    {
        super(ShapeType.BEZIER_CURVE);

        setControlPoints(new Point2DArray(new Point2D(x, y), new Point2D(controlX1, controlY1), new Point2D(controlX2, controlY2), new Point2D(endX, endY)));
    }

    public BezierCurve(double controlX1, double controlY1, double controlX2, double controlY2, double endX, double endY)
    {
        this(0, 0, controlX1, controlY1, controlX2, controlY2, endX, endY);
    }

    protected BezierCurve(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.BEZIER_CURVE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return getControlPoints().getBoundingBox();
    }

    /**
     * Draws this Bezier Curve.
     * 
     * @param context the {@link Context2D} used to draw this bezier curve.
     */
    @Override
    public boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        Point2DArray points = getControlPoints();

        if ((points != null) && (points.size() == 4))
        {
            context.beginPath();

            Point2D p0 = points.getPoint(0);

            Point2D p1 = points.getPoint(1);

            Point2D p2 = points.getPoint(2);

            Point2D p3 = points.getPoint(3);

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
    public BezierCurve setControlPoints(Point2DArray points)
    {
        getAttributes().setControlPoints(points);

        return this;
    }

    @Override
    public IFactory<BezierCurve> getFactory()
    {
        return new BezierCurveFactory();
    }

    public static class BezierCurveFactory extends ShapeFactory<BezierCurve>
    {
        public BezierCurveFactory()
        {
            super(ShapeType.BEZIER_CURVE);

            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public BezierCurve create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new BezierCurve(node, ctx);
        }
    }
}
