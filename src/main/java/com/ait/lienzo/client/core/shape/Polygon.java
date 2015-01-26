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
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * A polygon is traditionally a plane figure that is bounded by a closed path, 
 * composed of a finite sequence of straight line segments.
 */
public class Polygon extends AbstractMultiPointShape<Polygon>
{
    /**
     * Constructor. Creates an instance of a polygon.
     * 
     * @param points a {@link Point2DArray} containing 3 or more points
     */
    public Polygon(final Point2DArray points)
    {
        super(ShapeType.POLYGON);

        setPoints(points);
    }

    public Polygon(final Point2D point, final Point2D... points)
    {
        this(new Point2DArray(point, points));
    }

    protected Polygon(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.POLYGON, node, ctx);
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
        final Point2DArray list = attr.getPoints();

        if ((null != list) && (list.size() > 2))
        {
            Point2D point = list.get(0);

            context.beginPath();

            context.moveTo(point.getX(), point.getY());

            final int size = list.size();

            for (int i = 1; i < size; i++)
            {
                point = list.get(i);

                context.lineTo(point.getX(), point.getY());
            }
            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Gets this polygon's points.
     * 
     * @return {@link Point2DArray}
     */
    public Point2DArray getPoints()
    {
        return getAttributes().getPoints();
    }

    /**
     * Sets this polygon's points.
     * 
     * @param points a {@link Point2DArray} of 3 or more points
     * @return this Polygon
     */
    public Polygon setPoints(final Point2DArray points)
    {
        getAttributes().setPoints(points);

        return this;
    }

    @Override
    public Polygon setPoint2DArray(Point2DArray points)
    {
        return setPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray()
    {
        return getPoints();
    }

    @Override
    public IFactory<Polygon> getFactory()
    {
        return new PolygonFactory();
    }

    public static class PolygonFactory extends ShapeFactory<Polygon>
    {
        public PolygonFactory()
        {
            super(ShapeType.POLYGON);

            addAttribute(Attribute.POINTS, true);
        }

        @Override
        public Polygon create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Polygon(node, ctx);
        }
    }
}
