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
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import jsinterop.annotations.JsProperty;

/**
 * Parallelogram defined by a width, a height and a skew factor.
 * A skew of 0 draws sides that form a 90 degree angle.
 */
public class Parallelogram extends Shape<Parallelogram> {

    @JsProperty
    private double width;

    @JsProperty
    private double height;

    @JsProperty
    private double cornerRadius;

    @JsProperty
    private double skew;

    private final PathPartList m_list = new PathPartList();

    /**
     * Constructor. Creates an instance of a parallelogram.
     *
     * @param width
     * @param height
     * @param skew   a skew of 0 draws sides that form a 90 degree angle
     */
    public Parallelogram(final double width, final double height, final double skew) {
        super(ShapeType.PARALLELOGRAM);

        setWidth(width).setHeight(height).setSkew(skew);
    }

    public Parallelogram(final double width, final double height, final double skew, final double corner) {
        this(width, height, skew);

        setCornerRadius(corner);
    }

    /**
     * Draws this parallelogram.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        if (m_list.size() < 1) {
            if (!parse()) {
                return false;
            }
        }
        if (m_list.size() < 1) {
            return false;
        }
        context.path(m_list);

        return true;
    }

    private boolean parse() {
        final double wide = getWidth();

        final double high = getHeight();

        if ((wide > 0) && (high > 0)) {
            final double skew = getSkew();

            final Point2DArray list = new Point2DArray();

            if (skew >= 0) {
                list.pushXY(skew, 0);

                list.pushXY(wide, 0);

                list.pushXY(wide - skew, high);

                list.pushXY(0, high);
            } else {
                list.pushXY(0, 0);

                list.pushXY(wide - Math.abs(skew), 0);

                list.pushXY(wide, high);

                list.pushXY(Math.abs(skew), high);
            }
            final Point2D p0 = list.get(0);

            m_list.M(p0);

            final double corner = getCornerRadius();

            if (corner > 0) {
                list.push(p0);
                Geometry.drawArcJoinedLines(m_list, list, corner);
            } else {
                final int size = list.size();

                for (int i = 1; i < size; i++) {
                    m_list.L(list.get(i));
                }
                m_list.Z();
            }
            return true;
        }
        return false;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BoundingBox.fromDoubles(0, 0, getWidth(), getHeight());
    }

    @Override
    public Parallelogram refresh() {
        m_list.clear();

        return this;
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
     * Sets the width of this parallelogram
     *
     * @param width
     * @return this Parallelogram
     */
    public Parallelogram setWidth(final double width) {
        this.width = width;

        return refresh();
    }

    /**
     * Gets the height of this parallelogram
     *
     * @return double
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Sets the height of this parallelogram
     *
     * @param height
     * @return this Parallelogram
     */
    public Parallelogram setHeight(final double height) {
        this.height = height;

        return refresh();
    }

    /**
     * Gets the skew of this parallelogram.
     *
     * @return double
     */
    public double getSkew() {
        return this.skew;
    }

    /**
     * Sets the skew of this parallelogram
     *
     * @param skew
     * @return this Parallelogram
     */
    public Parallelogram setSkew(final double skew) {
        this.skew = skew;

        return refresh();
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public Parallelogram setCornerRadius(final double radius) {
        this.cornerRadius = radius;

        return refresh();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.WIDTH, Attribute.HEIGHT, Attribute.SKEW, Attribute.CORNER_RADIUS);
    }
}
