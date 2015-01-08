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
 * Star is defined by an inner radius, an outer radius and the number of points.
 * The center points is at (0,0) unless additional attributes are set.
 */
public class Star extends Shape<Star>
{
    /**
     * Constructor. Creates an instance of a star.  Visually, there is an enclosing
     * circle which all the tips of the star touch, and an inner circle where all the
     * vertices of the star's arms touch.  The distance between the inner and the outer
     * circle define how long the star's arms are.
     * 
     * @param points number of points in this star.
     * @param innerRadius radius of the inner circle.
     * @param outerRadius radius of the enclosing circle.
     */
    public Star(final int points, final double innerRadius, final double outerRadius)
    {
        super(ShapeType.STAR);

        setStarPoints(points).setInnerRadius(innerRadius).setOuterRadius(outerRadius);
    }

    protected Star(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.STAR, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final int s = getStarPoints();

        final double ir = getInnerRadius();

        final double or = getOuterRadius();

        double minx = 0;

        double miny = 0;

        double maxx = 0;

        double maxy = 0;

        if ((s > 4) && (ir > 0) && (or > 0) && (or > ir))
        {
            minx = maxx = 0;

            miny = maxy = 0 - or;

            for (int n = 1; n < s * 2; n++)
            {
                double radius = n % 2 == 0 ? or : ir;

                double x = (radius * Math.sin(n * Math.PI / s));

                double y = (-1 * radius * Math.cos(n * Math.PI / s));

                minx = Math.min(minx, x);

                miny = Math.min(miny, y);

                maxx = Math.max(maxx, x);

                maxy = Math.max(maxy, y);
            }
        }
        return new BoundingBox(minx, miny, maxx, maxy);
    }

    /**
     * Draws this star.
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final int s = attr.getStarPoints();

        final double ir = attr.getInnerRadius();

        final double or = attr.getOuterRadius();

        if ((s > 4) && (ir > 0) && (or > 0) && (or > ir))
        {
            context.beginPath();

            context.moveTo(0, 0 - or);

            for (int n = 1; n < s * 2; n++)
            {
                double radius = n % 2 == 0 ? or : ir;

                context.lineTo(radius * Math.sin(n * Math.PI / s), -1 * radius * Math.cos(n * Math.PI / s));
            }
            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Returns the number of Stars points.
     * 
     * @return int
     */
    public int getStarPoints()
    {
        return getAttributes().getStarPoints();
    }

    /**
     * Sets the number of Star points.
     * 
     * If the value passed is less than 5, it will be replaced by 5.
     * 
     * @param points
     * @return this Star
     */
    public Star setStarPoints(final int points)
    {
        getAttributes().setStarPoints(points);

        return this;
    }

    /**
     * Gets the {@link Star} inner radius.
     * 
     * @return double
     */
    public double getInnerRadius()
    {
        return getAttributes().getInnerRadius();
    }

    /**
     * Sets the {@link Star} inner radius.
     * 
     * @param radius
     * @return this Star
     */
    public Star setInnerRadius(final double radius)
    {
        getAttributes().setInnerRadius(radius);

        return this;
    }

    /**
     * Returns the {@link Star} outer radius.
     * 
     * @return double
     */
    public double getOuterRadius()
    {
        return getAttributes().getOuterRadius();
    }

    /**
     * Sets the outer radius.
     * 
     * @param radius
     * @return this Star
     */
    public Star setOuterRadius(final double radius)
    {
        getAttributes().setOuterRadius(radius);

        return this;
    }

    @Override
    public IFactory<Star> getFactory()
    {
        return new StarFactory();
    }

    public static class StarFactory extends ShapeFactory<Star>
    {
        public StarFactory()
        {
            super(ShapeType.STAR);

            addAttribute(Attribute.STAR_POINTS, true);

            addAttribute(Attribute.INNER_RADIUS, true);

            addAttribute(Attribute.OUTER_RADIUS, true);
        }

        @Override
        public Star create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Star(node, ctx);
        }
    }
}
