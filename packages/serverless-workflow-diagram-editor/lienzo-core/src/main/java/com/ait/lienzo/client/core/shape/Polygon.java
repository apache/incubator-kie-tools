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

/**
 * A polygon is traditionally a plane figure that is bounded by a closed path,
 * composed of a finite sequence of straight line segments.
 */
public class Polygon extends AbstractMultiPointShape<Polygon> {

    private double cornerRadius;

    /**
     * Constructor. Creates an instance of a polygon.
     *
     * @param points a {@link Point2DArray} containing 3 or more points
     */
    public Polygon(final Point2DArray points) {
        super(ShapeType.POLYGON);

        setPoints(points);
    }

    public Polygon(final Point2DArray points, final double corner) {
        this(points);

        setCornerRadius(corner);
    }

    public Polygon(final Point2D... points) {
        this(new Point2DArray().push(points));
    }

    public Polygon(double... array) {
        this(Point2DArray.fromArrayOfDouble(array));
    }

    @Override
    public BoundingBox getBoundingBox() {
        return BoundingBox.fromPoint2DArray(getPoints());
    }

    private boolean parse() {
        Point2DArray list = getPoints();

        PathPartList plist = getPathPartList();

        if (null != list) {
            list = list.noAdjacentPoints();

            final int size = list.size();

            if (size > 1) {
                final Point2D point = list.get(0);

                plist.M(point);

                final double corner = getCornerRadius();

                if (corner > 0) {
                    list.push(point);
                    Geometry.drawArcJoinedLines(plist, list, corner);
                } else {
                    for (int i = 1; i < size; i++) {
                        plist.L(list.get(i));
                    }
                    plist.Z();
                }
                return true;
            }
        }
        return false;
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

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public Polygon setCornerRadius(final double radius) {
        this.cornerRadius = radius;

        return refresh();
    }

    @Override
    public Polygon setPoint2DArray(Point2DArray points) {
        return setPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray() {
        return getPoints();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return asAttributes(Attribute.POINTS, Attribute.CORNER_RADIUS);
    }
}
