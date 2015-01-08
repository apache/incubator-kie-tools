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
 * Circle with a radius. The center point is set via the X,Y attributes.
 */
public class Circle extends Shape<Circle>
{
    /**
     * Constructor. Creates an instance of a circle.
     * 
     * @param radius
     */
    public Circle(final double radius)
    {
        super(ShapeType.CIRCLE);

        setRadius(radius);
    }

    protected Circle(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.CIRCLE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final double radius = getRadius();

        return new BoundingBox(0 - radius, 0 - radius, radius, radius);
    }

    /**
     * Draws this circle
     * 
     * @param context the {@link Context2D} used to draw this circle. 
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final double r = attr.getRadius();

        if (r > 0)
        {
            context.beginPath();

            context.arc(0, 0, r, 0, Math.PI * 2, true);

            context.closePath();

            return true;
        }
        return false;
    }

    @Override
    protected boolean doStrokeExtraProperties()
    {
        return false;
    }

    /**
     * Sets this circle's radius.
     * 
     * @param radius
     * @return this Circle
     */
    public Circle setRadius(final double radius)
    {
        getAttributes().setRadius(radius);

        return this;
    }

    /**
     * Gets this circle's radius.
     * 
     * @return double
     */
    public double getRadius()
    {
        return getAttributes().getRadius();
    }

    @Override
    public IFactory<Circle> getFactory()
    {
        return new CircleFactory();
    }

    public static class CircleFactory extends ShapeFactory<Circle>
    {
        public CircleFactory()
        {
            super(ShapeType.CIRCLE);

            addAttribute(Attribute.RADIUS, true);
        }

        @Override
        public Circle create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Circle(node, ctx);
        }
    }
}
