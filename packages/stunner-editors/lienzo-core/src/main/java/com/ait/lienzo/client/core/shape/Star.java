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
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ShapeType;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Star is defined by an inner radius, an outer radius and the number of points.
 * The center points is at (0,0) unless additional attributes are set.
 */
@JsType
public class Star extends Shape<Star> {

    private final PathPartList m_list = new PathPartList();

    @JsProperty
    private double cornerRadius;

    @JsProperty
    private int starPoints;

    @JsProperty
    private double innerRadius;

    @JsProperty
    private double outerRadius;

    /**
     * Constructor. Creates an instance of a star.  Visually, there is an enclosing
     * circle which all the tips of the star touch, and an inner circle where all the
     * vertices of the star's arms touch.  The distance between the inner and the outer
     * circle define how long the star's arms are.
     *
     * @param points      number of points in this star.
     * @param innerRadius radius of the inner circle.
     * @param outerRadius radius of the enclosing circle.
     */
    public Star(final int points, final double innerRadius, final double outerRadius) {
        super(ShapeType.STAR);

        setStarPoints(points).setInnerRadius(innerRadius).setOuterRadius(outerRadius);
    }

    @JsIgnore
    public Star(final int points, final double innerRadius, final double outerRadius, final double corner) {
        this(points, innerRadius, outerRadius);

        setCornerRadius(corner);
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (m_list.size() < 1) {
            parse();
        }
        return m_list.getBoundingBox();
    }

    /**
     * Draws this star.
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
        final int sp = getStarPoints();

        final double ir = getInnerRadius();

        final double or = getOuterRadius();

        if ((sp > 4) && (ir > 0) && (or > 0) && (or > ir)) {
            m_list.M(0, 0 - or);

            final int s2 = sp * 2;

            final double corner = getCornerRadius();

            if (corner <= 0) {
                for (int n = 1; n < s2; n++) {
                    final double stheta = (n * Math.PI / sp);

                    final double radius = (((n % 2) == 0) ? or : ir);

                    m_list.L(radius * Math.sin(stheta), -1 * radius * Math.cos(stheta));
                }
                m_list.Z();
            } else {
                final Point2DArray list = Point2DArray.fromArrayOfDouble(0, 0 - or);

                for (int n = 1; n < s2; n++) {
                    final double stheta = (n * Math.PI / sp);

                    final double radius = (((n % 2) == 0) ? or : ir);

                    list.pushXY(radius * Math.sin(stheta), -1 * radius * Math.cos(stheta));
                }
                Geometry.drawArcJoinedLines(m_list, list.pushXY(0, 0 - or), corner);
            }
            return true;
        }
        return false;
    }

    @Override
    public Star refresh() {
        m_list.clear();

        return this;
    }

    /**
     * Returns the number of Stars points.
     *
     * @return int
     */
    public int getStarPoints() {
        return this.starPoints;
    }

    /**
     * Sets the number of Star points.
     * <p>
     * If the value passed is less than 5, it will be replaced by 5.
     *
     * @param points
     * @return this Star
     */
    public Star setStarPoints(int points) {
        if (points < 5) {
            points = 5;
        }
        this.starPoints = points;

        return refresh();
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
    public Star setInnerRadius(final double radius) {
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
    public Star setOuterRadius(final double radius) {
        this.outerRadius = radius;

        return this;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public Star setCornerRadius(final double radius) {
        this.cornerRadius = radius;

        return refresh();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.STAR_POINTS, Attribute.INNER_RADIUS, Attribute.OUTER_RADIUS, Attribute.CORNER_RADIUS);
    }

    @Override
    public PathPartList getPathPartList() {
        if (m_list.size() < 1) {
            if (!parse()) {
                return null;
            }
        }
        if (m_list.size() < 1) {
            return null;
        }
        return m_list;
    }
}
