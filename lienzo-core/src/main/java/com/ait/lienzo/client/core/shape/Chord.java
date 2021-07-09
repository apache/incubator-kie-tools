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

import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ShapeType;
import jsinterop.annotations.JsProperty;

/**
 * A Chord is defined by a radius, a start angle and an end angle.  Effectively,
 * a chord is a circle with a flat side, which is defined by the start and end angles.
 * The angles can be specified in clockwise or counter-clockwise order.
 */
public class Chord extends Shape<Chord> {

    @JsProperty
    private double radius;

    @JsProperty
    private double startAngle;

    @JsProperty
    private double endAngle;

    @JsProperty
    private boolean counterClockwise;

    /**
     * Constructor. Creates an instance of a chord.
     *
     * @param radius
     * @param startAngle       in radians
     * @param endAngle         in radians
     * @param counterClockwise
     */
    public Chord(final double radius, final double startAngle, final double endAngle, final boolean counterClockwise) {
        super(ShapeType.CHORD);

        setRadius(radius).setStartAngle(startAngle).setEndAngle(endAngle).setCounterClockwise(counterClockwise);
    }

    /**
     * Constructor. Creates an instance of a chord, drawn clockwise.
     *
     * @param radius
     * @param startAngle in radians
     * @param endAngle   in radians
     */
    public Chord(final double radius, final double startAngle, final double endAngle) {
        this(radius, startAngle, endAngle, false);
    }

    /**
     * Draws this chord.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        final double r = getRadius();

        final double beg = getStartAngle();

        final double end = getEndAngle();

        if (r > 0) {
            context.beginPath();

            if (beg == end) {
                context.arc(0, 0, r, 0, Math.PI * 2, true);
            } else {
                context.arc(0, 0, r, beg, end, isCounterClockwise());
            }
            context.closePath();

            return true;
        }
        return false;
    }

    @Override
    public BoundingBox getBoundingBox() {
        final double radius = getRadius();

        return BoundingBox.fromDoubles(0 - radius, 0 - radius, radius, radius);
    }

    /**
     * Sets this chord's radius.
     *
     * @param radius
     * @return this Circle
     */
    public Chord setRadius(final double radius) {
        this.radius = radius;

        return this;
    }

    /**
     * Gets this chord's radius.
     *
     * @return double
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Gets the starting angle of this chord.
     *
     * @return double (in radians)
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the starting angle of this chord.
     *
     * @param angle (in radians)
     * @return this chord
     */
    public Chord setStartAngle(final double angle) {
        this.startAngle = angle;

        return this;
    }

    /**
     * Gets the end angle of this chord.
     *
     * @return double (in radians)
     */
    public double getEndAngle() {
        return this.endAngle;
    }

    /**
     * Sets the end angle of this chord.
     *
     * @param angle (in radians)
     * @return this chord
     */
    public Chord setEndAngle(final double angle) {
        this.endAngle = angle;

        return this;
    }

    /**
     * Returns whether the drawing direction of this chord is counter clockwise.
     *
     * @return boolean
     */
    public boolean isCounterClockwise() {
        return this.counterClockwise;
    }

    /**
     * Sets the drawing direction for this chord.
     *
     * @param counterClockwise If true, it's drawn counter clockwise.
     * @return this chord
     */
    public Chord setCounterClockwise(final boolean counterClockwise) {
        this.counterClockwise = counterClockwise;

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return Arrays.asList(Attribute.RADIUS, Attribute.START_ANGLE, Attribute.END_ANGLE, Attribute.COUNTER_CLOCKWISE);
    }
}
