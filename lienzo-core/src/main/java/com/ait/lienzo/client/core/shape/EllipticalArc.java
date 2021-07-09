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
import jsinterop.annotations.JsProperty;

/**
 * Arcs are defined by a center point, a radius, a starting angle, an ending angle, and the drawing direction (either clockwise or counterclockwise).
 */
public class EllipticalArc extends Shape<EllipticalArc> {

    @JsProperty
    private double radiusX;

    @JsProperty
    private double radiusY;

    @JsProperty
    private double startAngle;

    @JsProperty
    private double endAngle;

    @JsProperty
    private boolean counterClockwise;

    /**
     * Constructor. Creates an instance of an arc.
     *
     * @param radius           radius of the circle
     * @param startAngle       starting angle (in radians) of this arc
     * @param endAngle         end angle (in radians) of this arc
     * @param counterClockwise direction in which the arc is drawn.  True draws the arc counter clockwise;
     *                         false draws the arc clockwise.
     */
    public EllipticalArc(final double radiusX, final double radiusY, final double startAngle, final double endAngle, final boolean counterClockwise) {
        super(ShapeType.ELLIPTICAL_ARC);

        setRadiusX(radiusX).setRadiusY(radiusY).setStartAngle(startAngle).setEndAngle(endAngle).setCounterClockwise(counterClockwise);
    }

    /**
     * Constructor. Creates an instance of an arc, drawn clockwise.
     *
     * @param radius     radius of the circle
     * @param startAngle starting angle (in radians) of this arc
     * @param endAngle   end angle (in radians) of this arc
     */
    public EllipticalArc(final double radiusX, final double radiusY, final double startAngle, final double endAngle) {
        super(ShapeType.ELLIPTICAL_ARC);

        setRadiusX(radiusX).setRadiusY(radiusY).setStartAngle(startAngle).setEndAngle(endAngle).setCounterClockwise(false);
    }

    @Override
    public BoundingBox getBoundingBox() {
        final double rx = getRadiusX();

        final double ry = getRadiusY();

        return BoundingBox.fromDoubles(0 - rx, 0 - ry, rx, ry);
    }

    /**
     * Draws this arc.
     *
     * @param context the {@link Context2D} used to draw this arc.
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        final double rx = getRadiusX();

        final double ry = getRadiusY();

        if ((rx > 0) && (ry > 0)) {
            context.beginPath();

            context.ellipse(0, 0, rx, ry, 0, getStartAngle(), getEndAngle(), isCounterClockwise());

            return true;
        }
        return false;
    }

    /**
     * Sets this arc's X radius.
     *
     * @param radiusX
     * @return this Circle
     */
    public EllipticalArc setRadiusX(final double radiusX) {
        this.radiusX = radiusX;

        return this;
    }

    /**
     * Gets this arc's X radius.
     *
     * @return double
     */
    public double getRadiusY() {
        return this.radiusY;
    }

    /**
     * Sets this arc's X radius.
     *
     * @param radiusY
     * @return this Circle
     */
    public EllipticalArc setRadiusY(final double radiusY) {
        this.radiusY = radiusY;

        return this;
    }

    /**
     * Gets this arc's X radius.
     *
     * @return double
     */
    public double getRadiusX() {
        return this.radiusX;
    }

    /**
     * Gets the starting angle of this arc.
     *
     * @return double (in radians)
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the starting angle of this arc.
     *
     * @param angle (in radians)
     * @return this Arc
     */
    public EllipticalArc setStartAngle(final double angle) {
        this.startAngle = angle;

        return this;
    }

    /**
     * Gets the end angle of this arc.
     *
     * @return double (in radians)
     */
    public double getEndAngle() {
        return this.endAngle;
    }

    /**
     * Sets the end angle of this arc.
     *
     * @param angle (in radians)
     * @return this Arc
     */
    public EllipticalArc setEndAngle(final double angle) {
        this.endAngle = angle;

        return this;
    }

    /**
     * Returns whether the drawing direction of this arc is counter clockwise.
     *
     * @return boolean
     */
    public boolean isCounterClockwise() {
        return this.counterClockwise;
    }

    /**
     * Sets the drawing direction for this arc.
     *
     * @param counterClockwise If true, it's drawn counter clockwise.
     * @return this Arc
     */
    public EllipticalArc setCounterClockwise(final boolean counterClockwise) {
        this.counterClockwise = counterClockwise;

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.RADIUS_X, Attribute.RADIUS_Y, Attribute.START_ANGLE, Attribute.END_ANGLE, Attribute.COUNTER_CLOCKWISE);
    }
}
