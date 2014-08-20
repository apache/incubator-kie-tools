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
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * Ellipse is defined by a width and a height.
 * The center of the ellipse will be at (0,0) unless
 * it is moved by setting X, Y, OFFSET or TRANSFORM attributes.
 */
public class Ellipse extends Shape<Ellipse>
{
    private static final double KAPPA = .5522848;

    /**
     * Constructor. Creates an instance of an ellipse.
     * The center of the ellipse will be at (0,0) unless
     * it is moved by setting X, Y, OFFSET or TRANSFORM attributes.
     * 
     * @param width
     * @param height
     */
    public Ellipse(double width, double height)
    {
        super(ShapeType.ELLIPSE);

        setWidth(width).setHeight(height);
    }

    protected Ellipse(JSONObject node, ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.ELLIPSE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final double w = getWidth();

        final double h = getHeight();

        return new BoundingBox(0 - (w / 2), 0 - (h / 2), w, h);
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
    public boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        final double w = getWidth();

        final double h = getHeight();

        if ((w > 0) && (h > 0))
        {
            double x = -(w / 2);

            double y = -(h / 2);

            double ox = (w / 2) * KAPPA; // control point offset horizontal

            double oy = (h / 2) * KAPPA; // control point offset vertical

            double xe = x + w; // x-end

            double ye = y + h; // y-end

            double xm = x + w / 2; // x-middle

            double ym = y + h / 2; // y-middle

            context.beginPath();

            context.moveTo(x, ym);

            context.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);

            context.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);

            context.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);

            context.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);

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
    public Ellipse setWidth(double width)
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
    public Ellipse setHeight(double height)
    {
        getAttributes().setHeight(height);

        return this;
    }

    @Override
    public IFactory<Ellipse> getFactory()
    {
        return new EllipseFactory();
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
        public Ellipse create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new Ellipse(node, ctx);
        }
    }
}