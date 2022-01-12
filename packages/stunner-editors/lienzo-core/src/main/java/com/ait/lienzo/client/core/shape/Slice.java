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

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ShapeType;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * A Slice is defined by a start angle and an end angle, like a slice of a pizza.
 * The angles can be specified in clockwise or counter-clockwise order.
 * Slices greater than 180 degrees (or PI radians) look like pacmans.
 */
@JsType
public class Slice extends Shape<Slice> {

    @JsProperty
    private double radius;

    @JsProperty
    private double startAngle;

    @JsProperty
    private double endAngle;

    @JsProperty
    private boolean counterClockwise;

    /**
     * Constructor. Creates an instance of a slice.
     *
     * @param radius
     * @param startAngle       in radians
     * @param endAngle         in radians
     * @param counterClockwise
     */
    public Slice(final double radius, final double startAngle, final double endAngle, final boolean counterClockwise) {
        super(ShapeType.SLICE);

        setRadius(radius).setStartAngle(startAngle).setEndAngle(endAngle).setCounterClockwise(counterClockwise);
    }

    /**
     * Constructor. Creates an instance of a slice, drawn clockwise.
     *
     * @param radius
     * @param startAngle in radians
     * @param endAngle   in radians
     */
    @JsIgnore
    public Slice(final double radius, final double startAngle, final double endAngle) {
        this(radius, startAngle, endAngle, false);
    }

    @Override
    public BoundingBox getBoundingBox() {
        final double radius = getRadius();

        return BoundingBox.fromDoubles(0 - radius, 0 - radius, radius, radius);
    }

    /**
     * Draws this slice.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        final double beg = getStartAngle();

        final double end = getEndAngle();

        if (beg == end) {
            return false;
        }
        final double r = getRadius();

        if (r > 0) {
            boolean pacman = true;

            if ((Math.abs(beg - end) % (Math.PI * 2)) == 0) {
                pacman = false;
            }
            context.beginPath();

            context.arc(0, 0, r, beg, end, isCounterClockwise());

            if (pacman) {
                context.lineTo(0, 0);
            }
            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Sets this slice's radius.
     *
     * @param radius
     * @return this Circle
     */
    public Slice setRadius(final double radius) {
        this.radius = radius;

        return this;
    }

    /**
     * Gets this slice's radius.
     *
     * @return double
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Gets the starting angle of this slice.
     *
     * @return double (in radians)
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the starting angle of this slice.
     *
     * @param angle (in radians)
     * @return this slice
     */
    public Slice setStartAngle(final double angle) {
        this.startAngle = angle;

        return this;
    }

    /**
     * Gets the end angle of this slice.
     *
     * @return double (in radians)
     */
    public double getEndAngle() {
        return this.endAngle;
    }

    /**
     * Sets the end angle of this slice.
     *
     * @param angle (in radians)
     * @return this slice
     */
    public Slice setEndAngle(final double angle) {
        this.endAngle = angle;

        return this;
    }

    /**
     * Returns whether the drawing direction of this slice is counter clockwise.
     *
     * @return boolean
     */
    public boolean isCounterClockwise() {
        return this.counterClockwise;
    }

    /**
     * Sets the drawing direction for this slice.
     *
     * @param counterClockwise If true, it's drawn counter clockwise.
     * @return this slice
     */
    public Slice setCounterClockwise(final boolean counterClockwise) {
        this.counterClockwise = counterClockwise;

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.RADIUS, Attribute.START_ANGLE, Attribute.END_ANGLE, Attribute.COUNTER_CLOCKWISE);
    }
}
