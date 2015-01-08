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
 * Parallelogram defined by a width, a height and a skew factor.
 * A skew of 0 draws sides that form a 90 degree angle.
 */
public class Parallelogram extends Shape<Parallelogram>
{
    /**
     * Constructor. Creates an instance of a parallelogram.
     * 
     * @param width
     * @param height
     * @param skew a skew of 0 draws sides that form a 90 degree angle
     */
    public Parallelogram(final double width, final double height, final double skew)
    {
        super(ShapeType.PARALLELOGRAM);

        setWidth(width).setHeight(height).setSkew(skew);
    }

    protected Parallelogram(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.PARALLELOGRAM, node, ctx);
    }

    /**
     * Draws this parallelogram.
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final double wide = attr.getWidth();

        final double high = attr.getHeight();

        if ((wide > 0) && (high > 0))
        {
            final double skew = attr.getSkew();

            context.beginPath();

            if (skew > 0)
            {
                context.moveTo(skew, 0);

                context.lineTo(wide, 0);

                context.lineTo(wide - skew, high);

                context.lineTo(0, high);
            }
            else if (skew < 0)
            {
                context.moveTo(0, 0);

                context.lineTo(wide - Math.abs(skew), 0);

                context.lineTo(wide, high);

                context.lineTo(Math.abs(skew), high);
            }
            else
            {
                context.rect(0, 0, wide, high);
            }
            context.closePath();

            return true;
        }
        return false;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(0, 0, getWidth(), getHeight());
    }

    /**
     * Gets the width of this parallelogram
     * 
     * @return double
     */
    public double getWidth()
    {
        return getAttributes().getWidth();
    }

    /**
     * Sets the width of this parallelogram
     * 
     * @param width
     * @return this Parallelogram
     */
    public Parallelogram setWidth(final double width)
    {
        getAttributes().setWidth(width);

        return this;
    }

    /**
     * Gets the height of this parallelogram
     * 
     * @return double
     */
    public double getHeight()
    {
        return getAttributes().getHeight();
    }

    /**
     * Sets the height of this parallelogram
     * 
     * @param height
     * @return this Parallelogram
     */
    public Parallelogram setHeight(final double height)
    {
        getAttributes().setHeight(height);

        return this;
    }

    /**
     * Gets the skew of this parallelogram.
     * 
     * @return double
     */
    public double getSkew()
    {
        return getAttributes().getSkew();
    }

    /**
     * Sets the skew of this parallelogram
     * 
     * @param skew
     * @return this Parallelogram
     */
    public Parallelogram setSkew(final double skew)
    {
        getAttributes().setSkew(skew);

        return this;
    }

    @Override
    public IFactory<Parallelogram> getFactory()
    {
        return new ParallelogramFactory();
    }

    public static class ParallelogramFactory extends ShapeFactory<Parallelogram>
    {
        public ParallelogramFactory()
        {
            super(ShapeType.PARALLELOGRAM);

            addAttribute(Attribute.WIDTH, true);

            addAttribute(Attribute.HEIGHT, true);

            addAttribute(Attribute.SKEW, true);
        }

        @Override
        public Parallelogram create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Parallelogram(node, ctx);
        }
    }
}
