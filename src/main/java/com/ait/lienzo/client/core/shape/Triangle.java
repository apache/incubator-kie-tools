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
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * A triangle is one of the basic shapes of geometry: a polygon with three corners or vertices and three sides 
 * or edges which are line segments.
 * In Euclidean geometry any three points, when non-collinear, determine a unique triangle and a unique plane 
 * (i.e. a two-dimensional Euclidean space). 
 */
public class Triangle extends AbstractMultiPointShape<Triangle>
{
    private final PathPartList m_list = new PathPartList();

    /**
     * Constructor. Creates an instance of a triangle.
     * 
     * @param 3 points {@link Point2D}
     */
    public Triangle(final Point2D a, final Point2D b, final Point2D c)
    {
        super(ShapeType.TRIANGLE);

        setPoints(a, b, c);
    }

    public Triangle(final Point2D a, final Point2D b, final Point2D c, final double corner)
    {
        this(a, b, c);

        setCornerRadius(corner);
    }

    protected Triangle(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.TRIANGLE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(getPoints());
    }

    /**
     * Draws this polygon.
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        if (m_list.size() < 1)
        {
            if (false == parse(attr))
            {
                return false;
            }
        }
        if (m_list.size() < 1)
        {
            return false;
        }
        context.path(m_list);

        return true;
    }

    private boolean parse(final Attributes attr)
    {
        final Point2DArray list = attr.getPoints().noAdjacentPoints();

        if ((null != list) && (list.size() > 2))
        {
            final Point2D p0 = list.get(0);

            m_list.M(p0);

            final double corner = getCornerRadius();

            if (corner <= 0)
            {
                m_list.L(list.get(1));

                m_list.L(list.get(2));

                m_list.Z();
            }
            else
            {
                Geometry.drawArcJoinedLines(m_list, list.push(p0), corner);
            }
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
    public Triangle setPoints(final Point2D a, final Point2D b, final Point2D c)
    {
        return setPoint2DArray(new Point2DArray(a, b, c));
    }

    @Override
    public Triangle setPoint2DArray(final Point2DArray points)
    {
        while (points.size() > 3)
        {
            points.pop();
        }
        getAttributes().setPoints(points);

        return refresh();
    }

    @Override
    public Point2DArray getPoint2DArray()
    {
        return getPoints();
    }

    public double getCornerRadius()
    {
        return getAttributes().getCornerRadius();
    }

    public Triangle setCornerRadius(final double radius)
    {
        getAttributes().setCornerRadius(radius);

        return refresh();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return asAttributes(Attribute.POINTS, Attribute.CORNER_RADIUS);
    }

    public static class TriangleFactory extends ShapeFactory<Triangle>
    {
        public TriangleFactory()
        {
            super(ShapeType.TRIANGLE);

            addAttribute(Attribute.POINTS, true);

            addAttribute(Attribute.CORNER_RADIUS);
        }

        @Override
        public Triangle create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Triangle(node, ctx);
        }
    }
}
