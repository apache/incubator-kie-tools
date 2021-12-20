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
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * A triangle is one of the basic shapes of geometry: a polygon with three corners or vertices and three sides
 * or edges which are line segments.
 * In Euclidean geometry any three points, when non-collinear, determine a unique triangle and a unique plane
 * (i.e. a two-dimensional Euclidean space).
 */
@JsType
public class Triangle extends AbstractMultiPointShape<Triangle> {

    @JsProperty
    private double cornerRadius;

    /**
     * Constructor. Creates an instance of a triangle.
     *
     * @param 3 points {@link Point2D}
     */
    public Triangle(final Point2D a, final Point2D b, final Point2D c) {
        super(ShapeType.TRIANGLE);

        setPoints(a, b, c);
    }

    @JsIgnore
    public Triangle(final Point2D a, final Point2D b, final Point2D c, final double corner) {
        this(a, b, c);

        setCornerRadius(corner);
    }

    /**
     * Sets this triangles points.
     *
     * @param 3 points {@link Point2D}
     * @return this Triangle
     */
    public Triangle setPoints(final Point2D a, final Point2D b, final Point2D c) {
        return setPoint2DArray(Point2DArray.fromArrayOfPoint2D(a, b, c));
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BoundingBox.fromPoint2DArray(getPoints());
    }

    /**
     * Draws this polygon.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        PathPartList plist = getPathPartList();
        if (plist.size() < 1) {
            if (!parse()) {
                return false;
            }
        }
        if (plist.size() < 1) {
            return false;
        }
        context.path(plist);

        return true;
    }

    private boolean parse() {
        final Point2DArray list = getPoints().noAdjacentPoints();

        PathPartList plist = getPathPartList();

        if ((null != list) && (list.size() > 2)) {
            final Point2D p0 = list.get(0);

            plist.M(p0);

            final double corner = getCornerRadius();

            if (corner <= 0) {
                plist.L(list.get(1));

                plist.L(list.get(2));

                plist.Z();
            } else {
                list.push(p0);
                Geometry.drawArcJoinedLines(plist, list, corner);
            }
            return true;
        }
        return false;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public Triangle setCornerRadius(final double radius) {
        this.cornerRadius = radius;

        return refresh();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.POINTS, Attribute.CORNER_RADIUS);
    }
}
