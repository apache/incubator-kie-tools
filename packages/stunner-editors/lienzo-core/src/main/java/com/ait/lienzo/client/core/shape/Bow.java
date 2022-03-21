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
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Bow shaped shape
 */
@JsType
public class Bow extends Shape<Bow> {

    @JsProperty
    private double innerRadius;

    @JsProperty
    private double outerRadius;

    @JsProperty
    private double startAngle;

    @JsProperty
    private double endAngle;

    @JsProperty
    private boolean counterClockwise;

    /**
     * Constructor. Creates an instance of a bow.
     *
     * @param radius
     * @param startAngle       in radians
     * @param endAngle         in radians
     * @param counterClockwise
     */
    public Bow(final double innerRadius, final double outerRadius, final double startAngle, final double endAngle, final boolean counterClockwise) {
        super(ShapeType.BOW);

        setInnerRadius(innerRadius).setOuterRadius(outerRadius).setStartAngle(startAngle).setEndAngle(endAngle).setCounterClockwise(counterClockwise);
    }

    /**
     * Constructor. Creates an instance of a bow, drawn clockwise.
     *
     * @param radius
     * @param startAngle in radians
     * @param endAngle   in radians
     */
    @JsIgnore
    public Bow(final double innerRadius, final double outerRadius, final double startAngle, final double endAngle) {
        this(innerRadius, outerRadius, startAngle, endAngle, false);
    }

    @Override
    public BoundingBox getBoundingBox() {
        final double radius = Math.max(getInnerRadius(), getOuterRadius());

        return BoundingBox.fromDoubles(0 - radius, 0 - radius, radius, radius);
    }

    /**
     * Draws this bow.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        final double end = getEndAngle();

        final double beg = getStartAngle();

        if (beg == end) {
            return false;
        }
        final double ord = getOuterRadius();

        final double ird = getInnerRadius();

        final boolean ccw = isCounterClockwise();

        if ((ord > 0) && (ird > 0)) {
            context.beginPath();

            context.arc(0, 0, ord, beg, end, ccw);

            context.arc(0, 0, ird, end, beg, (!ccw));

            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Gets the {@link Bow} inner radius.
     *
     * @return double
     */
    public double getInnerRadius() {
        return this.innerRadius;
    }

    /**
     * Sets the {@link Bow} inner radius.
     *
     * @param radius
     * @return this Bow
     */
    public Bow setInnerRadius(final double radius) {
        this.innerRadius = radius;

        return this;
    }

    /**
     * Returns the {@link Bow} outer radius.
     *
     * @return double
     */
    public double getOuterRadius() {
        return this.outerRadius;
    }

    /**
     * Sets the outer radius.
     *
     * @param radius
     * @return this Bow
     */
    public Bow setOuterRadius(final double radius) {
        this.outerRadius = radius;

        return this;
    }

    /**
     * Gets the starting angle of this bow.
     *
     * @return double (in radians)
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the starting angle of this bow.
     *
     * @param angle (in radians)
     * @return this bow
     */
    public Bow setStartAngle(final double angle) {
        this.startAngle = angle;

        return this;
    }

    /**
     * Gets the end angle of this bow.
     *
     * @return double (in radians)
     */
    public double getEndAngle() {
        return this.endAngle;
    }

    /**
     * Sets the end angle of this bow.
     *
     * @param angle (in radians)
     * @return this bow
     */
    public Bow setEndAngle(final double angle) {
        this.endAngle = angle;

        return this;
    }

    /**
     * Returns whether the drawing direction of this bow is counter clockwise.
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
    public Bow setCounterClockwise(final boolean counterClockwise) {
        this.counterClockwise = counterClockwise;

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return Arrays.asList(Attribute.INNER_RADIUS, Attribute.OUTER_RADIUS, Attribute.START_ANGLE, Attribute.END_ANGLE, Attribute.COUNTER_CLOCKWISE);
    }
}
