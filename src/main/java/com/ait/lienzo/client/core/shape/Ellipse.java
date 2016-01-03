/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * Ellipse is defined by a width and a height.
 * The center of the ellipse will be at (0,0) unless
 * it is moved by setting X, Y, OFFSET or TRANSFORM attributes.
 */
public class Ellipse extends Shape<Ellipse>
{
    /**
     * Constructor. Creates an instance of an ellipse.
     * The center of the ellipse will be at (0,0) unless
     * it is moved by setting X, Y, OFFSET or TRANSFORM attributes.
     * 
     * @param width
     * @param height
     */
    public Ellipse(final double width, final double height)
    {
        super(ShapeType.ELLIPSE);

        setWidth(width).setHeight(height);
    }

    protected Ellipse(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ELLIPSE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final double w = getWidth() / 2;

        final double h = getHeight() / 2;

        return new BoundingBox(0 - w, 0 - h, w, h);
    }

    @Override
    protected boolean doStrokeExtraProperties()
    {
        return false;
    }

    /**
     * Draws this ellipse.
     * 
     * @param context the {@link Context2D} used to draw this ellipse.
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final double w = attr.getWidth();

        final double h = attr.getHeight();

        if ((w > 0) && (h > 0))
        {
            context.beginPath();

            context.ellipse(0, 0, w / 2, h / 2, 0, 0, Math.PI * 2, true);

            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Gets this ellipse's width.
     * 
     * @return double
     */
    public double getWidth()
    {
        return getAttributes().getWidth();
    }

    /**
     * Sets this ellipse's width.
     * 
     * @param width
     * @return Ellipse this ellipse
     */
    public Ellipse setWidth(final double width)
    {
        getAttributes().setWidth(width);

        return this;
    }

    /**
     * Gets this ellipse's height.
     * 
     * @return double
     */
    public double getHeight()
    {
        return getAttributes().getHeight();
    }

    /**
     * Sets this ellipse's height.
     * 
     * @param height
     * @return Ellipse this ellipse
     */
    public Ellipse setHeight(final double height)
    {
        getAttributes().setHeight(height);

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes()
    {
        return Arrays.asList(Attribute.WIDTH, Attribute.HEIGHT);
    }

    public static class EllipseFactory extends ShapeFactory<Ellipse>
    {
        public EllipseFactory()
        {
            super(ShapeType.ELLIPSE);

            addAttribute(Attribute.WIDTH, true);

            addAttribute(Attribute.HEIGHT, true);
        }

        @Override
        public Ellipse create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Ellipse(node, ctx);
        }
    }
}