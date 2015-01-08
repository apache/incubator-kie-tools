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
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * In Euclidean geometry, a regular polygon is a polygon that is equiangular (all angles are equal in measure) 
 * and equilateral (all sides have the same length).  All regular polygons fit perfectly inside a circle.
 */
public class RegularPolygon extends Shape<RegularPolygon>
{
    /**
     * Constructor. Creates an instance of a regular polygon.
     * 
     * @param sides number of sides
     * @param radius size of the encompassing circle
     */
    public RegularPolygon(final int sides, final double radius)
    {
        super(ShapeType.REGULAR_POLYGON);

        setRadius(radius).setSides(sides);
    }

    protected RegularPolygon(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.REGULAR_POLYGON, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final int s = getSides();

        final double r = getRadius();

        double minx = 0;

        double miny = 0;

        double maxx = 0;

        double maxy = 0;

        if ((s > 2) && (r > 0))
        {
            minx = maxx = 0;

            miny = maxy = 0 - r;

            for (int n = 1; n < s; n++)
            {
                double x = (r * Math.sin(n * 2 * Math.PI / s));

                double y = (-1 * r * Math.cos(n * 2 * Math.PI / s));

                minx = Math.min(minx, x);

                miny = Math.min(miny, y);

                maxx = Math.max(maxx, x);

                maxy = Math.max(maxy, y);
            }
        }
        return new BoundingBox(minx, miny, maxx, maxy);
    }

    /**
     * Draws this regular polygon
     * 
     * @context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final int s = attr.getSides();

        final double r = attr.getRadius();

        if ((s > 2) && (r > 0))
        {
            context.beginPath();

            context.moveTo(0, 0 - r);

            for (int n = 1; n < s; n++)
            {
                context.lineTo(r * Math.sin(n * 2 * Math.PI / s), -1 * r * Math.cos(n * 2 * Math.PI / s));
            }
            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Gets this regular polygon's encompassing circle's radius.
     * 
     * @return double
     */
    public double getRadius()
    {
        return getAttributes().getRadius();
    }

    /**
     * Sets the size of this regular polygon, expressed by the radius of the enclosing circle.
     * 
     * @param radius
     * @return this RegularPolygon
     */
    public RegularPolygon setRadius(final double radius)
    {
        getAttributes().setRadius(radius);

        return this;
    }

    /**
     * Gets the number of sides this regular polygon has.
     * 
     * @return int
     */
    public int getSides()
    {
        return getAttributes().getSides();
    }

    /**
     * Sets the number of sides this regular polygon has.
     * 
     * @param sides
     * @return this RegularPolygon
     */
    public RegularPolygon setSides(final int sides)
    {
        getAttributes().setSides(sides);

        return this;
    }

    @Override
    public IFactory<RegularPolygon> getFactory()
    {
        return new RegularPolygonFactory();
    }

    public static class RegularPolygonFactory extends ShapeFactory<RegularPolygon>
    {
        public RegularPolygonFactory()
        {
            super(ShapeType.REGULAR_POLYGON);

            addAttribute(Attribute.RADIUS, true);

            addAttribute(Attribute.SIDES, true);
        }

        @Override
        public RegularPolygon create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new RegularPolygon(node, ctx);
        }
    }
}
