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
import jsinterop.annotations.JsType;

/**
 * Ellipse is defined by a width and a height.
 * The center of the ellipse will be at (0,0) unless
 * it is moved by setting X, Y, OFFSET or TRANSFORM attributes.
 */
@JsType
public class Ellipse extends Shape<Ellipse> {

    @JsProperty
    private double width;

    @JsProperty
    private double height;

    /**
     * Constructor. Creates an instance of an ellipse.
     * The center of the ellipse will be at (0,0) unless
     * it is moved by setting X, Y, OFFSET or TRANSFORM attributes.
     *
     * @param width
     * @param height
     */
    public Ellipse(final double width, final double height) {
        super(ShapeType.ELLIPSE);

        setWidth(width).setHeight(height);
    }

    @Override
    public BoundingBox getBoundingBox() {
        final double w = getWidth() / 2;

        final double h = getHeight() / 2;

        return BoundingBox.fromDoubles(0 - w, 0 - h, w, h);
    }

    @Override
    protected boolean doStrokeExtraProperties() {
        return false;
    }

    /**
     * Draws this ellipse.
     *
     * @param context the {@link Context2D} used to draw this ellipse.
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        final double w = getWidth();

        final double h = getHeight();

        if ((w > 0) && (h > 0)) {
            context.beginPath();

            context.ellipse(0, 0, w / 2, h / 2, 0, 0, Math.PI * 2, true);

            context.closePath();

            return true;
        }
        return false;
    }

    /**
     * Gets the width of this parallelogram
     *
     * @return double
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Sets the width of this ellipse
     *
     * @param width
     * @return this ellipse
     */
    public Ellipse setWidth(final double width) {
        this.width = width;

        return this;
    }

    /**
     * Gets the height of this ellipse
     *
     * @return double
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Sets the height of this ellipse
     *
     * @param height
     * @return this ellipse
     */
    public Ellipse setHeight(final double height) {
        this.height = height;

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.WIDTH, Attribute.HEIGHT);
    }
}