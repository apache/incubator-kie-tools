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
 * A triangle is one of the basic shapes of geometry: a polygon with three corners or vertices and three sides 
 * or edges which are line segments.
 * In Euclidean geometry any three points, when non-collinear, determine a unique triangle and a unique plane 
 * (i.e. a two-dimensional Euclidean space). 
 */
public class Triangle extends Shape<Triangle>
{
    /**
     * Constructor. Creates an instance of a triangle.
     * 
     * @param 3 points {@link Point2D}
     */
    public Triangle(Point2D a, Point2D b, Point2D c)
    {
        super(ShapeType.TRIANGLE);

        setPoints(a, b, c);
    }

    protected Triangle(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.TRIANGLE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return getPoints().getBoundingBox();
    }

    /**
     * Draws this polygon.
     * 
     * @param context
     */
    @Override
    public boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        Point2DArray list = getPoints();

        if ((null != list) && (list.size() > 2))
        {
            Point2D point0 = list.getPoint(0);

            Point2D point1 = list.getPoint(1);

            Point2D point2 = list.getPoint(2);

            context.beginPath();

            context.moveTo(point0.getX(), point0.getY());

            context.lineTo(point1.getX(), point1.getY());

            context.lineTo(point2.getX(), point2.getY());

            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Gets this triangles points.
     * 
     * @return {@link Point2DArray}
     */
    public Point2DArray getPoints()
    {
        return getAttributes().getPoints();
    }

    /**
     * Sets this triangles points.
     * 
     * @param 3 points {@link Point2D}
     * @return this Triangle
     */
    public Triangle setPoints(Point2D a, Point2D b, Point2D c)
    {
        getAttributes().setPoints(new Point2DArray(a, b, c));

        return this;
    }

    @Override
    public IFactory<Triangle> getFactory()
    {
        return new TriangleFactory();
    }

    // TODO - add triangle math methods, inner angles, etc

    public static class TriangleFactory extends ShapeFactory<Triangle>
    {
        public TriangleFactory()
        {
            super(ShapeType.TRIANGLE);

            addAttribute(Attribute.POINTS, true);
        }

        @Override
        public Triangle create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new Triangle(node, ctx);
        }
    }
}
