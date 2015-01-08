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
 * A Chord is defined by a radius, a start angle and an end angle.  Effectively,
 * a chord is a circle with a flat side, which is defined by the start and end angles.
 * The angles can be specified in clockwise or counter-clockwise order.
 */
public class Chord extends Shape<Chord>
{
    /**
     * Constructor. Creates an instance of a chord.
     * 
     * @param radius
     * @param startAngle in radians
     * @param endAngle in radians
     * @param counterClockwise
     */
    public Chord(final double radius, final double startAngle, final double endAngle, final boolean counterClockwise)
    {
        super(ShapeType.CHORD);

        setRadius(radius).setStartAngle(startAngle).setEndAngle(endAngle).setCounterClockwise(counterClockwise);
    }

    /**
     * Constructor. Creates an instance of a chord, drawn clockwise.
     * 
     * @param radius
     * @param startAngle in radians
     * @param endAngle in radians
     */
    public Chord(final double radius, final double startAngle, final double endAngle)
    {
        this(radius, startAngle, endAngle, false);
    }

    protected Chord(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.CHORD, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        final double radius = getRadius();

        return new BoundingBox(0 - radius, 0 - radius, radius, radius);
    }

    /**
     * Draws this chord.
     * 
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        final double r = attr.getRadius();

        final double beg = attr.getStartAngle();

        final double end = attr.getEndAngle();

        if (r > 0)
        {
            context.beginPath();

            if (beg == end)
            {
                context.arc(0, 0, r, 0, Math.PI * 2, true);
            }
            else
            {
                context.arc(0, 0, r, beg, end, attr.isCounterClockwise());
            }
            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Gets this chord's radius
     * 
     * @return double
     */
    public double getRadius()
    {
        return getAttributes().getRadius();
    }

    /**
     * Sets this chord's radius.
     * 
     * @param radius
     * @return this chord.
     */
    public Chord setRadius(final double radius)
    {
        getAttributes().setRadius(radius);

        return this;
    }

    /**
     * Gets the starting angle of this chord.
     * 
     * @return double in radians
     */
    public double getStartAngle()
    {
        return getAttributes().getStartAngle();
    }

    /**
     * Sets the starting angle of this chord.
     * 
     * @param angle in radians
     * @return this chord.
     */
    public Chord setStartAngle(final double angle)
    {
        getAttributes().setStartAngle(angle);

        return this;
    }

    /**
     * Gets the end angle of this chord.
     * 
     * @return double in radians
     */
    public double getEndAngle()
    {
        return getAttributes().getEndAngle();
    }

    /**
     * Gets the end angle of this chord.
     * 
     * @param angle in radians
     * @return this chord.
     */
    public Chord setEndAngle(final double angle)
    {
        getAttributes().setEndAngle(angle);

        return this;
    }

    /**
     * Returns whether the chord is drawn counter clockwise.
     * The default value is true.
     * 
     * @return boolean
     */
    public boolean isCounterClockwise()
    {
        return getAttributes().isCounterClockwise();
    }

    /**
     * Sets whether the drawing direction of this chord is counter clockwise.
     * The default value is true.
     * 
     * @param counterclockwise
     * @return this chord
     */
    public Chord setCounterClockwise(final boolean counterclockwise)
    {
        getAttributes().setCounterClockwise(counterclockwise);

        return this;
    }

    @Override
    public IFactory<Chord> getFactory()
    {
        return new ChordFactory();
    }

    public static class ChordFactory extends ShapeFactory<Chord>
    {
        public ChordFactory()
        {
            super(ShapeType.CHORD);

            addAttribute(Attribute.RADIUS, true);

            addAttribute(Attribute.START_ANGLE, true);

            addAttribute(Attribute.END_ANGLE, true);

            addAttribute(Attribute.COUNTER_CLOCKWISE);
        }

        @Override
        public Chord create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new Chord(node, ctx);
        }
    }
}
