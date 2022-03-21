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
 * A Slice is defined by a start angle and an end angle, like a slice of a pizza.
 * The angles can be specified in clockwise or counter-clockwise order.
 * Slices greater than 180 degrees (or PI radians) look like pacmans.
 */
public class Ring extends Shape<Ring> {

    @JsProperty
    private double innerRadius;

    @JsProperty
    private double outerRadius;

    /**
     * Constructor. Creates an instance of a slice.
     */
    public Ring(final double innerRadius, final double outerRadius) {
        super(ShapeType.RING);

        setInnerRadius(innerRadius).setOuterRadius(outerRadius);
    }

    @Override
    public BoundingBox getBoundingBox() {
        final double radius = Math.max(getInnerRadius(), getOuterRadius());

        return BoundingBox.fromDoubles(0 - radius, 0 - radius, radius, radius);
    }

    @Override
    protected boolean doStrokeExtraProperties() {
        return false;
    }

    /**
     * Draws this slice.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        final double ord = getOuterRadius();

        final double ird = getInnerRadius();

        if ((ord > 0) && (ird > 0) && (ord > ird)) {
            context.beginPath();

            context.arc(0, 0, ord, 0, Math.PI * 2, false);

            context.arc(0, 0, ird, 0, Math.PI * 2, true);

            context.closePath();

            return true;
        }
        return false;
    }

    @Override
    protected void stroke(final Context2D context, final double alpha, final boolean filled) {
        if (setStrokeParams(context, alpha, filled)) {
            if (getShadow() != null && !context.isSelection()) {
                doApplyShadow(context);
            }
            context.beginPath();

            context.arc(0, 0, getOuterRadius(), 0, Math.PI * 2, false);

            context.closePath();

            context.stroke();

            context.beginPath();

            context.arc(0, 0, getInnerRadius(), 0, Math.PI * 2, true);

            context.closePath();

            context.stroke();

            context.restore();
        }
    }

    /**
     * Gets the {@link Star} inner radius.
     *
     * @return double
     */
    public double getInnerRadius() {
        return this.innerRadius;
    }

    /**
     * Sets the {@link Star} inner radius.
     *
     * @param radius
     * @return this Star
     */
    public Ring setInnerRadius(final double radius) {
        this.innerRadius = radius;

        return this;
    }

    /**
     * Returns the {@link Star} outer radius.
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
     * @return this Star
     */
    public Ring setOuterRadius(final double radius) {
        this.outerRadius = radius;

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.INNER_RADIUS, Attribute.OUTER_RADIUS);
    }
}
