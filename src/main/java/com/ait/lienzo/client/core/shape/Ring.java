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
 * A Slice is defined by a start angle and an end angle, like a slice of a pizza.
 * The angles can be specified in clockwise or counter-clockwise order.
 * Slices greater than 180 degrees (or PI radians) look like pacmans.
 */
public class Ring extends Shape<Ring>
{
    /**
     * Constructor. Creates an instance of a slice.
     * 
     * @param radius
     * @param startAngle in radians
     * @param endAngle in radians
     * @param counterClockwise
     */
    public Ring(final double innerRadius, final double outerRadius)
    {
        super(ShapeType.RING);

        setInnerRadius(innerRadius).setOuterRadius(outerRadius);
    }

    protected Ring(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.RING, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final double radius = Math.max(getInnerRadius(), getOuterRadius());

        return new BoundingBox(0 - radius, 0 - radius, radius, radius);
    }

    @Override
    protected boolean doStrokeExtraProperties()
    {
        return false;
    }

    /**
     * Draws this slice.
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final double ord = attr.getOuterRadius();

        final double ird = attr.getInnerRadius();

        if ((ord > 0) && (ird > 0) && (ord > ird))
        {
            context.beginPath();

            context.arc(0, 0, ord, 0, Math.PI * 2, false);

            context.arc(0, 0, ird, 0, Math.PI * 2, true);

            context.closePath();

            return true;
        }
        return false;
    }

    @Override
    protected void stroke(final Context2D context, final Attributes attr, final double alpha)
    {
        context.save();

        if (setStrokeParams(context, attr, alpha))
        {
            if (context.isSelection())
            {
                context.beginPath();

                context.arc(0, 0, attr.getOuterRadius(), 0, Math.PI * 2, false);

                context.closePath();

                context.stroke();

                context.beginPath();

                context.arc(0, 0, attr.getInnerRadius(), 0, Math.PI * 2, true);

                context.closePath();

                context.stroke();

                context.restore();

                return;
            }
            doApplyShadow(context, attr);

            context.beginPath();

            context.arc(0, 0, attr.getOuterRadius(), 0, Math.PI * 2, false);

            context.closePath();

            context.stroke();

            context.beginPath();

            context.arc(0, 0, attr.getInnerRadius(), 0, Math.PI * 2, true);

            context.closePath();

            context.stroke();
        }
        context.restore();
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
    public Ring setInnerRadius(final double radius)
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
    public Ring setOuterRadius(final double radius)
    {
        getAttributes().setOuterRadius(radius);

        return this;
    }

    @Override
    public IFactory<Ring> getFactory()
    {
        return new RingFactory();
    }

    public static class RingFactory extends ShapeFactory<Ring>
    {
        public RingFactory()
        {
            super(ShapeType.RING);

            addAttribute(Attribute.INNER_RADIUS, true);

            addAttribute(Attribute.OUTER_RADIUS, true);
        }

        @Override
        public Ring create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Ring(node, ctx);
        }
    }
}
