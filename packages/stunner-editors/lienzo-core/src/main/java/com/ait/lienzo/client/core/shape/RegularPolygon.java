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
import jsinterop.annotations.JsProperty;

/**
 * In Euclidean geometry, a regular polygon is a polygon that is equiangular (all angles are equal in measure)
 * and equilateral (all sides have the same length).  All regular polygons fit perfectly inside a circle.
 */
public class RegularPolygon extends Shape<RegularPolygon> {

    @JsProperty
    private double radius;

    @JsProperty
    private double cornerRadius;

    @JsProperty
    private int sides;

    private final PathPartList m_list = new PathPartList();

    /**
     * Constructor. Creates an instance of a regular polygon.
     *
     * @param sides  number of sides
     * @param radius size of the encompassing circle
     */
    public RegularPolygon(final int sides, final double radius) {
        super(ShapeType.REGULAR_POLYGON);

        setRadius(radius).setSides(sides);
    }

    public RegularPolygon(final int sides, final double radius, final double corner) {
        this(sides, radius);

        setCornerRadius(corner);
    }

    @Override
    public BoundingBox getBoundingBox() {
        final int s = getSides();

        final double r = getRadius();

        double minx = 0;

        double miny = 0;

        double maxx = 0;

        double maxy = 0;

        if ((s > 2) && (r > 0)) {
            minx = maxx = 0;

            miny = maxy = 0 - r;

            for (int n = 1; n < s; n++) {
                double x = (r * Math.sin(n * 2 * Math.PI / s));

                double y = (-1 * r * Math.cos(n * 2 * Math.PI / s));

                minx = Math.min(minx, x);

                miny = Math.min(miny, y);

                maxx = Math.max(maxx, x);

                maxy = Math.max(maxy, y);
            }
        }
        return BoundingBox.fromDoubles(minx, miny, maxx, maxy);
    }

    /**
     * Draws this regular polygon
     *
     * @context
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
        final int sides = getSides();

        final double radius = getRadius();

        if ((sides > 2) && (radius > 0)) {
            m_list.M(0, 0 - radius);

            final double corner = getCornerRadius();

            if (corner <= 0) {
                for (int n = 1; n < sides; n++) {
                    final double theta = (n * 2 * Math.PI / sides);

                    m_list.L(radius * Math.sin(theta), -1 * radius * Math.cos(theta));
                }
                m_list.Z();
            } else {
                final Point2DArray list = new Point2DArray().pushXY(0, 0 - radius);

                for (int n = 1; n < sides; n++) {
                    final double theta = (n * 2 * Math.PI / sides);

                    list.pushXY(radius * Math.sin(theta), -1 * radius * Math.cos(theta));
                }
                Geometry.drawArcJoinedLines(m_list, list.pushXY(0, 0 - radius), corner);
            }
            return true;
        }
        return false;
    }

    @Override
    public RegularPolygon refresh() {
        m_list.clear();

        return this;
    }

    /**
     * Gets this regular polygon's encompassing circle's radius.
     *
     * @return double
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Sets the size of this regular polygon, expressed by the radius of the enclosing circle.
     *
     * @param radius
     * @return this RegularPolygon
     */
    public RegularPolygon setRadius(final double radius) {
        this.radius = radius;

        return refresh();
    }

    /**
     * Gets the number of sides this regular polygon has.
     *
     * @return int
     */
    public int getSides() {
        return this.sides;
    }

    /**
     * Sets the number of sides this regular polygon has.
     *
     * @param sides
     * @return this RegularPolygon
     */
    public RegularPolygon setSides(final int sides) {
        if (sides < 3) {
            throw new IllegalArgumentException("Cannot have less than 3 sides");
        }
        this.sides = sides;

        return refresh();
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public RegularPolygon setCornerRadius(final double radius) {
        this.cornerRadius = radius;

        return refresh();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.RADIUS, Attribute.SIDES, Attribute.CORNER_RADIUS);
    }
}
