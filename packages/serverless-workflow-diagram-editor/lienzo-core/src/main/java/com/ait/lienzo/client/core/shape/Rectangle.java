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
 * Rectangle is defined by a width and a height.
 * It may have rounded corners.
 */
@JsType
public class Rectangle extends Shape<Rectangle> {

    @JsProperty
    private double width;

    @JsProperty
    private double height;

    @JsProperty
    private double cornerRadius;

    /**
     * Constructor. Creates an instance of a rectangle.
     *
     * @param width
     * @param height
     */
    public Rectangle(final double width, final double height) {
        super(ShapeType.RECTANGLE);
        setWidth(width).setHeight(height);
    }

    /**
     * Constructor. Creates an instance of rectangle with rounded corners.
     *
     * @param width
     * @param height
     * @param cornerRadius
     */
    @JsIgnore
    public Rectangle(final double width, final double height, final double corner) {
        this(width, height);

        setCornerRadius(corner);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BoundingBox.fromDoubles(0, 0, getWidth(), getHeight());
    }

    /**
     * Draws this rectangle.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        final double w = getWidth();

        final double h = getHeight();

        final double r = getCornerRadius();

        if ((w > 0) && (h > 0)) {
            context.beginPath();

            if ((r > 0) && (r < (w / 2)) && (r < (h / 2))) {
                context.moveTo(r, 0);

                context.lineTo(w - r, 0);

                context.arc(w - r, r, r, Math.PI * 3 / 2, 0, false);

                context.lineTo(w, h - r);

                context.arc(w - r, h - r, r, 0, Math.PI / 2, false);

                context.lineTo(r, h);

                context.arc(r, h - r, r, Math.PI / 2, Math.PI, false);

                context.lineTo(0, r);

                context.arc(r, r, r, Math.PI, Math.PI * 3 / 2, false);
            } else {
                context.rect(0, 0, w, h);
            }
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
     * Sets the width of this rectangle
     *
     * @param width
     * @return this Rectangle
     */
    public Rectangle setWidth(final double width) {
        this.width = width;

        return this;
    }

    /**
     * Gets the height of this rectangle
     *
     * @return double
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Sets the height of this rectangle
     *
     * @param height
     * @return this Rectangle
     */
    public Rectangle setHeight(final double height) {
        this.height = height;

        return this;
    }

    /**
     * Gets the corner radius, if this rectangle has rounded corners.
     *
     * @return double the value returned is 0 if the rectangle has no rounded corners.
     */
    public double getCornerRadius() {
        return this.cornerRadius;
    }

    /**
     * Sets the radius for this rectangle's rounded corners
     *
     * @param radius
     * @return this Rectangle
     */
    public Rectangle setCornerRadius(final double radius) {
        this.cornerRadius = radius;

        return this;
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.WIDTH, Attribute.HEIGHT, Attribute.CORNER_RADIUS);
    }
}
